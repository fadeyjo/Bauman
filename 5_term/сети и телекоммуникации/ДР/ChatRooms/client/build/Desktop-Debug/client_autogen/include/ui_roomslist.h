/********************************************************************************
** Form generated from reading UI file 'roomslist.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ROOMSLIST_H
#define UI_ROOMSLIST_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLineEdit>
#include <QtWidgets/QListWidget>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_RoomsList
{
public:
    QWidget *centralwidget;
    QLineEdit *newRoomLineEdit;
    QPushButton *newRoomPushButton;
    QListWidget *roomsListWidget;
    QMenuBar *menubar;
    QStatusBar *statusbar;

    void setupUi(QMainWindow *RoomsList)
    {
        if (RoomsList->objectName().isEmpty())
            RoomsList->setObjectName("RoomsList");
        RoomsList->resize(800, 600);
        centralwidget = new QWidget(RoomsList);
        centralwidget->setObjectName("centralwidget");
        newRoomLineEdit = new QLineEdit(centralwidget);
        newRoomLineEdit->setObjectName("newRoomLineEdit");
        newRoomLineEdit->setGeometry(QRect(90, 20, 190, 25));
        newRoomPushButton = new QPushButton(centralwidget);
        newRoomPushButton->setObjectName("newRoomPushButton");
        newRoomPushButton->setGeometry(QRect(330, 20, 130, 25));
        roomsListWidget = new QListWidget(centralwidget);
        roomsListWidget->setObjectName("roomsListWidget");
        roomsListWidget->setGeometry(QRect(90, 90, 630, 461));
        RoomsList->setCentralWidget(centralwidget);
        menubar = new QMenuBar(RoomsList);
        menubar->setObjectName("menubar");
        menubar->setGeometry(QRect(0, 0, 800, 20));
        RoomsList->setMenuBar(menubar);
        statusbar = new QStatusBar(RoomsList);
        statusbar->setObjectName("statusbar");
        RoomsList->setStatusBar(statusbar);

        retranslateUi(RoomsList);

        QMetaObject::connectSlotsByName(RoomsList);
    } // setupUi

    void retranslateUi(QMainWindow *RoomsList)
    {
        RoomsList->setWindowTitle(QCoreApplication::translate("RoomsList", "MainWindow", nullptr));
        newRoomLineEdit->setPlaceholderText(QCoreApplication::translate("RoomsList", "\320\235\320\260\320\267\320\262\320\260\320\275\320\270\320\265 \320\272\320\276\320\274\320\275\320\260\321\202\321\213", nullptr));
        newRoomPushButton->setText(QCoreApplication::translate("RoomsList", "\320\224\320\276\320\261\320\260\320\262\320\270\321\202\321\214 \320\272\320\276\320\274\320\275\320\260\321\202\321\203", nullptr));
    } // retranslateUi

};

namespace Ui {
    class RoomsList: public Ui_RoomsList {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ROOMSLIST_H
