import QtQuick 2.0
import QtQuick.Controls 2.5
import QtCharts 2.0


ApplicationWindow {
    id: window
    visible: true
    width: 640
    height: 480
    title: qsTr("Gap calculator")

    SwipeView {
        id: swipeView
        anchors.fill: parent
        currentIndex: tabBar.currentIndex
        Page1Form {
            id:page
        }
        Page2 {
            id:page2
        }

        onCurrentIndexChanged: {
            if(currentIndex==1) {
                page2.update_series()
            }
            else{
                //doing nothing
            }
        }

    }

    footer: TabBar {
        id: tabBar
        height: window.height/8
        contentHeight: window.height/8
        font.pointSize: 20
        clip: false
        currentIndex: swipeView.currentIndex
        TabButton {
            id: tab1
            text: qsTr("Параметры")
        }
        TabButton {
            id: tab2
            text: qsTr("Трамплин")
        }
    }
}
