import QtQuick 2.0
import QtQuick.Controls 2.0
import QtCharts 2.0

Page {
    property var minX: -d_v
    property var maxX: 12
    property var minY: 0
    property var maxY: 2
    property var h_v: 0
    property var d_v: 0
    property var v_ser_property: v_ser
    property var p_ser_property: p_ser

    id: page2
    width: 600
    height: 400
    rotation: 0
    wheelEnabled: false

    ChartView {

        id: area
        anchors.fill: parent
        theme: ChartView.ChartThemeDark
        ValueAxis {
            id: xvalueAxis
            min: minX
            max: maxX
        }
        ValueAxis {
            id: yvalueAxis
            min: minY
            max: maxY
        }
        AreaSeries {
            id: v_ser
            name: "Вылет"
            axisX: xvalueAxis
            axisY: yvalueAxis
            upperSeries: LineSeries {
                XYPoint {
                    x: minX
                    y: 0
                }
                XYPoint {
                    x: d_v
                    y: h_v
                }
            }
        }
        AreaSeries {
            id: p_ser
            name: "Приземление"
            upperSeries: LineSeries {

                XYPoint {
                    x: 5
                    y: 0.8
                }

                XYPoint {
                    x: 12
                    y: 0
                }
            }
        }
    }
}
