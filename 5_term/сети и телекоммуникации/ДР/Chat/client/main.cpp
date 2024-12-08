#include "Chat.h"

#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    Chat client1;
    client1.setWindowTitle("Chat Client");
    client1.resize(800, 500);
    client1.show();

    Chat client2;
    client2.setWindowTitle("Chat Client");
    client2.resize(800, 500);
    client2.show();

    return a.exec();
}
