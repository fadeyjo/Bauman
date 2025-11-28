#include "DB.h"

DB::DB(const char *connectionString)
{
    conn = PQconnectdb(connectionString);

    if (PQstatus(conn) != CONNECTION_OK)
    {
        std::cerr << "Connection to database failed: " << PQerrorMessage(conn) << std::endl;
        PQfinish(conn);
        return;
    }

    std::cout << "Connected to database successfully!" << std::endl;
}

bool DB::addUser(std::string nickname)
{
    std::string query = "INSERT INTO users (nickname) VALUES ($1)";

    const char *paramValues[1] = {nickname.c_str()};
    PGresult *res = PQexecParams(conn, query.c_str(),
                                 1,
                                 nullptr,
                                 paramValues,
                                 nullptr,
                                 nullptr,
                                 0);

    if (PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        std::cerr << "Failed to insert user: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return false;
    }

    PQclear(res);
    return true;
}

bool DB::userExists(std::string nickname)
{
    std::string query = "SELECT 1 FROM users WHERE nickname = $1 LIMIT 1";

    const char *paramValues[1] = {nickname.c_str()};
    PGresult *res = PQexecParams(conn, query.c_str(), 1, nullptr, paramValues, nullptr, nullptr, 0);

    if (PQresultStatus(res) != PGRES_TUPLES_OK)
    {
        std::cerr << "Query failed: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return false;
    }

    bool exists = PQntuples(res) > 0;

    PQclear(res);
    return exists;
}

std::vector<std::string> DB::getRoomsNames()
{
    std::vector<std::string> roomsNames;
    const char *query = "SELECT name FROM rooms";

    PGresult *res = PQexec(conn, query);
    if (PQresultStatus(res) != PGRES_TUPLES_OK)
    {
        std::cerr << "Failed to execute query: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return roomsNames;
    }

    int nRows = PQntuples(res);
    for (int i = 0; i < nRows; ++i)
    {
        roomsNames.push_back(PQgetvalue(res, i, 0));
    }

    PQclear(res);
    return roomsNames;
}

bool DB::roomExists(std::string roomName)
{
    const char *query = "SELECT 1 FROM rooms WHERE name = $1 LIMIT 1";
    const char *paramValues[1] = {roomName.c_str()};

    PGresult *res = PQexecParams(
        conn, query, 1, nullptr, paramValues, nullptr, nullptr, 0);

    if (PQresultStatus(res) != PGRES_TUPLES_OK)
    {
        std::cerr << "Failed to execute query: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return false;
    }

    bool exists = PQntuples(res) > 0;
    PQclear(res);
    return exists;
}

bool DB::addRoom(std::string roomName)
{
    if (roomExists(roomName))
    {
        std::cerr << "Room already exists: " << roomName << std::endl;
        return false;
    }

    const char *query = "INSERT INTO rooms (name) VALUES ($1)";
    const char *paramValues[1] = {roomName.c_str()};

    PGresult *res = PQexecParams(
        conn, query, 1, nullptr, paramValues, nullptr, nullptr, 0);

    if (PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        std::cerr << "Failed to insert room: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return false;
    }

    PQclear(res);
    return true;
}

bool DB::addMessage(std::string content, std::string senderNickname, std::string roomName)
{
    const char* query = "INSERT INTO messages (content, sender_nickname, room_name, date_time) VALUES ($1, $2, $3, NOW())";

    const char* values[3] = {content.c_str(), senderNickname.c_str(), roomName.c_str()};
    int lengths[3] = {(int)content.size(), (int)senderNickname.size(), (int)roomName.size()};
    int formats[3] = {0, 0, 0};

    PGresult* res = PQexecParams(conn, query, 3, nullptr, values, lengths, formats, 0);

    if (PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        std::cerr << "Failed to insert message: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return false;
    }

    std::cout << "Message inserted successfully!" << std::endl;

    PQclear(res);
    return true;
}

std::vector<std::pair<std::string, std::string>> DB::getMessagesByRoom(std::string roomName)
{
    std::vector<std::pair<std::string, std::string>> messages;

    const char* query = "SELECT sender_nickname, content FROM messages WHERE room_name = $1 ORDER BY date_time ASC";

    const char* values[1] = {roomName.c_str()};
    int lengths[1] = {(int)roomName.size()};
    int formats[1] = {0};

    PGresult* res = PQexecParams(conn, query, 1, nullptr, values, lengths, formats, 0);

    if (PQresultStatus(res) != PGRES_TUPLES_OK)
    {
        std::cerr << "Failed to get messages: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return messages;
    }

    int numRows = PQntuples(res);
    for (int i = 0; i < numRows; ++i)
    {
        std::string senderNickname = PQgetvalue(res, i, 0);
        std::string content = PQgetvalue(res, i, 1);
        messages.push_back({senderNickname, content});
    }

    PQclear(res);

    return messages;
}

bool DB::deleteUser(std::string nickname)
{
    std::string query = "DELETE FROM users WHERE nickname = $1";

    const char *paramValues[1] = {nickname.c_str()};
    PGresult *res = PQexecParams(conn, query.c_str(),
                                 1,
                                 nullptr,
                                 paramValues,
                                 nullptr,
                                 nullptr,
                                 0);

    if (PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        std::cerr << "Failed to delete user: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return false;
    }

    PQclear(res);
    return true;
}

bool DB::deleteRoom(std::string roomName)
{
    std::string query = "DELETE FROM rooms WHERE name = $1";

    const char *paramValues[1] = {roomName.c_str()};
    PGresult *res = PQexecParams(conn, query.c_str(),
                                 1,
                                 nullptr,
                                 paramValues,
                                 nullptr,
                                 nullptr,
                                 0);

    if (PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        std::cerr << "Failed to delete room: " << PQerrorMessage(conn) << std::endl;
        PQclear(res);
        return false;
    }

    PQclear(res);
    return true;
}