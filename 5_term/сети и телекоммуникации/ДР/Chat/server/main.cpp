#include <iostream>
#include <thread>
#include <vector>
#include <string>
#include <cstring>
#include <netinet/in.h>
#include <unistd.h>
#include <mutex>
#include <algorithm>

constexpr int PORT = 5000;
constexpr int BUFFER_SIZE = 1024;

std::vector<int> clients;
std::mutex clients_mutex;

void broadcast_message(const std::string &, int);
void handle_client(int);

int main()
{
    int server = socket(AF_INET, SOCK_STREAM, 0);

    if (server == -1)
    {
        perror("Socket creation failed");
        return 1;
    }

    sockaddr_in socket_settings{};
    socket_settings.sin_family = AF_INET;
    socket_settings.sin_addr.s_addr = INADDR_ANY;
    socket_settings.sin_port = htons(PORT);

    if (bind(server, reinterpret_cast<sockaddr *>(&socket_settings), sizeof(socket_settings)) < 0)
    {
        perror("Bind failed");
        return 1;
    }

    if (listen(server, 3) == -1)
    {
        perror("Listen failed");
        return 1;
    }

    std::cout << "Server listening on port " << PORT << "..." << std::endl;

    while (true)
    {
        sockaddr_in client_addr{};
        socklen_t client_len = sizeof(client_addr);
        int client = accept(server, reinterpret_cast<sockaddr *>(&client_addr), &client_len);
        if (client == -1)
        {
            perror("Client accept failed");
            continue;
        }

        std::lock_guard<std::mutex> lock(clients_mutex);
        clients.push_back(client);

        std::thread(handle_client, client).detach();
    }

    close(server);
    return 0;
}

void broadcast_message(const std::string &message, int sender)
{
    std::lock_guard<std::mutex> lock(clients_mutex);
    std::string message_with_newline = message + '\n';
    for (int client : clients)
    {
        if (client != sender)
        {
            send(client, message_with_newline.c_str(), message_with_newline.size(), 0);
        }
    }
}

void handle_client(int client)
{
    char buffer[BUFFER_SIZE];
    while (true)
    {
        std::memset(buffer, 0, BUFFER_SIZE);
        ssize_t bytes_received = recv(client, buffer, BUFFER_SIZE, 0);

        if (bytes_received <= 0)
        {
            break;
        }

        std::string message(buffer, bytes_received);
        std::cout << "Received: " << message << std::endl;
        broadcast_message(message, client);
    }

    close(client);

    std::lock_guard<std::mutex> lock(clients_mutex);
    clients.erase(std::remove(clients.begin(), clients.end(), client), clients.end());
}
