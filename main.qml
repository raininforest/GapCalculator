import QtQuick 2.0
import QtQuick.Controls 2.5
import QtCharts 2.0


ApplicationWindow {
    id: window
    visible: true
    width: 640
    height: 480
    title: qsTr("Double calculator")


    function setdata(){
        page2.minX=-1*(page.h_v/Math.tan(page.angle_v*3.14/180))
        page2.h_v=page.h_v
        page2.d_v=(page.h_v/Math.tan(page.angle_v*3.14/180))
        page2.maxX=12
        page2.minY=0
        page2.maxY=2
        console.log(page.gap,page.table,page.h_p,page.h_v,page.angle_p,page.angle_v,page.v)


    }

    SwipeView {
        id: swipeView
        anchors.fill: parent
        currentIndex: tabBar.currentIndex
        Page1Form {
            id:page
        }
        Page2Form {
            id:page2
        }

        onCurrentIndexChanged: {
            if(currentIndex==1) {

                setdata()
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
