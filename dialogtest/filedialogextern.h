#ifndef FILEDIALOGEXTERN_H
#define FILEDIALOGEXTERN_H

#include <QObject>

class FileDialogExtern : public QObject
{
    Q_OBJECT
    Q_ENUMS(FileMode)
    Q_PROPERTY(NOTIFY accepted)
    Q_PROPERTY(NOTIFY rejected)
    Q_PROPERTY(QString title READ title WRITE setTitle NOTIFY titleChanged)
    Q_PROPERTY(QString folder READ folder WRITE setFolder NOTIFY folderChanged)
    Q_PROPERTY(QString currentFile READ currentFile WRITE setCurrentFile NOTIFY currentFileChanged)
    Q_PROPERTY(QStringList currentFiles READ currentFiles WRITE setCurrentFiles NOTIFY currentFilesChanged)
    Q_PROPERTY(FileMode fileMode READ fileMode WRITE setFileMode NOTIFY fileModeChanged)


public:
    enum FileMode{OpenFile,OpenFiles,Saveile};
    explicit FileDialogExtern(QObject *parent = nullptr);
    bool event(QEvent *);

    Q_INVOKABLE bool open();

    QString title() const{return m_title;}
    QString folder() const{return m_folder;}
    QString currentFile() const{return m_currentFile;}
    QStringList currentFiles() const{return m_currentFiles;}
    FileMode fileMode() const{return m_fileMode;}

signals:
    void accepted();
    void rejected();
    void titleChanged(QString title);
    void folderChanged(QString folder);
    void currentFilesChanged(QStringList currentFiles);
    void currentFileChanged(QString currentFile);
    void fileModeChanged(FileMode fileMode);

public slots:
    void setTitle(const QString &title){m_title = title; emit titleChanged(m_title);}
    void setFolder(const QString &folder){m_folder = folder; emit folderChanged(m_folder);}
    void setCurrentFile(const QString &currentFile){m_currentFile = currentFile; emit currentFileChanged(m_currentFile);}
    void setCurrentFiles(const QStringList &currentFiles){m_currentFiles = currentFiles; emit currentFilesChanged(m_currentFiles);}
    void setFileMode(const FileMode &fileMode){m_fileMode = fileMode; emit fileModeChanged(m_fileMode);}

private:
    QString m_title;
    QString m_folder;
    QString m_currentFile;
    QStringList m_currentFiles;
    FileMode m_fileMode;
};

#endif // FILEDIALOGEXTERN_H
