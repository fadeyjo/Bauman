/********************************************************************************
** Form generated from reading UI file 'authwindow.ui'
**
** Created by: Qt User Interface Compiler version 6.8.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_AUTHWINDOW_H
#define UI_AUTHWINDOW_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QHBoxLayout>
#include <QtWidgets/QLabel>
#include <QtWidgets/QLineEdit>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_AuthWindow
{
public:
    QWidget *centralwidget;
    QWidget *horizontalLayoutWidget;
    QHBoxLayout *horizontalLayout;
    QLabel *label;
    QWidget *horizontalLayoutWidget_2;
    QHBoxLayout *horizontalLayout_2;
    QLineEdit *nicknameLineEdit;
    QWidget *horizontalLayoutWidget_3;
    QHBoxLayout *horizontalLayout_3;
    QPushButton *connectPushButton;
    QWidget *horizontalLayoutWidget_4;
    QHBoxLayout *horizontalLayout_4;
    QLabel *errorLabel;
    QMenuBar *menubar;
    QStatusBar *statusbar;

    void setupUi(QMainWindow *AuthWindow)
    {
        if (AuthWindow->objectName().isEmpty())
            AuthWindow->setObjectName("AuthWindow");
        AuthWindow->resize(800, 900);
        centralwidget = new QWidget(AuthWindow);
        centralwidget->setObjectName("centralwidget");
        horizontalLayoutWidget = new QWidget(centralwidget);
        horizontalLayoutWidget->setObjectName("horizontalLayoutWidget");
        horizontalLayoutWidget->setGeometry(QRect(9, 70, 781, 161));
        horizontalLayout = new QHBoxLayout(horizontalLayoutWidget);
        horizontalLayout->setObjectName("horizontalLayout");
        horizontalLayout->setContentsMargins(0, 50, 0, 50);
        label = new QLabel(horizontalLayoutWidget);
        label->setObjectName("label");
        QFont font;
        font.setPointSize(22);
        font.setBold(true);
        label->setFont(font);
        label->setAlignment(Qt::AlignmentFlag::AlignCenter);

        horizontalLayout->addWidget(label);

        horizontalLayoutWidget_2 = new QWidget(centralwidget);
        horizontalLayoutWidget_2->setObjectName("horizontalLayoutWidget_2");
        horizontalLayoutWidget_2->setGeometry(QRect(9, 310, 781, 121));
        horizontalLayout_2 = new QHBoxLayout(horizontalLayoutWidget_2);
        horizontalLayout_2->setObjectName("horizontalLayout_2");
        horizontalLayout_2->setContentsMargins(270, 0, 270, 0);
        nicknameLineEdit = new QLineEdit(horizontalLayoutWidget_2);
        nicknameLineEdit->setObjectName("nicknameLineEdit");
        QFont font1;
        font1.setPointSize(14);
        nicknameLineEdit->setFont(font1);

        horizontalLayout_2->addWidget(nicknameLineEdit);

        horizontalLayoutWidget_3 = new QWidget(centralwidget);
        horizontalLayoutWidget_3->setObjectName("horizontalLayoutWidget_3");
        horizontalLayoutWidget_3->setGeometry(QRect(10, 750, 781, 91));
        horizontalLayout_3 = new QHBoxLayout(horizontalLayoutWidget_3);
        horizontalLayout_3->setObjectName("horizontalLayout_3");
        horizontalLayout_3->setContentsMargins(270, 0, 270, 0);
        connectPushButton = new QPushButton(horizontalLayoutWidget_3);
        connectPushButton->setObjectName("connectPushButton");
        QFont font2;
        font2.setPointSize(16);
        connectPushButton->setFont(font2);

        horizontalLayout_3->addWidget(connectPushButton);

        horizontalLayoutWidget_4 = new QWidget(centralwidget);
        horizontalLayoutWidget_4->setObjectName("horizontalLayoutWidget_4");
        horizontalLayoutWidget_4->setGeometry(QRect(9, 469, 781, 91));
        horizontalLayout_4 = new QHBoxLayout(horizontalLayoutWidget_4);
        horizontalLayout_4->setObjectName("horizontalLayout_4");
        horizontalLayout_4->setContentsMargins(0, 0, 0, 0);
        errorLabel = new QLabel(horizontalLayoutWidget_4);
        errorLabel->setObjectName("errorLabel");
        errorLabel->setFont(font2);
        errorLabel->setStyleSheet(QString::fromUtf8("color: rgb(255, 0, 0)"));
        errorLabel->setAlignment(Qt::AlignmentFlag::AlignCenter);

        horizontalLayout_4->addWidget(errorLabel);

        AuthWindow->setCentralWidget(centralwidget);
        menubar = new QMenuBar(AuthWindow);
        menubar->setObjectName("menubar");
        menubar->setGeometry(QRect(0, 0, 800, 20));
        AuthWindow->setMenuBar(menubar);
        statusbar = new QStatusBar(AuthWindow);
        statusbar->setObjectName("statusbar");
        AuthWindow->setStatusBar(statusbar);

        retranslateUi(AuthWindow);

        QMetaObject::connectSlotsByName(AuthWindow);
    } // setupUi

    void retranslateUi(QMainWindow *AuthWindow)
    {
        AuthWindow->setWindowTitle(QCoreApplication::translate("AuthWindow", "AuthWindow", nullptr));
        label->setText(QCoreApplication::translate("AuthWindow", "\320\222\320\262\320\265\320\264\320\270\321\202\320\265 \320\275\320\270\320\272\320\275\320\265\320\271\320\274", nullptr));
        nicknameLineEdit->setPlaceholderText(QCoreApplication::translate("AuthWindow", "\320\235\320\270\320\272\320\275\320\265\320\271\320\274", nullptr));
        connectPushButton->setText(QCoreApplication::translate("AuthWindow", "\320\237\320\276\320\264\320\272\320\273\321\216\321\207\320\270\321\202\321\214\321\201\321\217", nullptr));
        errorLabel->setText(QString());
    } // retranslateUi

};

namespace Ui {
    class AuthWindow: public Ui_AuthWindow {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_AUTHWINDOW_H
