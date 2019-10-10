import QtQuick 2.0
import QtQuick.Controls 2.0
import QtCharts 2.0

Page {
    property var minX: -d_v
    property var maxX: gap+d_p
    property var minY: 0
    property var maxY: 6
    property var h_v: page.h_v
    property var d_v: (page.h_v/Math.tan(page.angle_v*3.14/180))
    property var h_p: page.h_p
    property var d_p: (page.h_p/Math.tan(page.angle_p*3.14/180))
    property var gap: page.gap
    property var table: page.table

    function update_series(){
        xvalueAxis.min=minX
        xvalueAxis.max=maxX
        yvalueAxis.min=minY
        yvalueAxis.max=maxY

        v_line_series.clear()
        v_line_series.append(-d_v,0)
        v_line_series.append(0,h_v)


        p_line_series.clear()
        p_line_series.append(gap-table,h_p)
        p_line_series.append(gap,h_p)
        p_line_series.append(gap+d_p,0)
    }

    id: page2
    width: 600
    height: 400
    rotation: 0
    wheelEnabled: false

    ChartView {

        id: chartView
        anchors.fill: parent
        theme: ChartView.ChartThemeDark
        ValueAxis {
            id: xvalueAxis

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
                id: v_line_series
            }
        }
        AreaSeries {
            id: p_ser
            name: "Приземление"
            upperSeries: LineSeries {
                id: p_line_series
            }
        }
    }
}
