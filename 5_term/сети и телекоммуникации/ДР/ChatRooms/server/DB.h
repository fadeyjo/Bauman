#include <libpq-fe.h>
#include <iostream>
#include <vector>

class DB
{
public:
    DB(const char *);
    bool addUser(std::string);
    bool userExists(std::string);
    std::vector<std::string> getRoomsNames();
    bool roomExists(std::string);
    bool addRoom(std::string);
    bool addMessage(std::string, std::string, std::string);
    std::vector<std::pair<std::string, std::string>> getMessagesByRoom(std::string);
    bool deleteUser(std::string);
    bool deleteRoom(std::string);

private:
    PGconn *conn = nullptr;
};