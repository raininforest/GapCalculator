#ifndef DIRCHECKER_H
#define DIRCHECKER_H

#include <QObject>
#include <QDir>
#include <QDebug>

class DirChecker : public QObject
{
    Q_OBJECT
public:
    explicit DirChecker(QObject *parent = nullptr);

signals:

public slots:
    void check();
};

#endif // DIRCHECKER_H
