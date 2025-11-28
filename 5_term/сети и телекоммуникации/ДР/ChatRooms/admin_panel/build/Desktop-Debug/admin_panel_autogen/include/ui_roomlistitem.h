/********************************************************************************
** Form generated from reading UI file 'roomlistitem.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ROOMLISTITEM_H
#define UI_ROOMLISTITEM_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLabel>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_RoomListItem
{
public:
    QLabel *roomNameLabel;
    QPushButton *deletePushButton;

    void setupUi(QWidget *RoomListItem)
    {
        if (RoomListItem->objectName().isEmpty())
            RoomListItem->setObjectName("RoomListItem");
        RoomListItem->resize(700, 60);
        roomNameLabel = new QLabel(RoomListItem);
        roomNameLabel->setObjectName("roomNameLabel");
        roomNameLabel->setGeometry(QRect(60, 10, 291, 31));
        QFont font;
        font.setPointSize(12);
        roomNameLabel->setFont(font);
        deletePushButton = new QPushButton(RoomListItem);
        deletePushButton->setObjectName("deletePushButton");
        deletePushButton->setGeometry(QRect(510, 10, 101, 31));

        retranslateUi(RoomListItem);

        QMetaObject::connectSlotsByName(RoomListItem);
    } // setupUi

    void retranslateUi(QWidget *RoomListItem)
    {
        RoomListItem->setWindowTitle(QCoreApplication::translate("RoomListItem", "Form", nullptr));
        roomNameLabel->setText(QString());
        deletePushButton->setText(QCoreApplication::translate("RoomListItem", "Delete", nullptr));
    } // retranslateUi

};

namespace Ui {
    class RoomListItem: public Ui_RoomListItem {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ROOMLISTITEM_H
