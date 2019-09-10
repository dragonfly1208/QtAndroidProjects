#include "simpleCustomEvent.h"

#ifdef ANDROID
#include <QFile>
#include <QApplication>
#include <QAndroidJniEnvironment>
#include <QAndroidJniObject>
#include <jni.h>
#include "simpleCustomEvent.h"
#include <QAndroidJniObject>
#endif

QEvent::Type SimpleCustomEvent::m_evType = (QEvent::Type)QEvent::None;

SimpleCustomEvent::SimpleCustomEvent(int requestCode , int resultCode, const QVariant &msg)
    : QEvent(eventType()), m_requestCode(requestCode), m_resultCode(resultCode),m_msg(msg)
{}

SimpleCustomEvent::~SimpleCustomEvent()
{

}

QEvent::Type SimpleCustomEvent::eventType()
{
    if(m_evType == QEvent::None)
    {
        m_evType = (QEvent::Type)registerEventType();
    }
    return m_evType;
}


#ifdef ANDROID
void onFileManager(JNIEnv *env, jobject thiz,int result, jobjectArray fileUrl);
jclass g_extendsNative = 0;
bool registerNativeMethods()
{
    JNINativeMethod methods[] {
        {"onFileManager", "(I[Ljava/lang/String;)V", (void*)onFileManager}
    };

    const char *classname = "an/qt/extendsQtWithJava/ExtendsQtNative";
    jclass clazz;
    QAndroidJniEnvironment env;

    QAndroidJniObject javaClass(classname);
    clazz = env->GetObjectClass(javaClass.object<jobject>());
    //clazz = env->FindClass(classname);
    //qDebug() << "find ExtendsQtNative - " << clazz;
    bool result = false;
    if(clazz)
    {
        //g_extendsNative = static_cast<jclass>(env->NewGlobalRef(clazz));
        jint ret = env->RegisterNatives(clazz,
                                        methods,
                                        sizeof(methods) / sizeof(methods[0]));
        env->DeleteLocalRef(clazz);
        //qDebug() << "RegisterNatives return - " << ret;
        result = ret >= 0;
    }
    if(env->ExceptionCheck()) env->ExceptionClear();
    return result;
}
#endif
