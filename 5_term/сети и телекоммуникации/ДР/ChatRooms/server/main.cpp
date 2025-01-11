#include "Server.h"
#include <thread>
#include <iostream>

constexpr int PORT = 5000;

void startServer();

int main()
{
    std::thread(startServer).detach();

    std::string command;

    while (true)
    {
        std::cout << "Enter command: ";
        std::cin >> command;

        if (command == "close")
        {
            break;
        }
        // Другие команды...

        std::cout << "Unknown command." << std::endl;
    }

    return 0;
}

void startServer()
{
    Server *server = new Server(PORT);

    server->startListening();
}