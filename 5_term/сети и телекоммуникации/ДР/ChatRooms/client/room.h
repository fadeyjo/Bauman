#ifndef ROOM_H
#define ROOM_H

#include <QMainWindow>
#include <QTcpSocket>
#include "roomslist.h"

namespace Ui {
class Room;
}

class Room : public QMainWindow
{
    Q_OBJECT

public:
    explicit Room(QString hostName, QString nickname, QString roomName, QWidget *parent = nullptr);
    ~Room();

private slots:
    void sendMessage();
    void receive();
    void goBack();

private:
    Ui::Room *ui;
    QString room = "";
    QTcpSocket *socket = nullptr;
    QString nickname = "";
    QWidget *parent = nullptr;
    QString hostName = "";

protected:
    void closeEvent(QCloseEvent *event) override;
};

#endif // ROOM_H
