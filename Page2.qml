import QtQuick 2.2
import QtQuick.Controls 2.5
import QtCharts 2.3

Page {


    property var k_scale
    property real minX: -4
    property real maxX: Math.round(gap)+4
    property real minY: 0
    property real maxY
    property real h_v: page.h_v
    property real h_p: page.h_p
    property real gap: page.gap
    property real table: page.table
    property real angle_v: page.angle_v*pi/180
    property real angle_p: page.angle_p*pi/180

    property real r: vR*vR/(2*g)
    property real vR: page.v*1000/3600
    property real v0: Math.sqrt(vR*vR-2*g*h_v)
    property real v0x: v0*Math.cos(angle_v)
    property real v0y: v0*Math.sin(angle_v)

    property real g : 9.80666
    property real pi: 3.14159265359
    property real xbegin : 0
    property real xend : maxX
    property real dx : (xend-xbegin)/20

    function fx(x){

        if (v0x!==0){
            var res=(v0y*(x/v0x))-((g/2)*(Math.pow((x/v0x),2)))
            return res
        }
    }
    function update_series(){
        v0=Math.sqrt(vR*vR-2*g*h_v)
        r=vR*vR/(2*g)
        angle_v=page.angle_v*pi/180

        if (h_p<0) {
            minY=-Math.ceil(Math.abs(h_p))-2
        }
        else if (h_p==0){
            minY=-1
        }
        else {
            minY=0
        }

        if (k_scale>1){
            maxY=Math.ceil((Math.abs(minX)+maxX)/k_scale)-Math.abs(minY)-1
        }
        else{
            maxY=Math.ceil((Math.abs(minX)+maxX)/k_scale)-Math.abs(minY)-1
        }
        xvalueAxis.min=minX
        xvalueAxis.max=maxX
        yvalueAxis.min=minY
        yvalueAxis.max=maxY

        v_line_series.clear()
        v_line_bottom_series.clear()
        if (page.angle_v>0) {
            v_line_series.append(-h_v/Math.tan(angle_v),0)
            v_line_series.append(0,h_v)
            v_line_bottom_series.append(-h_v/Math.tan(angle_v),minY)
            v_line_bottom_series.append(0,minY)
        }
        else if (page.angle_v<0){
            v0=vR
            r=0
            v_line_series.append(minX, h_v+Math.abs(minX*Math.tan(angle_v)))
            v_line_series.append(0,h_v)
            v_line_bottom_series.append(minX,minY)
            v_line_bottom_series.append(0,minY)
        }
        else if (page.angle_v==0) {
            v0=vR
            r=0
            v_line_series.append(minX, h_v)
            v_line_series.append(0,h_v)
            v_line_bottom_series.append(minX,minY)
            v_line_bottom_series.append(0,minY)
        }
        p_line_series.clear()
        p_line_bottom_series.clear()
        if (page.angle_p!=0){
            p_line_series.append(gap-table,h_p)
            p_line_series.append(gap,h_p)
            p_line_series.append(gap+((Math.abs(minY)+h_p)/Math.abs(Math.tan(angle_p))),minY)
            p_line_bottom_series.append(gap-table,minY)
            p_line_bottom_series.append(gap,minY)
            p_line_bottom_series.append(gap+((Math.abs(minY)+h_p)/Math.abs(Math.tan(angle_p))),minY)
        }
        else {
            p_line_series.append(gap-table,h_p)
            p_line_series.append(gap,h_p)
            p_line_series.append(maxX,h_p)
            p_line_bottom_series.append(gap-table,minY)
            p_line_bottom_series.append(gap,minY)
            p_line_bottom_series.append(maxX,minY)
        }
        spline.clear()
        spline_minus.clear()
        spline_plus.clear()

        if (page.angle_v<89){
            for (var px=xbegin;px<xend;px=px+dx){
                spline.append(px, h_v+fx(px))
            }
            if (page.angle_v<84) {
                angle_v=angle_v-(5*pi/180)
                for (px=xbegin;px<xend;px=px+dx){
                    spline_minus.append(px, h_v+fx(px))
                }maxY=Math.ceil((Math.abs(minX)+maxX)/k_scale)-Math.abs(minY)-1
                angle_v=angle_v+10*pi/180
                for (px=xbegin;px<xend;px=px+dx){
                    spline_plus.append(px, h_v+fx(px))
                }
                angle_v=page.angle_v*pi/180
            }
        }
        else {
            spline.append(0, h_v)
            spline.append(0, h_v + Math.pow(v0y,2)/(2*g))
        }
        v0: Math.sqrt(vR*vR-2*g*h_v)
    }

    id: page2
    width: 600
    height: 400
    rotation: 0
    wheelEnabled: false

    ChartView {
        id: chartView
        anchors.fill: parent
        backgroundColor: "#202020"
        backgroundRoundness: 0
        legend.visible: false

        MouseArea{
            anchors.fill: parent
            onDoubleClicked: {
                check()
                chartView.grabToImage(function(result) {
                    console.log("result: ",result.saveToFile("/storage/emulated/0/Pictures/GapCalculator/Gap_"+ Qt.formatDateTime(new Date(),'dd.MM.yyyy.hh.mm.ss') +".png"));
                });
                appearence.running=true
            }
        }

        Rectangle{
            width: info.width+20
            height: info.height+20
            x: chartView.plotArea.x+10
            y: chartView.plotArea.y+10
            color: "#555555"
            opacity: 0.8
            Text {
                id: info
                text:"Vразг. = "+Number(vR*3600/1000).toFixed(1)+ " км/ч
Vо = "+Number(v0*3600/1000).toFixed(1)+" км/ч
R вылета min = "+Number(r).toFixed(1)+" м
Высота вылета = "+page.h_v+" м
Угол вылета = "+page.angle_v+" град.
Гэп = "+page.gap+" м
Стол = "+page.table+" м
Высота призем. = "+page.h_p+" м
Угол призем. = "+page.angle_p+" град."

                color: "white"
                font.pointSize: 10
                anchors.top: parent.top
                anchors.topMargin: 10
                anchors.horizontalCenter: parent.horizontalCenter
            }
        }
        Rectangle{
            id:black_background
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
            id: xvalueAxis
            tickCount: Math.ceil(Math.abs(minX)+maxX+1)
            labelFormat: "%d"
            minorTickCount: 1
            min: minX
            max: maxX
            labelsFont:Qt.font({pointSize: 10})
            color: "grey"
            gridLineColor: "grey"
            labelsColor: "white"
            minorGridLineColor : "#555555"
        }
        ValueAxis {
            id: yvalueAxis
            tickCount: Math.ceil(Math.abs(minY)+maxY+1)
            minorTickCount: 1
            labelFormat: "%d"
            min: minY
            max: maxY
            labelsFont:Qt.font({pointSize: 10})
            color: "grey"
            gridLineColor: "grey"
            labelsColor: "white"
            minorGridLineColor : "#555555"
        }
        AreaSeries {
            id: v_ser
            name: "Вылет"
            color: "gray"
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
            color: "green"
            width: 4
            style: Qt.DotLine
            axisX: xvalueAxis
            axisY: yvalueAxis
        }
        SplineSeries {
            id: spline_minus
            opacity: 0.8
            color: "red"
            width: 2
            style: Qt.DotLine
            axisX: xvalueAxis
            axisY: yvalueAxis
        }
        SplineSeries {
            id: spline_plus
            opacity: 0.8
            color: "red"
            width: 2
            style: Qt.DotLine
            axisX: xvalueAxis
            axisY: yvalueAxis
        }
    }
}
