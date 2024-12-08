/********************************************************************************
** Form generated from reading UI file 'Chat.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_CHAT_H
#define UI_CHAT_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLineEdit>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QTextEdit>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_Chat
{
public:
    QWidget *centralwidget;
    QPushButton *sendNewMsgBtn;
    QLineEdit *newMsgLineEdit;
    QTextEdit *msgsBox;
    QMenuBar *menubar;
    QStatusBar *statusbar;

    void setupUi(QMainWindow *Chat)
    {
        if (Chat->objectName().isEmpty())
            Chat->setObjectName("Chat");
        Chat->resize(800, 500);
        centralwidget = new QWidget(Chat);
        centralwidget->setObjectName("centralwidget");
        sendNewMsgBtn = new QPushButton(centralwidget);
        sendNewMsgBtn->setObjectName("sendNewMsgBtn");
        sendNewMsgBtn->setGeometry(QRect(710, 442, 80, 26));
        newMsgLineEdit = new QLineEdit(centralwidget);
        newMsgLineEdit->setObjectName("newMsgLineEdit");
        newMsgLineEdit->setGeometry(QRect(0, 430, 700, 50));
        msgsBox = new QTextEdit(centralwidget);
        msgsBox->setObjectName("msgsBox");
        msgsBox->setGeometry(QRect(0, 0, 800, 430));
        Chat->setCentralWidget(centralwidget);
        menubar = new QMenuBar(Chat);
        menubar->setObjectName("menubar");
        menubar->setGeometry(QRect(0, 0, 800, 20));
        Chat->setMenuBar(menubar);
        statusbar = new QStatusBar(Chat);
        statusbar->setObjectName("statusbar");
        Chat->setStatusBar(statusbar);

        retranslateUi(Chat);

        QMetaObject::connectSlotsByName(Chat);
    } // setupUi

    void retranslateUi(QMainWindow *Chat)
    {
        Chat->setWindowTitle(QCoreApplication::translate("Chat", "Chat", nullptr));
        sendNewMsgBtn->setText(QCoreApplication::translate("Chat", "Send", nullptr));
        newMsgLineEdit->setPlaceholderText(QCoreApplication::translate("Chat", "Type your message here...", nullptr));
    } // retranslateUi

};

namespace Ui {
    class Chat: public Ui_Chat {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_CHAT_H
