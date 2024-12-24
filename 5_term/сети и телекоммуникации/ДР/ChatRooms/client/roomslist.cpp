#include "roomslist.h"
#include "ui_roomslist.h"
#include "roomlistwidgetitem.h"

RoomsList::RoomsList(QString hostAddress, QString nickname, QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::RoomsList)
{
    ui->setupUi(this);

    this->nickname = nickname;
    this->hostAddress = hostAddress;

    socket= new QTcpSocket(this);

    socket->connectToHost(hostAddress, 5000);
    if (!socket->waitForConnected(5000))
    {
        qDebug() << "Connection failed!";
        return;
    }

    socket->write("get_rooms\n");
    socket->write(nickname.toUtf8() + '\n');

    connect(ui->newRoomPushButton, &QPushButton::clicked, this, &RoomsList::createNewRoom);
    connect(socket, &QTcpSocket::readyRead, this, &RoomsList::receive);
}

void RoomsList::createNewRoom()
{
    QString newRoom = ui->newRoomLineEdit->text();

    ui->newRoomLineEdit->setText("");

    if (newRoom == "")
    {
        return;
    }

    newRoomName = newRoom;

    socket->write("new_room\n");
    socket->write(newRoom.toUtf8() + '\n');
}

void RoomsList::receive()
{
    while (socket->canReadLine())
    {
        QString message = socket->readLine();
        qDebug() << "Signal    " << message;
        if (message == "new_room\n")
        {
            QString newRoomFrom = socket->readLine();
            roomListWidgetItem* roomItem = new roomListWidgetItem(hostAddress, socket, nickname, newRoomFrom, this);
            roomItem->setFixedSize(600, 60);

            QListWidgetItem* listItem = new QListWidgetItem(ui->roomsListWidget);
            listItem->setSizeHint(QSize(600, 60));

            ui->roomsListWidget->setItemWidget(listItem, roomItem);

            newRoomName = "";
            return;
        }

        if (message == "ban_user\n")
        {
            close();
        }

        if (message == "delete_room\n")
        {
            QString roomToDelete = socket->readLine();

            for (int i = 0; i < ui->roomsListWidget->count(); ++i)
            {
                QListWidgetItem *listItem = ui->roomsListWidget->item(i);

                roomListWidgetItem *roomItem = qobject_cast<roomListWidgetItem*>(ui->roomsListWidget->itemWidget(listItem));
                qDebug() << roomToDelete;
                qDebug() << roomItem->name;
                if (roomItem && roomItem->name == roomToDelete)
                {
                    delete ui->roomsListWidget->takeItem(i);
                    continue;
                }
            }
        }

        if (message == "rooms\n")
        {
            QString existRoomName = socket->readLine();

            roomListWidgetItem* roomItem = new roomListWidgetItem(hostAddress, socket, nickname, existRoomName, this);
            roomItem->setFixedSize(600, 60);

            QListWidgetItem* listItem = new QListWidgetItem(ui->roomsListWidget);
            listItem->setSizeHint(QSize(600, 60));

            ui->roomsListWidget->setItemWidget(listItem, roomItem);
        }
    }
}

void RoomsList::closeEvent(QCloseEvent *event)
{
    qDebug() << "Close rooms list";
    socket->disconnectFromHost();
}

RoomsList::~RoomsList()
{
    delete ui;
}
