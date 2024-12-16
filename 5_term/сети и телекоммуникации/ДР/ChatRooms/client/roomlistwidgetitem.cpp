#include "roomlistwidgetitem.h"
#include "ui_roomlistwidgetitem.h"
#include "room.h"

roomListWidgetItem::roomListWidgetItem(QTcpSocket *socket, QString nickname, QString roomName, QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::roomListWidgetItem)
{
    ui->setupUi(this);

    this->nickname = nickname;
    this->parent = parent;
    this->name = roomName;
    this->socket = socket;

    ui->roomNameLabel->setText(roomName);

    connect(ui->openRoomPushButton, &QPushButton::clicked, this, &roomListWidgetItem::openRoom);
}

void roomListWidgetItem::openRoom()
{
    Room *room = new Room(nickname, name, parent);
    parent->close();
    room->show();
}

roomListWidgetItem::~roomListWidgetItem()
{
    delete ui;
}
