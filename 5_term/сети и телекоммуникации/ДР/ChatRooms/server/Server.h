#include <netinet/in.h>
#include <iostream>
#include <thread>
#include <unistd.h>
#include "DB.h"
#include <vector>
#include <mutex>
#include "Dict.h"
#include "Client.h"
#include <set>
#include <algorithm>
#include <arpa/inet.h>

class Server
{
public:
    Server(int);
    void startListening();

private:
    int server = -1;
    sockaddr_in socketSettings{};

    DB *db = nullptr;

    int PORT = -1;

    static const int BUFFER_SIZE = 1024;

    std::mutex clientsMutex;

    std::vector<Client> clientsInRoomsList;
    Dict clientsRooms = Dict();

    void handleConnections(int);
    void handleRoomsList(int, std::string);
    void broadcastNewRoom(std::string);
    void handleRoom(int, std::string, std::string);
    void handleAdmin(int);

    int *admin = nullptr;

    std::string readRowFromChannel(std::string &);
};