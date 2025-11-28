#include "room.h"
#include "ui_room.h"
#include "authwindow.h"

Room::Room(QString hostName, QString nickname, QString roomName, QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::Room)
{
    ui->setupUi(this);

    room = roomName;
    this->nickname = nickname;
    this->parent = parent;
    this->hostName = hostName;

    ui->roomNameLabel->setText(room);

    socket= new QTcpSocket(this);

    socket->connectToHost(hostName, 5000);
    if (!socket->waitForConnected(5000))
    {
        qDebug() << "Connection failed!";
        return;
    }

    socket->write("get_messages\n");
    socket->write(room.toUtf8());
    socket->write(nickname.toUtf8() + '\n');

    connect(ui->sendNewMessagePushButton, &QPushButton::clicked, this, &Room::sendMessage);
    connect(socket, &QTcpSocket::readyRead, this, &Room::receive);
    connect(ui->backPushButton, &QPushButton::clicked, this, &Room::goBack);
    connect(ui->newMessageLineEdit, &QLineEdit::returnPressed, this, &Room::sendMessage);
}

void Room::goBack()
{
    RoomsList *roomsList = new RoomsList(hostName, nickname);
    socket->disconnectFromHost();
    close();
    roomsList->show();
}

void Room::receive()
{
    bool readSender = true;
    QString sender = "";
    while (socket->canReadLine())
    {
        QString signal = socket->readLine();
        if (signal == "new_message_from_server\n")
        {
            QString content = socket->readLine();
            QString nicknameFromServer = socket->readLine();
            nicknameFromServer.chop(1);
            content.chop(1);

            ui->messagesTextEdit->append(nicknameFromServer + ": " + content);
            return;
        }

        if (signal == "ban_user\n")
        {
            close();
        }

        if (signal == "delete_room\n")
        {
            RoomsList *roomsList = new RoomsList(hostName, nickname);
            socket->disconnectFromHost();
            close();
            roomsList->show();
        }

        if (signal == "messages\n") continue;
        if (readSender)
        {
            if (signal == this->nickname + '\n')
            {
                sender = "You\n";
            }
            else
            {
                sender = signal;
            }
        }
        else
        {
            sender.chop(1);
            signal.chop(1);
            ui->messagesTextEdit->append(sender + ": " + signal);
        }
        readSender = !readSender;
    }
}

void Room::sendMessage()
{
    QString newMessage = ui->newMessageLineEdit->text();

    if (newMessage == "")
    {
        return;
    }

    ui->newMessageLineEdit->setText("");

    socket->write("new_message\n");
    socket->write(newMessage.toUtf8() + '\n');

    ui->messagesTextEdit->append("You: " + newMessage);
}

void Room::closeEvent(QCloseEvent *event)
{
    socket->disconnectFromHost();
}

Room::~Room()
{
    delete ui;
}
