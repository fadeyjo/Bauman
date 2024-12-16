#include <vector>
#include <iostream>
#include <netinet/in.h>
#include <set>
#include <algorithm>

class Dict
{
public:
    void addValue(std::string, int, std::string);
    void broadcastMessage(int, std::string, std::string, std::string);
    std::set<std::string> getSet();
    void deleteClient(int, std::string);
    void banUser(std::string);
    void deleteRoom(std::string);
private:
    struct Pair
    {
        Pair(std::string, int, std::string);
        std::string key = "";
        std::vector<std::pair<int, std::string>> value;
    };

    std::vector<Pair> pairs;
};