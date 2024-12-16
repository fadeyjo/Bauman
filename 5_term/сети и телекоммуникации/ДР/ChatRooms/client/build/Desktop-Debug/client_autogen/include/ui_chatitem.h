/********************************************************************************
** Form generated from reading UI file 'chatitem.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_CHATITEM_H
#define UI_CHATITEM_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLabel>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_ChatItem
{
public:
    QLabel *roomName;
    QPushButton *pushButton;

    void setupUi(QWidget *ChatItem)
    {
        if (ChatItem->objectName().isEmpty())
            ChatItem->setObjectName("ChatItem");
        ChatItem->resize(400, 40);
        ChatItem->setStyleSheet(QString::fromUtf8(""));
        roomName = new QLabel(ChatItem);
        roomName->setObjectName("roomName");
        roomName->setGeometry(QRect(30, 10, 200, 21));
        pushButton = new QPushButton(ChatItem);
        pushButton->setObjectName("pushButton");
        pushButton->setGeometry(QRect(290, 10, 80, 23));

        retranslateUi(ChatItem);

        QMetaObject::connectSlotsByName(ChatItem);
    } // setupUi

    void retranslateUi(QWidget *ChatItem)
    {
        ChatItem->setWindowTitle(QCoreApplication::translate("ChatItem", "Form", nullptr));
        roomName->setText(QString());
        pushButton->setText(QCoreApplication::translate("ChatItem", "\320\236\321\202\320\272\321\200\321\213\321\202\321\214", nullptr));
    } // retranslateUi

};

namespace Ui {
    class ChatItem: public Ui_ChatItem {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_CHATITEM_H
