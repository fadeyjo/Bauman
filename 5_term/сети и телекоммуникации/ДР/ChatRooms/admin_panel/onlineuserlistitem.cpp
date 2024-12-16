#include "onlineuserlistitem.h"
#include "ui_onlineuserlistitem.h"

OnlineUserListItem::OnlineUserListItem(QTcpSocket *socket, QString nickname, QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::OnlineUserListItem)
{
    ui->setupUi(this);

    this->nickname = nickname;
    this->socket = socket;

    ui->nicknameLabel->setText(nickname);

    connect(ui->banPushButton, &QPushButton::clicked, this, &OnlineUserListItem::deleteUser);
}

void OnlineUserListItem::deleteUser()
{
    socket->write("ban_user\n");
    socket->write(nickname.toUtf8() + '\n');
}

OnlineUserListItem::~OnlineUserListItem()
{
    delete ui;
}
