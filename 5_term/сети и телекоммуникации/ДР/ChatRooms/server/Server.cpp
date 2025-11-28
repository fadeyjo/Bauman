#include "Server.h"

Server::Server(int PORT)
{
    server = socket(AF_INET, SOCK_STREAM, 0);
    if (server == -1)
    {
        perror("Socket creation failed: ");
        return;
    }

    socketSettings.sin_family = AF_INET;
    socketSettings.sin_addr.s_addr = INADDR_ANY;
    socketSettings.sin_port = htons(PORT);

    this->PORT = PORT;

    if (bind(server, reinterpret_cast<sockaddr *>(&socketSettings), sizeof(socketSettings)) == -1)
    {
        perror("Bind failed: ");
        return;
    }

    if (listen(server, 5) == -1)
    {
        perror("Listen failed");
        return;
    }

    db = new DB("host=localhost port=5432 dbname=rooms user=postgres password=123");
}

void Server::startListening()
{
    std::cout << "Server listening on port = " << PORT << ".\n"
              << "Enter command: " << std::flush;

    while (true)
    {
        sockaddr_in clientAddr{};
        socklen_t clientLen = sizeof(clientAddr);

        int client = accept(server, reinterpret_cast<sockaddr *>(&clientAddr), &clientLen);
        if (client == -1)
        {
            perror("Client accept failed: ");
            continue;
        }

        std::thread(&Server::handleConnections, this, client).detach();
    }
}

void Server::handleConnections(int client)
{
    char buffer[BUFFER_SIZE];

    int bytesRead = recv(client, buffer, sizeof(buffer) - 1, 0);
    if (bytesRead == -1)
    {
        std::cerr << "Failed to receive data or connection closed." << std::endl;
        close(client);
        return;
    }

    if (bytesRead == 0)
    {
        std::cerr << "Connection closed by client." << std::endl;
        close(client);
        return;
    }

    buffer[bytesRead] = '\0';
    std::string data(buffer);

    std::string action = readRowFromChannel(data);

    if (action == "new_user")
    {
        std::string nickname = readRowFromChannel(data);
        if (db->userExists(nickname))
        {
            std::string error = "User already exists\n";
            send(client, error.c_str(), error.size(), 0);
            return;
        }
        if (!db->addUser(nickname))
        {
            std::string error = "Error with adding user\n";
            send(client, error.c_str(), error.size(), 0);
            return;
        }
        std::string ok = "New user was successfully added\n";
        send(client, ok.c_str(), ok.size(), 0);
    }
    else if (action == "get_rooms")
    {
        std::string nickname = readRowFromChannel(data);
        std::thread(&Server::handleRoomsList, this, client, nickname).detach();
    }
    else if (action == "get_messages")
    {
        std::string roomName = readRowFromChannel(data);
        std::string nickname = readRowFromChannel(data);
        std::thread(&Server::handleRoom, this, client, roomName, nickname).detach();
    }
    else if (action == "get_info_for_admin")
    {
        std::thread(&Server::handleAdmin, this, client).detach();
    }
}

void Server::handleAdmin(int admin)
{
    if (this->admin != nullptr)
    {
        std::cerr << "Admin already on server";
        return;
    }

    this->admin = new int(admin);

    std::set<std::string> clients;

    {
        std::lock_guard<std::mutex> lock(clientsMutex);

        for (Client &clientInRoomList : clientsInRoomsList)
        {
            clients.insert(clientInRoomList.nickname);
        }

        std::set<std::string> clientsRoomsSet = clientsRooms.getSet();

        clients.insert(clientsRoomsSet.begin(), clientsRoomsSet.end());

        std::string onlineUsersResult = "";

        for (auto &client : clients)
        {
            onlineUsersResult += "online_users\n" + client + '\n';
        }

        send(admin, onlineUsersResult.c_str(), onlineUsersResult.size(), 0);

        std::vector<std::string> roomsNames = db->getRoomsNames();

        std::string roomsNamesResult = "";
        for (std::string &roomName : roomsNames)
        {
            roomsNamesResult += "existing_rooms\n" + roomName + '\n';
        }

        send(admin, roomsNamesResult.c_str(), roomsNamesResult.size(), 0);
    }

    while (true)
    {
        char buffer[BUFFER_SIZE];

        int bytesRead = recv(admin, buffer, sizeof(buffer) - 1, 0);
        if (bytesRead == 0)
        {
            std::cerr << "Connection closed by client." << std::endl;
            close(admin);
            return;
        }

        buffer[bytesRead] = '\0';
        std::string data(buffer);

        std::string action = readRowFromChannel(data);

        if (action == "ban_user")
        {
            std::string nicknameBaned = readRowFromChannel(data);
            db->deleteUser(nicknameBaned);

            for (auto it = clientsInRoomsList.begin(); it != clientsInRoomsList.end(); ++it)
            {
                if (it->nickname == nicknameBaned)
                {
                    std::string deleteUserString = "ban_user\n";
                    send(it->socket, deleteUserString.c_str(), deleteUserString.size(), 0);
                    clientsInRoomsList.erase(it);
                    break;
                }
            }

            clientsRooms.banUser(nicknameBaned);
        }
        if (action == "delete_room")
        {
            std::string roomDeleted = readRowFromChannel(data);
            db->deleteRoom(roomDeleted);
            clientsRooms.deleteRoom(roomDeleted);

            std::string rowToDeletedFromRooms = "delete_room\n" + roomDeleted + '\n';
            for (Client &clientInRoomsList : clientsInRoomsList)
            {
                send(clientInRoomsList.socket, rowToDeletedFromRooms.c_str(), rowToDeletedFromRooms.size(), 0);
            }
            if (this->admin != nullptr)
            {
                std::string deleteRoomRow = "delete_room\n" + roomDeleted + '\n';
                send(admin, deleteRoomRow.c_str(), deleteRoomRow.size(), 0);
            }
        }
    }
}

std::string Server::readRowFromChannel(std::string &data)
{
    size_t pos = data.find('\n');
    if (pos == std::string::npos)
    {
        std::cerr << "Incorrect format of connect data." << std::endl;
        return "";
    }
    std::string row = data.substr(0, pos);

    data.erase(0, pos + 1);

    return row;
}

void Server::handleRoomsList(int client, std::string nickname)
{
    {
        std::lock_guard<std::mutex> lock(clientsMutex);
        clientsInRoomsList.push_back(Client(client, nickname));
    }


    std::vector<std::string> roomsNames = db->getRoomsNames();

    for (std::string &roomName : roomsNames)
    {
        std::string roomsSend = "rooms\n" + roomName + '\n';
        send(client, roomsSend.c_str(), roomsSend.size(), 0);
    }

    if (admin != nullptr)
    {
        std::string newUserStringToAdmin = "new_online_user\n" + nickname + '\n';
        send(*admin, newUserStringToAdmin.c_str(), newUserStringToAdmin.size(), 0);
    }

    while (true)
    {
        char buffer[BUFFER_SIZE];

        int bytesRead = recv(client, buffer, sizeof(buffer) - 1, 0);
        if (bytesRead == 0)
        {
            std::cerr << "Failed to receive data or connection closed." << std::endl;
            close(client);
            auto it = std::remove_if(clientsInRoomsList.begin(), clientsInRoomsList.end(),
                                     [client](const Client &user)
                                     {
                                         return user.socket == client;
                                     });
            clientsInRoomsList.erase(it, clientsInRoomsList.end());

            if (admin != nullptr)
            {
                std::string deleteUserStringToAdmin = "delete_online_user\n" + nickname + '\n';
                send(*admin, deleteUserStringToAdmin.c_str(), deleteUserStringToAdmin.size(), 0);
            }

            return;
        }

        buffer[bytesRead] = '\0';
        std::string data(buffer);

        std::string action = readRowFromChannel(data);

        if (action == "new_room")
        {
            std::string newRoom = readRowFromChannel(data);
            if (db->roomExists(newRoom))
            {
                continue;
            }
            if (!db->addRoom(newRoom))
            {
                continue;
            }
            broadcastNewRoom(newRoom);
            if (admin != nullptr)
            {
                std::string newRoomStringToAdmin = "new_room\n" + newRoom + '\n';
                send(*admin, newRoomStringToAdmin.c_str(), newRoomStringToAdmin.size(), 0);
            }
        }
    }
}

void Server::broadcastNewRoom(std::string room)
{
    std::lock_guard<std::mutex> lock(clientsMutex);

    for (Client &clientInRoom : clientsInRoomsList)
    {
        std::string newRoom = "new_room\n" + room + '\n';
        send(clientInRoom.socket, newRoom.c_str(), newRoom.size(), 0);
    }
}

void Server::handleRoom(int client, std::string roomName, std::string nickname)
{
    {
        std::lock_guard<std::mutex> lock(clientsMutex);
        clientsRooms.addValue(roomName, client, nickname);
    }

    std::vector<std::pair<std::string, std::string>> messages = db->getMessagesByRoom(roomName);

    std::string resultString = "messages\n";

    for (std::pair<std::string, std::string> &message : messages)
    {
        std::string senderNewLine = message.first + '\n';
        std::string messageNewLine = message.second + '\n';
        resultString += senderNewLine + messageNewLine;
    }

    send(client, resultString.c_str(), resultString.size(), 0);

    if (admin != nullptr)
    {
        std::string newUserStringToAdmin = "new_online_user\n" + nickname + '\n';
        send(*admin, newUserStringToAdmin.c_str(), newUserStringToAdmin.size(), 0);
    }

    while (true)
    {
        char buffer[BUFFER_SIZE];

        int bytesRead = recv(client, buffer, sizeof(buffer) - 1, 0);
        if (bytesRead == 0)
        {
            std::cerr << "Connection closed by client." << std::endl;
            close(client);
            clientsRooms.deleteClient(client, roomName);

            if (admin != nullptr)
            {
                std::string deleteUserStringToAdmin = "delete_online_user\n" + nickname + '\n';
                send(*admin, deleteUserStringToAdmin.c_str(), deleteUserStringToAdmin.size(), 0);
            }
            return;
        }

        buffer[bytesRead] = '\0';
        std::string data(buffer);

        std::string action = readRowFromChannel(data);

        if (action == "new_message")
        {
            std::string content = readRowFromChannel(data);
            db->addMessage(content, nickname, roomName);
            clientsRooms.broadcastMessage(client, content, nickname, roomName);
        }
    }
}