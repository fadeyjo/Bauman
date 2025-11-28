#include "Dict.h"

Dict::Pair::Pair(std::string key, int value, std::string nickname)
{
    this->key = key;
    this->value.push_back(std::make_pair(value, nickname));
}

void Dict::addValue(std::string key, int value, std::string nickname)
{
    for (Pair &pair : pairs)
    {
        if (pair.key == key)
        {
            pair.value.push_back(std::make_pair(value, nickname));
            return;
        }
    }
    pairs.push_back(Pair(key, value, nickname));
}

void Dict::banUser(std::string banedUser)
{
    for (Pair &pair : pairs)
    {
        for (auto it = pair.value.begin(); it != pair.value.end(); ++it)
        {
            if (it->second == banedUser)
            {
                std::string deleteUserString = "ban_user\n";
                send(it->first, deleteUserString.c_str(), deleteUserString.size(), 0);
                pair.value.erase(it);
                break;
            }
        }
    }
}

void Dict::deleteRoom(std::string roomName)
{
    for (auto it = pairs.begin(); it != pairs.end(); ++it)
    {
        if (it->key == roomName)
        {
            std::string deleteRoomString = "delete_room\n";
            for (std::pair<int, std::string> clientNickname : it->value)
            {
                send(clientNickname.first, deleteRoomString.c_str(), deleteRoomString.size(), 0);
            }
            pairs.erase(it);
            return;
        }
    }
}

void Dict::broadcastMessage(int client, std::string content, std::string nickname, std::string roomName)
{
    for (Pair &pair : pairs)
    {
        if (pair.key == roomName)
        {
            std::string newMessageNewLine = "new_message_from_server\n";
            std::string contentNewLine = content + '\n';
            std::string nicknameNewLine = nickname + '\n';

            for (std::pair<int, std::string> &value : pair.value)
            {
                if (value.first != client)
                {
                    send(value.first, newMessageNewLine.c_str(), newMessageNewLine.size(), 0);
                    send(value.first, contentNewLine.c_str(), contentNewLine.size(), 0);
                    send(value.first, nicknameNewLine.c_str(), nicknameNewLine.size(), 0);
                }
            }
            return;
        }
    }
}

std::set<std::string> Dict::getSet()
{
    std::set<std::string> result;
    for (Pair &pair : pairs)
    {
        for (std::pair<int, std::string> clientNickname : pair.value)
        {
            result.insert(clientNickname.second);
        }
    }
    return result;
}

void Dict::deleteClient(int client, std::string roomName)
{
    for (Pair &pair : pairs)
    {
        if (pair.key == roomName)
        {
            auto it = std::remove_if(pair.value.begin(), pair.value.end(),
                                     [client](const std::pair<int, std::string> &clientNickname)
                                     {
                                         return clientNickname.first == client;
                                     });
            pair.value.erase(it, pair.value.end());
            return;
        }
    }
}