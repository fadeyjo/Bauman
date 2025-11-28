#include <iostream>

struct Client
{
    int socket = 0;
    std::string nickname = "";
    Client(int, std::string);
};