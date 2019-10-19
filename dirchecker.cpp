#include "dirchecker.h"

DirChecker::DirChecker(QObject *parent) : QObject(parent)
{

}

void DirChecker::check(){
    qDebug()<<"Slot started..";
    QDir dr("/storage/emulated/0/Pictures/GapCalculator");
    if (!dr.exists()){
        qDebug()<<"GapCalculator doesnt exist";

        QDir().mkdir("/storage/emulated/0/Pictures/GapCalculator");
    }
    if (!dr.exists()){
        qDebug()<<"GapCalculator was not created!";
    }
}
