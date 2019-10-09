import QtQuick 2.12
import QtQuick.Controls 2.5

ApplicationWindow {
    visible: true
    width: 300
    height: 450
    title: qsTr("Jump calculator")

    SwipeView {
        id: swipeView
        anchors.fill: parent
        currentIndex: tabBar.currentIndex

        Page1Form {
        }

        Page2Form {
        }

        onCurrentIndexChanged: {
            if(currentIndex==1) {
                console.log("run calculating...")
            }
        }
    }

    footer: TabBar {
        id: tabBar
        currentIndex: swipeView.currentIndex

        TabButton {
            text: qsTr("Параметры")

        }
        TabButton {
            text: qsTr("Трамплин")
        }
    }
}
