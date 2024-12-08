#include "Chat.h"

#include <QApplication>
#include <QScreen>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    QScreen *screen = QApplication::primaryScreen();
    QRect screenRect = screen->availableGeometry();

    Chat client1;
    client1.setWindowTitle("Chat Client");
    client1.resize(screenRect.width(), screenRect.height());
    client1.show();
    qDebug() << screenRect.height();
    qDebug() << screenRect.height();

    Chat client2;
    client2.setWindowTitle("Chat Client");
    client2.resize(screenRect.width(), screenRect.height());
    client2.show();

    return a.exec();
}
