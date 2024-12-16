#include "roomlistitem.h"
#include "ui_roomlistitem.h"

RoomListItem::RoomListItem(QTcpSocket *socket, QString roomaName, QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::RoomListItem)
{
    ui->setupUi(this);

    this->roomName = roomaName;
    this->socket = socket;

    ui->roomNameLabel->setText(roomaName);

    connect(ui->deletePushButton, &QPushButton::clicked, this, &RoomListItem::deleteRoom);
}

void RoomListItem::deleteRoom()
{
    socket->write("delete_room\n");
    socket->write(this->roomName.toUtf8() + '\n');
}

RoomListItem::~RoomListItem()
{
    delete ui;
}
