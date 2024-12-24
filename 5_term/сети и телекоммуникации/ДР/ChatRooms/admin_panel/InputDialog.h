#include <QDialog>
#include <QLabel>
#include <QLineEdit>
#include <QPushButton>
#include <QVBoxLayout>
#include <QHBoxLayout>

class InputDialog : public QDialog
{
    Q_OBJECT

public:
    InputDialog(QWidget *parent = nullptr) : QDialog(parent)
    {
        QVBoxLayout *mainLayout = new QVBoxLayout(this);
        QHBoxLayout *ipLayout = new QHBoxLayout();

        QLabel *ipLabel = new QLabel("IP Address/DNS name:");
        ipLineEdit = new QLineEdit(this);

        ipLayout->addWidget(ipLabel);
        ipLayout->addWidget(ipLineEdit);

        QPushButton *okButton = new QPushButton("OK", this);
        connect(okButton, &QPushButton::clicked, this, &InputDialog::accept);

        mainLayout->addLayout(ipLayout);
        mainLayout->addWidget(okButton);
    }

    QString getIpAddress() const
    {
        return ipLineEdit->text();
    }

private:
    QLineEdit *ipLineEdit;
};
