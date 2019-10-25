import QtQuick 2.2
import QtQuick.Controls 2.5
import QtCharts 2.3

Page{

    property bool chart_visible:false
    property real minX:0
    property real maxX
    property real minY:0
    property real maxY

    function drawpage3(){
        xvalueAxis2.min=minX
        xvalueAxis2.max=maxX
        yvalueAxis2.min=minY
        yvalueAxis2.max=maxY

        v_line_series.clear()
        v_line_bottom_series.clear()
        v_line_series_rmin.clear()
        var dcx=page2.d_v/10
        for (var cx=0;cx<page2.d_v+0.01;cx=cx+dcx){
            v_line_series.append(cx,page2.fcircle(cx,page2.r,0))
        }

        var d_v_min=page2.rmin*Math.sin(page2.angle_v)
        var h_v_min=page2.rmin-page2.rmin*Math.cos(page2.angle_v)
        var dcxmin=d_v_min/10
        for (cx=page2.d_v-d_v_min;cx<page2.d_v+0.01;cx=cx+dcxmin){
            v_line_series_rmin.append(cx,page2.fcircle(cx,page2.rmin,-page2.d_v+d_v_min))
        }

        v_line_bottom_series.append(0,0)
        v_line_bottom_series.append(page2.d_v,0)

    }

    Label{
        anchors.centerIn: parent
        text: "Информация не доступна."
    }

    ChartView {
        id: chartView2
        anchors.fill: parent
        backgroundColor: "#202020"
        backgroundRoundness: 0
        legend.visible: true
        legend.labelColor: "white"
        visible: chart_visible

//        MouseArea{
//            anchors.fill: parent
//            onDoubleClicked: {
//                check()
//                chartView2.grabToImage(function(result) {
//                    console.log("image saved: ",result.saveToFile("/storage/emulated/0/Pictures/GapCalculator/Vylet_"+ Qt.formatDateTime(new Date(),'dd.MM.yyyy.hh.mm.ss') +".png"));
//                });
//                appearence.running=true
//            }
//        }

        Rectangle{
            id: black_background
            anchors.fill: parent
            color: "black"
            opacity: 0
        }
        OpacityAnimator{
            id:appearence
            target: black_background;
            from: 1;
            to: 0;
            duration: 1000
            running: false
        }
        ValueAxis {
            id: xvalueAxis2
            min: minX
            max: maxX
            tickCount: Math.abs(maxX)+1
            labelFormat: "%i"
            minorTickCount: 3
            labelsFont:Qt.font({pointSize: 10})
            color: "grey"
            gridLineColor: "grey"
            labelsColor: "white"
            minorGridLineColor : "#555555"
        }
        ValueAxis {
            min: minY
            max: maxY
            id: yvalueAxis2
            tickCount: maxY+1
            minorTickCount: 3
            labelFormat: "%d"
            labelsFont:Qt.font({pointSize: 10})
            color: "grey"
            gridLineColor: "grey"
            labelsColor: "white"
            minorGridLineColor : "#555555"
        }
        AreaSeries {
            id: v_ser_rmin
            name: "R min"
            color: "red"
            opacity: 0.6
            borderWidth: 0
            axisX: xvalueAxis2
            axisY: yvalueAxis2
            upperSeries: LineSeries {
                id: v_line_series_rmin
            }
            //lowerSeries: v_line_bottom_series

        }
        AreaSeries {
            id: v_ser
            name: "R расчет."
            color: "green"
            opacity: 0.8
            borderWidth: 0
            axisX: xvalueAxis2
            axisY: yvalueAxis2
            upperSeries: LineSeries {
                id: v_line_series
            }
            lowerSeries: LineSeries {
                id: v_line_bottom_series
            }
        }

    }

}


