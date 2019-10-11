import QtQuick 2.0
import QtQuick.Controls 2.0
import QtCharts 2.0

Page {
    property real minX: -Math.ceil(Math.abs(-d_v))
    property real maxX: Math.ceil(gap+((Math.abs(minY)+h_p)/Math.abs(Math.tan(page.angle_p*3.14/180))))
    property real minY: 0
    property real maxY: Math.ceil(page.h_v+2)
    property real h_v: page.h_v
    property real d_v: (page.h_v/Math.tan(page.angle_v*3.14/180))
    property real h_p: page.h_p
    property real d_p: gap+((Math.abs(minY)+h_p)/Math.abs(Math.tan(page.angle_p*3.14/180)))
    property real gap: page.gap
    property real table: page.table

    property real v0 : page.v*1000/3600
    property real v0x : v0*Math.cos(page.angle_v*3.14/180)
    property real v0y : v0*Math.sin(page.angle_v*3.14/180)
    property real g : 9.82
    property real xbegin : 0
    property real xend : maxX
    property real dx : (xend-xbegin)/10

    function fx(x){
        if (v0x!==0){
//            console.log("V0x=",v0x,"V0y=",v0y)
            var res=(v0y*(x/v0x))-((g/2)*(Math.pow((x/v0x),2)))
            return res
        }
        else return 0
    }

    function update_series(){
        if (h_p<0) {
            minY=-Math.ceil(Math.abs(h_p))-2
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
        p_line_series.append(gap+((Math.abs(minY)+h_p)/Math.abs(Math.tan(page.angle_p*3.14/180))),minY)
        p_line_bottom_series.append(gap-table,minY)
        p_line_bottom_series.append(gap,minY)
        p_line_bottom_series.append(gap+((Math.abs(minY)+h_p)/Math.abs(Math.tan(page.angle_p*3.14/180))),minY)

        spline.clear()
        for (var px=xbegin;px<xend;px=px+dx){
                spline.append(px, h_v+fx(px))
//                console.log("px= ",px, " f(px)=", fx(px))
            }
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
        MouseArea{
            anchors.fill: parent
            onDoubleClicked: chartView.zoomReset()
        }

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
            labelFormat: "%d"
            min: minY
            max: maxY
        }
        AreaSeries {
            id: v_ser
            name: "Вылет"
            color: "dark gray"
            borderWidth: 0
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
            color: "gray"
            borderWidth: 0
            axisX: xvalueAxis
            axisY: yvalueAxis
            upperSeries: LineSeries {
                id: p_line_series
            }
            lowerSeries: LineSeries {
                id: p_line_bottom_series
            }
        }
        SplineSeries {
            id: spline
            name: "Траектория"
            color: "red"
            //opacity: 0.5
            width: 5
            style: Qt.DotLine
            axisX: xvalueAxis
            axisY: yvalueAxis
        }

    }
}
