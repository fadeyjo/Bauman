#ifndef AUTHWINDOW_H
#define AUTHWINDOW_H

#include <QMainWindow>
#include <QTcpSocket>

QT_BEGIN_NAMESPACE
namespace Ui {
class AuthWindow;
}
QT_END_NAMESPACE

class AuthWindow : public QMainWindow
{
    Q_OBJECT

public:
    AuthWindow(QWidget *parent = nullptr);
    ~AuthWindow();

private slots:
    void clientConnect();

private:
    Ui::AuthWindow *ui;
    QTcpSocket *socket = nullptr;
};
#endif // AUTHWINDOW_H
