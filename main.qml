import QtQuick 2.12
import QtQuick.Controls 2.5

ApplicationWindow {
    id: window
    visible: true
    width: 640
    height: 480
    title: qsTr("Double calculator")

    SwipeView {
        id: swipeView
        anchors.fill: parent
        currentIndex: tabBar.currentIndex

        Page1Form {
            id:pg1
        }

        Page2Form {
            id:pg2
        }

        onCurrentIndexChanged: {
            if(currentIndex==1) {
                console.log("run calculating...")
                //swipeView.orientation=Qt.Horizontal
            }
            else{
                //swipeView.focus=false

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
