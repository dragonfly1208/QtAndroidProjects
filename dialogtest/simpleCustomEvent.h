#ifndef SIMPLECUSTOMEVENT_H
#define SIMPLECUSTOMEVENT_H
#include <QEvent>
#include <QString>
#include <QVariant>

//#include <QAndroidJniEnvironment>



class SimpleCustomEvent : public QEvent
{
public:
    SimpleCustomEvent(int requestCode = 0, int resultCode = 0, const QVariant &msg = QVariant());
    ~SimpleCustomEvent();

    static Type eventType();

    int m_requestCode;
    int m_resultCode;
    QVariant m_msg;


private:
    static Type m_evType;
};

//void onFileManager(JNIEnv *env, jobject thiz,int result, jobjectArray fileUrl);
bool registerNativeMethods();

#endif // SIMPLECUSTOMEVENT_H
