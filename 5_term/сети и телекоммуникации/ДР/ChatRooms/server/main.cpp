#include "Server.h"
#include <thread>
#include <iostream>

constexpr int PORT = 5000;

void startServer(char *);

int main()
{
    int bufferSize = 100;
    char *listenAddress = new char[bufferSize];
    std::cout << "Enter addres to server or type 'all' for listening all ip addr: ";
    std::cin.getline(listenAddress, bufferSize);

    std::thread(startServer, listenAddress).detach();

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

void startServer(char *listenAddress)
{
    Server *server = new Server(PORT, listenAddress);

    server->startListening();
}