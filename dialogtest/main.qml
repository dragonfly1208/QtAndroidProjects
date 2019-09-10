//import QtQuick 2.9
//import QtQuick.Controls 2.2
//import Qt.labs.platform 1.0

import QtQuick 2.2
import QtQuick.Controls 1.2
import QtQuick.Dialogs 1.1 as Dialogs11
//import QtQuick.Layouts 1.1
//import QtQuick.Window 2.0
import Qt.labs.platform 1.0

import org.qtproject.filedialog.extern 1.0

ApplicationWindow {
    id:root
    visible: true
    width: 480
    height: 640
    title: qsTr("filedialog")

    /*TabView {
        anchors.fill: parent
        anchors.margins: 8
        Tab {
            id: controlPage
            title: "File"
            FileDialogs { }
        }
    }*/

    FileDialog {
        id: fileDialog
        title: "Please choose a file"
        //folder: shortcuts.home
        fileMode: FileDialog.OpenFile
        folder: "file:///storage/emulated/0" //"file:///F:" //
        onAccepted: {
            console.log("You chose: " + fileDialog.currentFiles)
            textField.text = currentFile
            //Qt.quit()
        }
        onRejected: {
            console.log("Canceled")
            //Qt.quit()
        }
        //Component.onCompleted: visible = true
    }
    Dialogs11.FileDialog {
        id: fileDialog2
        title: "Please choose a file"
        //folder: shortcuts.homze
        folder: "file:///storage/emulated/0"  //"StandardPaths.writableLocation(StandardPaths.DocumentsLocation)//
        onAccepted: {
            console.log("You chose: " + fileDialog.fileUrls)
            textField.text = fileUrl
            //Qt.quit()
        }
        onRejected: {
            console.log("Canceled")
            //Qt.quit()
        }
        //Component.onCompleted: visible = true
    }

    FileDialogExtern{
        id:fde
        fileMode: FileDialogExtern.OpenFiles

        onAccepted: {
            console.log("onAccepted")
            textField.text = currentFile
        }
        onRejected: {
            console.log("onRejected")
        }
    }

    Column {
        id: row

        spacing: 2
        anchors.fill: parent

        Button {
            id: button
            text: qsTr("platformButton")
            onClicked: fileDialog.open();
        }

        Button {
            id: button1
            text: qsTr("Button2")
            onClicked: fileDialog2.open();
        }
        Button {
            id: btnfde
            text: qsTr("FileDialogExtern")
            onClicked: fde.open();
        }

        TextField {
            id: textField
            width: root.width
            height: 100
            placeholderText: qsTr("Text Field")
        }
    }



    /*
    SystemPalette { id: palette }
    clip: true

    //! [filedialog]
    FileDialog {
        id: fileDialog
        visible: fileDialogVisible.checked
        modality: fileDialogModal.checked ? Qt.WindowModal : Qt.NonModal
        title: fileDialogSelectFolder.checked ? "Choose a folder" :
            (fileDialogSelectMultiple.checked ? "Choose some files" : "Choose a file")
        selectExisting: fileDialogSelectExisting.checked
        selectMultiple: fileDialogSelectMultiple.checked
        selectFolder: fileDialogSelectFolder.checked
        nameFilters: [ "Image files (*.png *.jpg)", "All files (*)" ]
        selectedNameFilter: "All files (*)"
        sidebarVisible: fileDialogSidebarVisible.checked
        onAccepted: {
            console.log("Accepted: " + fileUrls)
            if (fileDialogOpenFiles.checked)
                for (var i = 0; i < fileUrls.length; ++i)
                    Qt.openUrlExternally(fileUrls[i])
        }
        onRejected: { console.log("Rejected") }
    }
*/
}
