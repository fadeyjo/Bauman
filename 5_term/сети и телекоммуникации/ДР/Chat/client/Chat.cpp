#include "Chat.h"
#include "./ui_Chat.h"

Chat::Chat(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::Chat)
{
    ui->setupUi(this);

    socket = new QTcpSocket(this);
    socket->connectToHost("127.0.0.1", 5000);

    if (!socket->waitForConnected(5000)) {
        qDebug() << "Connection failed!";
        return;
    }

    connect(ui->sendNewMsgBtn, &QPushButton::clicked, this, &Chat::sendMessage);
    connect(socket, &QTcpSocket::readyRead, this, &Chat::receiveMessage);
}

void Chat::sendMessage()
{
    if (!ui->newMsgLineEdit->text().isEmpty()) {
        QString message = ui->newMsgLineEdit->text();
        socket->write(message.toUtf8());
        ui->newMsgLineEdit->clear();
        ui->msgsBox->append("You: " + message);
    }
}

void Chat::receiveMessage()
{
    while (socket->canReadLine())
    {
        QString message = socket->readLine();
        ui->msgsBox->append("Companion:" + message.trimmed());
    }
}

Chat::~Chat()
{
    delete ui;
}
