#include "Client.h"

Client::Client(int socket, std::string nickname)
{
    this->socket = socket;
    this->nickname = nickname;
}