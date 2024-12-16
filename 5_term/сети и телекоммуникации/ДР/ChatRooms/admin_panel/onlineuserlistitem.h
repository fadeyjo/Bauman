#ifndef ONLINEUSERLISTITEM_H
#define ONLINEUSERLISTITEM_H

#include <QWidget>
#include <QTcpSocket>

namespace Ui {
class OnlineUserListItem;
}

class OnlineUserListItem : public QWidget
{
    Q_OBJECT

public:
    explicit OnlineUserListItem(QTcpSocket *socket, QString nickname, QWidget *parent = nullptr);
    ~OnlineUserListItem();
    QString nickname = "";

private slots:
    void deleteUser();

private:
    Ui::OnlineUserListItem *ui;
    QTcpSocket *socket = nullptr;
};

#endif // ONLINEUSERLISTITEM_H
