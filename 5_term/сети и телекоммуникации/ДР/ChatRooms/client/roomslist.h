#ifndef ROOMSLIST_H
#define ROOMSLIST_H

#include <QMainWindow>
#include <QTcpSocket>

namespace Ui {
class RoomsList;
}

class RoomsList : public QMainWindow
{
    Q_OBJECT

public:
    explicit RoomsList(QString nickname, QWidget *parent = nullptr);
    ~RoomsList();

private slots:
    void createNewRoom();
    void receive();

private:
    Ui::RoomsList *ui;
    QString nickname = "";
    QTcpSocket *socket = nullptr;
    QString newRoomName;

protected:
    void closeEvent(QCloseEvent *event) override;
};

#endif // ROOMSLIST_H
