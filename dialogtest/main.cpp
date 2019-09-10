//#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlEngine>
#include <QDebug>
#include <QQuickView>

#include "filedialogextern.h"
#ifdef ANDROID
#include "simpleCustomEvent.h"
#endif

#ifdef QT_WIDGETS_LIB
#include <QtWidgets/QApplication>
#else
#include <QtGui/QGuiApplication>
#endif

#ifdef QT_WIDGETS_LIB
#define QtQuickControlsApplication QApplication
#else
#define QtQuickControlsApplication QGuiApplication
#endif
#define DEBUG     qDebug()<<__func__<<__LINE__


int main(int argc, char *argv[])
{
    //QCoreApplication::setAttribute(Qt::AA_EnableHighDpiScaling);
    QtQuickControlsApplication::setAttribute(Qt::AA_EnableHighDpiScaling);

    QtQuickControlsApplication app(argc, argv);

#ifdef ANDROID
    registerNativeMethods();
#endif

//    QQuickView viewer;
//    viewer.setResizeMode(QQuickView::SizeRootObjectToView);
//    qmlRegisterType<FileDialogExtern>("org.qtproject.filedialog.extern", 1, 0, "FileDialogExtern");
//    viewer.setSource(QUrl("qrc:///main.qml"));
//    viewer.show();

    QQmlApplicationEngine engine;
    qmlRegisterType<FileDialogExtern>("org.qtproject.filedialog.extern", 1, 0, "FileDialogExtern");
    engine.load(QUrl(QStringLiteral("qrc:/main.qml")));
    if (engine.rootObjects().isEmpty())
        return -1;

    return app.exec();
}
