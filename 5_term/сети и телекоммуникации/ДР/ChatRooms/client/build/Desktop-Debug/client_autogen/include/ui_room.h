/********************************************************************************
** Form generated from reading UI file 'room.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ROOM_H
#define UI_ROOM_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLabel>
#include <QtWidgets/QLineEdit>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QTextEdit>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_Room
{
public:
    QWidget *centralwidget;
    QPushButton *sendNewMessagePushButton;
    QLineEdit *newMessageLineEdit;
    QTextEdit *messagesTextEdit;
    QLabel *roomNameLabel;
    QPushButton *backPushButton;
    QMenuBar *menubar;
    QStatusBar *statusbar;

    void setupUi(QMainWindow *Room)
    {
        if (Room->objectName().isEmpty())
            Room->setObjectName("Room");
        Room->resize(800, 800);
        centralwidget = new QWidget(Room);
        centralwidget->setObjectName("centralwidget");
        sendNewMessagePushButton = new QPushButton(centralwidget);
        sendNewMessagePushButton->setObjectName("sendNewMessagePushButton");
        sendNewMessagePushButton->setGeometry(QRect(625, 740, 110, 30));
        newMessageLineEdit = new QLineEdit(centralwidget);
        newMessageLineEdit->setObjectName("newMessageLineEdit");
        newMessageLineEdit->setGeometry(QRect(40, 730, 560, 50));
        messagesTextEdit = new QTextEdit(centralwidget);
        messagesTextEdit->setObjectName("messagesTextEdit");
        messagesTextEdit->setGeometry(QRect(40, 59, 720, 661));
        QFont font;
        font.setPointSize(14);
        messagesTextEdit->setFont(font);
        roomNameLabel = new QLabel(centralwidget);
        roomNameLabel->setObjectName("roomNameLabel");
        roomNameLabel->setGeometry(QRect(260, 10, 261, 41));
        QFont font1;
        font1.setPointSize(14);
        font1.setBold(true);
        roomNameLabel->setFont(font1);
        roomNameLabel->setAlignment(Qt::AlignmentFlag::AlignCenter);
        backPushButton = new QPushButton(centralwidget);
        backPushButton->setObjectName("backPushButton");
        backPushButton->setGeometry(QRect(70, 12, 80, 31));
        Room->setCentralWidget(centralwidget);
        menubar = new QMenuBar(Room);
        menubar->setObjectName("menubar");
        menubar->setGeometry(QRect(0, 0, 800, 20));
        Room->setMenuBar(menubar);
        statusbar = new QStatusBar(Room);
        statusbar->setObjectName("statusbar");
        Room->setStatusBar(statusbar);

        retranslateUi(Room);

        QMetaObject::connectSlotsByName(Room);
    } // setupUi

    void retranslateUi(QMainWindow *Room)
    {
        Room->setWindowTitle(QCoreApplication::translate("Room", "Chat", nullptr));
        sendNewMessagePushButton->setText(QCoreApplication::translate("Room", "Send", nullptr));
        newMessageLineEdit->setPlaceholderText(QCoreApplication::translate("Room", "Type your message here...", nullptr));
        roomNameLabel->setText(QString());
        backPushButton->setText(QCoreApplication::translate("Room", "Back", nullptr));
    } // retranslateUi

};

namespace Ui {
    class Room: public Ui_Room {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ROOM_H
