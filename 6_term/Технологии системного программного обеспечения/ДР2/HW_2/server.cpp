#include <iostream>
#include <fstream>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>

#define PORT 12345
#define BUFFER_SIZE 4096

int main() {
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) return 1;

    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_fd, (sockaddr*)&address, sizeof(address)) < 0)
        return 1;

    if (listen(server_fd, 3) < 0)
        return 1;

    while (true) {
        sockaddr_in client_addr{};
        socklen_t client_len = sizeof(client_addr);
        int client_socket = accept(server_fd, (sockaddr*)&client_addr, &client_len);
        if (client_socket < 0) continue;

        std::ofstream outfile("received.txt", std::ios::app | std::ios::binary);
        if (!outfile) {
            close(client_socket);
            continue;
        }

        char buffer[BUFFER_SIZE];
        ssize_t bytes_read;
        while ((bytes_read = read(client_socket, buffer, BUFFER_SIZE)) > 0) {
            outfile.write(buffer, bytes_read);
        }

        outfile.close();
        close(client_socket);
    }

    close(server_fd);
    return 0;
}
