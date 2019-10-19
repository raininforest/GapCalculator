#include <QtWidgets/QApplication>
#include <QQmlApplicationEngine>
#include <QDebug>
#include <QtAndroid>
#include <QObject>
#include "dirchecker.h"

int main(int argc, char *argv[])
{
    QCoreApplication::setAttribute(Qt::AA_EnableHighDpiScaling);

    QApplication app(argc, argv);
    QQmlApplicationEngine engine;
    const QUrl url(QStringLiteral("qrc:/main.qml"));
    QObject::connect(&engine, &QQmlApplicationEngine::objectCreated,
                     &app, [url](QObject *obj, const QUrl &objUrl) {
        if (!obj && url == objUrl)
            QCoreApplication::exit(-1);
    }, Qt::QueuedConnection);
    engine.load(url);
    QtAndroid::hideSplashScreen();
    DirChecker drck;
    QObject *mainwindow = engine.rootObjects().at(0);
    QtAndroid::requestPermissionsSync(QStringList({"android.permission.WRITE_EXTERNAL_STORAGE"}),5000);
    QObject::connect(mainwindow,SIGNAL(check()),&drck,SLOT(check()));
    return app.exec();
}
