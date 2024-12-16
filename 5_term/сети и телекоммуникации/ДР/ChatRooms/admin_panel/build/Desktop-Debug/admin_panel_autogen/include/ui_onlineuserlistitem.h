/********************************************************************************
** Form generated from reading UI file 'onlineuserlistitem.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ONLINEUSERLISTITEM_H
#define UI_ONLINEUSERLISTITEM_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLabel>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_OnlineUserListItem
{
public:
    QLabel *nicknameLabel;
    QPushButton *banPushButton;

    void setupUi(QWidget *OnlineUserListItem)
    {
        if (OnlineUserListItem->objectName().isEmpty())
            OnlineUserListItem->setObjectName("OnlineUserListItem");
        OnlineUserListItem->resize(700, 60);
        nicknameLabel = new QLabel(OnlineUserListItem);
        nicknameLabel->setObjectName("nicknameLabel");
        nicknameLabel->setGeometry(QRect(40, 12, 261, 31));
        QFont font;
        font.setPointSize(12);
        nicknameLabel->setFont(font);
        banPushButton = new QPushButton(OnlineUserListItem);
        banPushButton->setObjectName("banPushButton");
        banPushButton->setGeometry(QRect(520, 12, 91, 31));

        retranslateUi(OnlineUserListItem);

        QMetaObject::connectSlotsByName(OnlineUserListItem);
    } // setupUi

    void retranslateUi(QWidget *OnlineUserListItem)
    {
        OnlineUserListItem->setWindowTitle(QCoreApplication::translate("OnlineUserListItem", "Form", nullptr));
        nicknameLabel->setText(QString());
        banPushButton->setText(QCoreApplication::translate("OnlineUserListItem", "Ban", nullptr));
    } // retranslateUi

};

namespace Ui {
    class OnlineUserListItem: public Ui_OnlineUserListItem {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ONLINEUSERLISTITEM_H
