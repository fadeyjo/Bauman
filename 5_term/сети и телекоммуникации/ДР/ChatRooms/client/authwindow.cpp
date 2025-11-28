#include "authwindow.h"
#include "./ui_authwindow.h"
#include "roomslist.h"
#include "InputDialog.h"

AuthWindow::AuthWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::AuthWindow)
{
    ui->setupUi(this);

    connect(ui->connectPushButton, &QPushButton::clicked, this, &AuthWindow::clientConnect);
}

void AuthWindow::clientConnect() {
    QString nickname = ui->nicknameLineEdit->text();

    if (nickname.isEmpty()) {
        ui->errorLabel->setText("Enter nickname");
        return;
    }

    InputDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        QString ip = dialog.getIpAddress();

        if (ip.isEmpty()) {
            ui->errorLabel->setText("Please enter either an IP or DNS name");
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
            ui->errorLabel->setText("Connection failed");
            return;
        }

        socket->write("new_user\n");
        socket->write(nickname.toUtf8() + '\n');

        if (socket->waitForReadyRead(5000)) {
            QString data = socket->readLine();
            if (data == "User already exists\n") {
                ui->errorLabel->setText("User already exists");
                return;
            }
            if (data == "Error with adding user\n") {
                ui->errorLabel->setText("Error with adding user");
                return;
            }

            // Переход к RoomsList
            RoomsList *roomsList = new RoomsList(hostAddress, nickname);

            close();
            roomsList->show();
        } else {
            ui->errorLabel->setText("No response from server");
        }
    }



    // QString nickname = ui->nicknameLineEdit->text();

    // if (nickname == "") {
    //     ui->errorLabel->setText("Enter nickname");
    //     return;
    // }

    // InputDialog dialog(this);
    // if (dialog.exec() == QDialog::Accepted) {
    //     QString ip = dialog.getIpAddress();

    //     QString hostAddress;
    //     if (!ip.isEmpty()) {
    //         hostAddress = ip;
    //     }
    //     else {
    //         ui->errorLabel->setText("Please enter either an IP");
    //         return;
    //     }

    //     socket = new QTcpSocket(this);
    //     socket->connectToHost(hostAddress, 5000);
    //     if (!socket->waitForConnected(5000)) {
    //         qDebug() << "Connection failed!";
    //         return;
    //     }

    //     socket->write("new_user\n");
    //     socket->write(nickname.toUtf8() + '\n');

    //     if (socket->waitForReadyRead(5000)) {
    //         QString data = socket->readLine();
    //         if (data == "User already exists\n") {
    //             ui->errorLabel->setText("User already exists");
    //             return;
    //         }
    //         if (data == "Error with adding user\n") {
    //             ui->errorLabel->setText("Error with adding user");
    //             return;
    //         }

    //         RoomsList *roomsList = new RoomsList(hostAddress, nickname);

    //         close();
    //         roomsList->show();
    //     }
    // }
}

AuthWindow::~AuthWindow()
{
    delete ui;
}
