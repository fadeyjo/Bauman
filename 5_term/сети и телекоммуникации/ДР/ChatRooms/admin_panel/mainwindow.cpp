#include "mainwindow.h"
#include "./ui_mainwindow.h"
#include "onlineuserlistitem.h"
#include "roomlistitem.h"
#include "InputDialog.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    InputDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        QString ip = dialog.getIpAddress();

        if (ip.isEmpty()) {
            return;
        }

        QString hostAddress = ip;

        socket = new QTcpSocket(this);
        bool connected = false;

        socket->connectToHost(hostAddress, 5000);
        if (!socket->waitForConnected(5000)) {
            qDebug() << "Connection by IP failed! Trying DNS...";
            socket->abort();
            socket->connectToHost(QHostAddress(hostAddress).toString(), 5000);
            connected = socket->waitForConnected(5000);
        } else {
            connected = true;
        }

        if (!connected) {
            qDebug() << "Connection failed completely!";
            return;
        }

        socket->write("get_info_for_admin\n");
    }

    connect(socket, &QTcpSocket::readyRead, this, &MainWindow::receive);
}

void MainWindow::receive()
{
    while (socket->canReadLine())
    {
        QString signal = socket->readLine();
        qDebug() << "Signal     " << signal;
        if (signal == "online_users\n")
        {
            QString nicknameFromServer = socket->readLine();
            nicknameFromServer.chop(1);
            OnlineUserListItem* userItem = new OnlineUserListItem(socket, nicknameFromServer);
            userItem->setFixedSize(700, 60);
            QListWidgetItem* listItem = new QListWidgetItem(ui->onlineClientsListWidget);
            listItem->setSizeHint(QSize(600, 60));
            ui->onlineClientsListWidget->setItemWidget(listItem, userItem);
            continue;
        }

        if (signal == "existing_rooms\n")
        {
            QString roomFromServer = socket->readLine();
            roomFromServer.chop(1);
            RoomListItem* roomItem = new RoomListItem(socket, roomFromServer);
            roomItem->setFixedSize(700, 60);
            QListWidgetItem* listItem = new QListWidgetItem(ui->existingRoomsListWidget);
            listItem->setSizeHint(QSize(600, 60));
            ui->existingRoomsListWidget->setItemWidget(listItem, roomItem);
            continue;
        }

        if (signal == "new_online_user\n")
        {
            QString nicknameFromServer = socket->readLine();
            qDebug() << "New user:  " << nicknameFromServer;
            nicknameFromServer.chop(1);
            OnlineUserListItem* userItem = new OnlineUserListItem(socket, nicknameFromServer);
            userItem->setFixedSize(700, 60);
            QListWidgetItem* listItem = new QListWidgetItem(ui->onlineClientsListWidget);
            listItem->setSizeHint(QSize(600, 60));
            ui->onlineClientsListWidget->setItemWidget(listItem, userItem);
            continue;
        }

        if (signal == "new_room\n")
        {
            QString roomFromServer = socket->readLine();
            roomFromServer.chop(1);
            RoomListItem* roomItem = new RoomListItem(socket, roomFromServer);
            roomItem->setFixedSize(700, 60);
            QListWidgetItem* listItem = new QListWidgetItem(ui->existingRoomsListWidget);
            listItem->setSizeHint(QSize(600, 60));
            ui->existingRoomsListWidget->setItemWidget(listItem, roomItem);
            continue;
        }

        if (signal == "delete_room\n")
        {
            QString roomToDelete = socket->readLine();
            roomToDelete.chop(1);

            for (int i = 0; i < ui->existingRoomsListWidget->count(); ++i)
            {
                QListWidgetItem *listItem = ui->existingRoomsListWidget->item(i);

                RoomListItem *roomItem = qobject_cast<RoomListItem*>(ui->existingRoomsListWidget->itemWidget(listItem));
                if (roomItem && roomItem->roomName == roomToDelete)
                {
                    delete ui->existingRoomsListWidget->takeItem(i);
                    continue;
                }
            }
        }

        if (signal == "delete_online_user\n")
        {
            QString userToDelete = socket->readLine();
            userToDelete.chop(1);

            for (int i = 0; i < ui->onlineClientsListWidget->count(); ++i)
            {
                QListWidgetItem *listItem = ui->onlineClientsListWidget->item(i);

                OnlineUserListItem *userItem = qobject_cast<OnlineUserListItem*>(ui->onlineClientsListWidget->itemWidget(listItem));
                if (userItem && userItem->nickname == userToDelete)
                {
                    delete ui->onlineClientsListWidget->takeItem(i);
                    continue;
                }
            }
        }
    }
}

MainWindow::~MainWindow()
{
    delete ui;
}
