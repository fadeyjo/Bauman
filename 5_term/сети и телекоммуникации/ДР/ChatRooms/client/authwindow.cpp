#include "authwindow.h"
#include "./ui_authwindow.h"
#include "roomslist.h"

AuthWindow::AuthWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::AuthWindow)
{
    ui->setupUi(this);

    connect(ui->connectPushButton, &QPushButton::clicked, this, &AuthWindow::clientConnect);
}

void AuthWindow::clientConnect() {
    QString nickname = ui->nicknameLineEdit->text();

    if (nickname == "")
    {
        ui->errorLabel->setText("Enter nickname");
        return;
    }

    socket = new QTcpSocket(this);

    socket->connectToHost("127.0.0.1", 5000);
    if (!socket->waitForConnected(5000))
    {
        qDebug() << "Connection failed!";
        return;
    }

    socket->write("new_user\n");
    socket->write(nickname.toUtf8() + '\n');

    if (socket->waitForReadyRead(5000))
    {
        QString data = socket->readLine();
        if (data == "User already exists\n")
        {
            ui->errorLabel->setText("User already exists");
            return;
        }
        if (data == "Error with adding user\n")
        {
            ui->errorLabel->setText("Error with adding user");
            return;
        }

        RoomsList *roomsList = new RoomsList(nickname);

        close();
        roomsList->show();
    }
}

AuthWindow::~AuthWindow()
{
    delete ui;
}
