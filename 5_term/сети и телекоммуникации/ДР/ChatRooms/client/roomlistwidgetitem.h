#ifndef ROOMLISTWIDGETITEM_H
#define ROOMLISTWIDGETITEM_H

#include <QWidget>
#include <QTcpSocket>

namespace Ui {
class roomListWidgetItem;
}

class roomListWidgetItem : public QWidget
{
    Q_OBJECT

public:
    explicit roomListWidgetItem(QString hostName, QTcpSocket *socket, QString nickname, QString roomName, QWidget *parent = nullptr);
    ~roomListWidgetItem();
    QString name = "";

private slots:
    void openRoom();

private:
    Ui::roomListWidgetItem *ui;
    QWidget *parent = nullptr;
    QString nickname = "";
    QTcpSocket *socket = nullptr;
    QString hostName = "";
};

#endif // ROOMLISTWIDGETITEM_H
