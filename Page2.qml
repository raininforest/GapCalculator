import QtQuick 2.0
import QtQuick.Controls 2.0
import QtCharts 2.0

Page {
    property var minX: -Math.ceil(Math.abs(-d_v))
    property var maxX: Math.ceil(gap+d_p)
    property var minY: 0
    property var maxY: Math.ceil(page.h_v+1)
    property var h_v: page.h_v
    property var d_v: (page.h_v/Math.tan(page.angle_v*3.14/180))
    property var h_p: page.h_p
    property var d_p: (Math.abs(page.h_p)/Math.tan(page.angle_p*3.14/180))
    property var gap: page.gap
    property var table: page.table

    function update_series(){
        if (h_p<0) {
            minY=-Math.ceil(Math.abs(h_p))-4
        }
        else{
            minY=0
        }

        xvalueAxis.min=minX
        xvalueAxis.max=maxX
        yvalueAxis.min=minY
        yvalueAxis.max=maxY

        v_line_series.clear()
        v_line_bottom_series.clear()
        v_line_series.append(-d_v,0)
        v_line_series.append(0,h_v)
        v_line_bottom_series.append(-d_v,0)
        v_line_bottom_series.append(0,0)

        p_line_series.clear()
        p_line_bottom_series.clear()
        p_line_series.append(gap-table,h_p)
        p_line_series.append(gap,h_p)
        p_line_series.append(gap+d_p,minY)
        p_line_bottom_series.append(gap-table,minY)
        p_line_bottom_series.append(gap,minY)
        p_line_bottom_series.append(gap+d_p,minY)
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
        backgroundRoundness: 0
        ValueAxis {
            id: xvalueAxis
            tickCount: (Math.abs(minX)+maxX+1)
            labelFormat: "%d"
            minorTickCount: 1
            min: minX
            max: maxX
        }
        ValueAxis {
            id: yvalueAxis
            tickCount: (Math.abs(minY)+maxY+1)
            minorTickCount: 1
            labelFormat: "%2.1f"
            min: minY
            max: maxY
        }
        AreaSeries {
            id: v_ser
            name: "Вылет"
            color: "dark red"
            axisX: xvalueAxis
            axisY: yvalueAxis
            upperSeries: LineSeries {
                id: v_line_series
            }
            lowerSeries: LineSeries {
                id: v_line_bottom_series
            }
        }
        AreaSeries {
            id: p_ser
            name: "Приземление"
            color: "red"
            axisX: xvalueAxis
            axisY: yvalueAxis
            upperSeries: LineSeries {
                id: p_line_series
            }
            lowerSeries: LineSeries {
                id: p_line_bottom_series
            }
        }
    }
}
