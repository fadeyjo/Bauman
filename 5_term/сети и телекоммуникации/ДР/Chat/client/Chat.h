#ifndef CHAT_H
#define CHAT_H

#include <QMainWindow>
#include <QTcpSocket>
#include <QKeyEvent>

QT_BEGIN_NAMESPACE
namespace Ui {
class Chat;
}
QT_END_NAMESPACE

class Chat : public QMainWindow
{
    Q_OBJECT

public:
    Chat(QWidget *parent = nullptr);
    ~Chat();

private slots:
    void sendMessage();
    void receiveMessage();

private:
    Ui::Chat *ui;
    QTcpSocket *socket;

protected:
    bool eventFilter(QObject*, QEvent*) override;
};

#endif // CHAT_H
