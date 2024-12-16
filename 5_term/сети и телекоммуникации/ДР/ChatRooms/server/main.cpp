#include "Server.h"

constexpr int PORT = 5000;

int main()
{
    Server *server = new Server(PORT);

    server->startListening();

    return 0;
}