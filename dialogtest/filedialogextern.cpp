#include "filedialogextern.h"
#include "simpleCustomEvent.h"
#include <QEvent>
#include <QCoreApplication>

#ifdef ANDROID
#include <QtAndroidExtras/QAndroidJniObject>
#include <QFile>
#include<unistd.h>
//#include <QGuiApplication>
#include <QAndroidJniEnvironment>
#else
#include <QFileDialog>
#include <QDir>
#endif

#include <QDebug>
#define DEBUG     qDebug()<<__func__<<__LINE__

static QObject *g_listener = 0;

FileDialogExtern::FileDialogExtern(QObject *parent) : QObject(parent)
{

}


bool FileDialogExtern::event(QEvent *e)
{
#ifdef ANDROID
    if(e->type() == SimpleCustomEvent::eventType())
    {
        SimpleCustomEvent *sce = (SimpleCustomEvent*)e;
        qDebug()<<"FileDialogExtern::event:"<<sce->m_requestCode<<sce->m_resultCode;
        if(sce->m_requestCode == 3){


            if(sce->m_resultCode == -1){
                QStringList paths = sce->m_msg.toStringList();
                DEBUG<<paths.count()<<paths;
                if(paths.isEmpty()) return QObject::event(e);
                setCurrentFiles(paths);
                setCurrentFile(paths.at(0));
                emit accepted();
            }else if (sce->m_resultCode == 0) {
                DEBUG<<"cancel";
                emit rejected();
            }

        }/*else
        {
            m_captureState->setText("cancel");
        }*/
        return true;
    }
#endif
    return QObject::event(e);
}



bool FileDialogExtern::open(){
DEBUG;

#ifdef ANDROID

    //QAndroidJniObject javaAction = QAndroidJniObject::fromString(name);
    g_listener = this;
    QAndroidJniObject::callStaticMethod<void>("an/qt/extendsQtWithJava/ExtendsQtWithJava",
                                       "fileManagerActivity",
                                       "(Z)V",(m_fileMode == OpenFiles));


#else

QString fdir = m_folder.isEmpty()?QDir::currentPath():m_folder;
QStringList fpaths;
if(m_fileMode == OpenFile){
    QString fpath = QFileDialog::getOpenFileName(nullptr,m_title,fdir,"all file(*)");
    if(!fpath.isEmpty())
        fpaths.push_back(fpath);
}else if (m_fileMode == OpenFiles) {
    fpaths = QFileDialog::getOpenFileNames(nullptr,m_title,fdir,"all file(*)");
}
DEBUG<<fpaths.count()<<fpaths;
if(!fpaths.isEmpty()){
    setCurrentFiles(fpaths);
    setCurrentFile(fpaths.at(0));
    emit accepted();
}else {
    emit rejected();
}
#endif

    return true;
}

#ifdef ANDROID
void onFileManager(JNIEnv *env, jobject thiz,int result, jobjectArray fileUrls)
{
    qDebug() << "onFileManager, result - " << result ;
   int length =  env->GetArrayLength(fileUrls);
   QStringList paths;
   int req = 3;
   int ret = result;
   if(result == -1){
       //ret = 3;

       for (int i=0;i<length;i++) {
           //jstring fileUrl= dynamic_cast<jstring>(env->GetObjectArrayElement(fileUrls,i));

           QString path=QAndroidJniObject::fromLocalRef(env->GetObjectArrayElement(fileUrls,i)).toString();

          // qDebug() << "onFileManager, result - " << result << " fileUrl - " << fileUrl;

           //jboolean copy = false;
            //const char *nativeString = env->GetStringUTFChars(fileUrl, &copy);
            //qDebug() << "onFileManager, nativeString - " << nativeString;
//            QString path = nativeString;
//            env->ReleaseStringUTFChars(fileUrl, nativeString);
            if(!path.isEmpty() && QFile::exists(path))
                paths.push_back(path);
            qDebug() << "onFileManager, path - " << path;
       }
   }else if(result != 0){
       ret = -3;
       qDebug() << "could not read the captured file!";
   }
DEBUG<<paths;
    //QGuiApplication::postEvent(g_listener, new SimpleCustomEvent(ret, image));
    QCoreApplication::postEvent(g_listener, new SimpleCustomEvent(req,ret, paths));
}
#endif
