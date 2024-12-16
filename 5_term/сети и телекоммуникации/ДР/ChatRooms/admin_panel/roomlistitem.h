#ifndef ROOMLISTITEM_H
#define ROOMLISTITEM_H

#include <QWidget>
#include <QTcpSocket>

namespace Ui {
class RoomListItem;
}

class RoomListItem : public QWidget
{
    Q_OBJECT

public:
    explicit RoomListItem(QTcpSocket *socket, QString roomName, QWidget *parent = nullptr);
    ~RoomListItem();
    QString roomName = "";

private slots:
    void deleteRoom();

private:
    Ui::RoomListItem *ui;
    QTcpSocket *socket;
};

#endif // ROOMLISTITEM_H
