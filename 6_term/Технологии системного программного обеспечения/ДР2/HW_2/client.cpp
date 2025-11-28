#include <arpa/inet.h>
#include <unistd.h>
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <sys/socket.h>
#include <netinet/in.h>

std::string toRoman(int);

int main() {
    const char* server_ip = "127.0.0.1";
    const int port = 12345;
    std::string filename = "results.txt";

    std::ofstream outFile(filename, std::ios::app);
    if (!outFile) {
        std::cerr << "Error to open file";
        return 1;
    }

    while (true) {
        std::cout << "Enter the number (0 to exit): ";
        int num;
        std::cin >> num;
        if (num == 0) break;

        if (num < 1 || num > 3999) {
            std::cout << "Number out of range [1-3999]\n";
            continue;
        }

        std::string roman = toRoman(num);
        std::string line = std::to_string(num) + " = " + roman + "\n";
        std::cout << line;
        outFile << line;
        outFile.flush();

        int sock = socket(AF_INET, SOCK_STREAM, 0);
        sockaddr_in serv_addr{};
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_port = htons(port);
        inet_pton(AF_INET, server_ip, &serv_addr.sin_addr);

        if (connect(sock, (sockaddr*)&serv_addr, sizeof(serv_addr)) < 0) {
            std::cerr << "Ошибка соединения с сервером\n";
            close(sock);
            continue;
        }

        std::ifstream fileToSend(filename, std::ios::binary);
        std::vector<char> buffer((std::istreambuf_iterator<char>(fileToSend)), {});
        send(sock, buffer.data(), buffer.size(), 0);
        close(sock);
    }

    return 0;
}

std::string toRoman(int number) {
    std::pair<int, std::string> roman[] = {
        {1000, "M"}, {900, "CM"}, {500, "D"}, {400, "CD"},
        {100,  "C"}, {90,  "XC"}, {50,  "L"}, {40,  "XL"},
        {10,   "X"}, {9,   "IX"}, {5,   "V"}, {4,   "IV"},
        {1,    "I"}
    };

    std::string result;
    for (auto& [val, sym] : roman) {
        while (number >= val) {
            result += sym;
            number -= val;
        }
    }
    return result;
}

