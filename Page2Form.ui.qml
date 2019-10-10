import QtQuick 2.0
import QtQuick.Controls 2.0
import QtCharts 2.0

Page {
    width: 600
    height: 400
    rotation: 0
    wheelEnabled: false

    ChartView {
        id: area
        anchors.fill: parent
        theme: ChartView.ChartThemeDark
        AreaSeries {
            name: "AreaSeries"
            upperSeries: LineSeries {
                XYPoint {
                    x: 0
                    y: 1.5
                }

                XYPoint {
                    x: 1
                    y: 3
                }

                XYPoint {
                    x: 3
                    y: 4.3
                }

                XYPoint {
                    x: 6
                    y: 1.1
                }
            }
        }
    }
}

/*##^##
Designer {
    D{i:2;anchors_height:300;anchors_width:300;anchors_x:121;anchors_y:69}
}
##^##*/

