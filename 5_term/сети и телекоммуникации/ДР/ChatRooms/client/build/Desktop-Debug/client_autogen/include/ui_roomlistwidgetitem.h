/********************************************************************************
** Form generated from reading UI file 'roomlistwidgetitem.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ROOMLISTWIDGETITEM_H
#define UI_ROOMLISTWIDGETITEM_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLabel>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_roomListWidgetItem
{
public:
    QLabel *roomNameLabel;
    QPushButton *openRoomPushButton;

    void setupUi(QWidget *roomListWidgetItem)
    {
        if (roomListWidgetItem->objectName().isEmpty())
            roomListWidgetItem->setObjectName("roomListWidgetItem");
        roomListWidgetItem->resize(600, 50);
        roomNameLabel = new QLabel(roomListWidgetItem);
        roomNameLabel->setObjectName("roomNameLabel");
        roomNameLabel->setGeometry(QRect(30, 10, 271, 25));
        openRoomPushButton = new QPushButton(roomListWidgetItem);
        openRoomPushButton->setObjectName("openRoomPushButton");
        openRoomPushButton->setGeometry(QRect(430, 12, 121, 31));

        retranslateUi(roomListWidgetItem);

        QMetaObject::connectSlotsByName(roomListWidgetItem);
    } // setupUi

    void retranslateUi(QWidget *roomListWidgetItem)
    {
        roomListWidgetItem->setWindowTitle(QCoreApplication::translate("roomListWidgetItem", "Form", nullptr));
        roomNameLabel->setText(QString());
        openRoomPushButton->setText(QCoreApplication::translate("roomListWidgetItem", "\320\236\321\202\320\272\321\200\321\213\321\202\321\214", nullptr));
    } // retranslateUi

};

namespace Ui {
    class roomListWidgetItem: public Ui_roomListWidgetItem {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ROOMLISTWIDGETITEM_H
