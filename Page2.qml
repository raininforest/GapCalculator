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
    property real d_v: r*Math.sin(angle_v)
    property real h_p: page.h_p
    property real d_p: Math.abs(h_p/Math.tan(angle_p))
    property real gap: page.gap
    property real table: page.table
    property real angle_v: page.angle_v*pi/180
    property real angle_p: page.angle_p*pi/180
    property real hr: vR*vR/(2*g)

    property real rmin: vR*vR/(2*g)
    property real r: h_v/(1-Math.cos(angle_v))
    property real vR: page.v*1000/3600
    property real v0: Math.sqrt(vR*vR-2*g*h_v)
    property real v0x: v0*Math.cos(angle_v)
    property real v0y: v0*Math.sin(angle_v)

    property real g : 9.80666
    property real pi: 3.14159265359
    property real xbegin : 0
    property real xend : maxX
    property real dx : (xend-xbegin)/40

    property real xk //crossing coordinate
    property real hg //how strong u land(eq drop height)

    function fx(x){
        if (v0x!==0){
            var res=(v0y*(x/v0x))-((g/2)*(Math.pow((x/v0x),2)))
            return res
        }
    }
    function find_root(){

        var a=-g/(2*v0x*v0x)
        var b=v0y/v0x+h_p/d_p
        var c=-(h_p+h_p*gap/d_p)+h_v
        var diskr=b*b-4*a*c
        console.log("a=",a, "b=", b, "c=", c, "diskr=", diskr)
        if (diskr<0){
            console.log("Нет пересечения траектории с приземлением!", diskr)
            return 0
        }
        else {
            xk=(-b-Math.sqrt(diskr))/(2*a)
            console.log("Xk=",xk)
            var vk=Math.sqrt(v0x*v0x+Math.pow((v0y-g*xk/v0x),2))
            var fi=Math.atan((v0y-g*xk/v0x)/v0x)
            console.log("Vk=",vk, "fi=", fi*180/pi)
            var h_equi=Math.pow((vk*Math.sin(Math.abs(Math.abs(fi)-angle_p))),2)/(2*g)
            if (h_equi>1){

            }
            return h_equi
        }
    }

    function fcircle(x,radius,d){
        var res=-Math.sqrt(radius*radius-Math.pow((x+d),2))+radius
        return res
    }

    function update_series(){
        d_p=Math.abs(h_p/Math.tan(angle_p))
        if (h_p<0) {
            minY=-Math.ceil(Math.abs(h_p))-2
        }
        else if (h_p==0){
            minY=-1
            d_p=Math.abs(minY/Math.tan(angle_p))
        }
        else {
            minY=0
        }

        if((gap>6)&(gap<20)){
            xvalueAxis.labelsFont.pointSize=8
            yvalueAxis.labelsFont.pointSize=8
            if (!window.was_warning){
                warning_label.text="Вы хотите рассчитать большой трамплин.\n\nПомните, что в расчёте не учитывается сопротивление воздуха, которое может сильно повлиять на вашу скорость в полёте, что в свою очередь заметно повлияет на место приземления.
Возрастает риск не долететь."
                warning_rect.visible=true
                window.was_warning=true
            }
        }
        else if(gap>20){
            xvalueAxis.labelsFont.pointSize=6
            yvalueAxis.labelsFont.pointSize=6
            if (!window.was_warning){
                warning_label.text="Вы хотите рассчитать большой трамплин.\n\nПомните, что в расчёте не учитывается сопротивление воздуха, которое может сильно повлиять на вашу скорость в полёте, что в свою очередь заметно повлияет на место приземления.
Возрастает риск не долететь."
                warning_rect.visible=true
                window.was_warning=true
            }
        }
        else {
            xvalueAxis.labelsFont.pointSize=10
            yvalueAxis.labelsFont.pointSize=10
        }

        v0=Math.sqrt(vR*vR-2*g*h_v)
        rmin=vR*vR/(2*g)
        angle_v=page.angle_v*pi/180
        r=h_v/(1-Math.cos(angle_v))
        d_v=r*Math.sin(angle_v)

        hr=vR*vR/(2*g)


        maxY=Math.ceil((Math.abs(minX)+maxX)/k_scale)-Math.abs(minY)-1
        page3.maxY=Math.ceil((d_v+1)/k_scale)-1
        page3.maxX=Math.ceil(d_v)+1

        xvalueAxis.min=minX
        xvalueAxis.max=maxX
        yvalueAxis.min=minY
        yvalueAxis.max=maxY



        v_line_series.clear()
        v_line_bottom_series.clear()
        v_line_series_rmin.clear()
        if ((page.angle_v>0)&(page.angle_v<=89)) {
//            v_line_series.append(-h_v/Math.tan(angle_v),0)
//            v_line_series.append(0,h_v)
            var dcx=d_v/10
            for (var cx=-d_v;cx<0.01;cx=cx+dcx){
                v_line_series.append(cx,fcircle(cx,r,d_v))
            }
            var d_v_min=rmin*Math.sin(angle_v)
            var h_v_min=rmin-rmin*Math.cos(angle_v)
            var dcxmin=d_v_min/10
            for (cx=-d_v_min;cx<0.01;cx=cx+dcxmin){
                v_line_series_rmin.append(cx,fcircle(cx,rmin,d_v_min))
            }
            v_line_bottom_series.append(-h_v/Math.tan(angle_v),0)
            v_line_bottom_series.append(0,0)
            page3.chart_visible=true
            page3.drawpage3()

        }
        else if (page.angle_v<0){
            v0=vR
            rmin=0
            r=0
            d_v=0
            v_line_series.append(minX, h_v+Math.abs(minX*Math.tan(angle_v)))
            v_line_series.append(0,h_v)
            v_line_bottom_series.append(minX,0)
            v_line_bottom_series.append(0,0)
            page3.chart_visible=false
        }
        else if (page.angle_v==0) {
            v0=vR
            rmin=0
            r=0
            d_v=0
            v_line_series.append(minX, h_v)
            v_line_series.append(0,h_v)
            v_line_bottom_series.append(minX,minY)
            v_line_bottom_series.append(0,minY)
            page3.chart_visible=false
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

            hg=find_root()
            console.log("Hg=",hg)
//        if (h_p<=0) {
//            d_p=0
//        }
        if(xk>gap){
            if (hg>1) {
                warning_label2.text="Приземление будет жестким.\n\nОно будет эквивалентно дропу на плоскость с высоты "+Number(hg).toFixed(2)+" м.
Попробуйте сделать угол приземления более пологим, чтобы он отличался от угла вылета не более, чем на 10 градусов."
                warning_rect2.visible=true
            }
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
        //v0=Math.sqrt(vR*vR-2*g*h_v)
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
                    console.log("image saved: ",result.saveToFile("/storage/emulated/0/Pictures/GapCalculator/Gap_"+ Qt.formatDateTime(new Date(),'dd.MM.yyyy.hh.mm.ss') +".png"));
                });
                appearence.running=true
            }
        }

        Rectangle{
            width: info.width+10
            height: info.height+20
            x: chartView.plotArea.x+5
            y: chartView.plotArea.y+5
            color: "#555555"
            opacity: 0.6
            Text {
                id: info
                text:"Vразг. = "+Number(vR*3600/1000).toFixed(1)+ " км/ч
Vо = "+Number(v0*3600/1000).toFixed(1)+" км/ч
R вылета = "+Number(r).toFixed(1)+" м
R вылета min = "+Number(rmin).toFixed(1)+" м
Высота вылета = "+page.h_v+" м
Длина вылета = " +Number(d_v).toFixed(1)+" м
Угол вылета = "+page.angle_v+" град.

Гэп = "+page.gap+" м
Стол = "+page.table+" м"

                color: "white"
                font.pointSize: 10
                anchors.top: parent.top
                anchors.topMargin: 10
                anchors.horizontalCenter: parent.horizontalCenter
            }
        }
        Rectangle{
            width: info2.width+10
            height: info2.height+20
            x: chartView.plotArea.x+chartView.plotArea.width-width-5
            y: chartView.plotArea.y+5
            color: "#555555"
            opacity: 0.6
            Text {
                id: info2
                text:"Уровень призем. = "+page.h_p+" м
Длина призем. = " +Number(d_p).toFixed(1)+" м
Угол призем. = "+page.angle_p+" град.

G призем. = "+Number(hg).toFixed(2)+" м.
Высота разгонки = "+Number(hr).toFixed(1)+" м"

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
            id: v_ser_rmin
            name: "Вылет"
            color: "red"
            opacity: 1
            borderWidth: 0
            axisX: xvalueAxis
            axisY: yvalueAxis
            upperSeries: LineSeries {
                id: v_line_series_rmin
            }
            lowerSeries: v_line_bottom_series

        }
        AreaSeries {
            id: v_ser
            name: "Вылет"
            color: "gray"
            opacity: 0.8
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
            color: "yellow"
            width: 2
            style: Qt.DotLine
            axisX: xvalueAxis
            axisY: yvalueAxis
        }
        SplineSeries {
            id: spline_plus
            opacity: 0.8
            color: "yellow"
            width: 2
            style: Qt.DotLine
            axisX: xvalueAxis
            axisY: yvalueAxis
        }

        Rectangle{
            id: warning_rect2
            anchors.fill: parent
            visible: false
            color: "#000000"
            Label{
                y:window.height/8
                id: warning_label2
                horizontalAlignment: Text.AlignHCenter
                font.pointSize: 18
                color: "white"
                wrapMode: "WordWrap"
                clip: true
                width: parent.width
            }
            Button{
                id:ok_warning2
                width:window.width/2
                height: 60
                anchors.horizontalCenter: parent.horizontalCenter
                anchors.top: warning_label2.bottom
                anchors.topMargin: 20
                text: "Понятно"
                font.pointSize: 18
                onClicked: {
                    warning_rect2.visible=false
                }
            }
        }
        Rectangle{
            id: warning_rect
            anchors.fill: parent
            visible: false
            color: "#000000"
            Label{
                y:window.height/8
                id: warning_label
                horizontalAlignment: Text.AlignHCenter
                font.pointSize: 18
                color: "white"
                wrapMode: "WordWrap"
                clip: true
                width: parent.width
            }
            Button{
                id:ok_warning
                width:window.width/2
                height: 60
                anchors.horizontalCenter: parent.horizontalCenter
                anchors.top: warning_label.bottom
                anchors.topMargin: 20
                text: "Понятно"
                font.pointSize: 18
                onClicked: {
                    warning_rect.visible=false                    
                }
            }
        }
    }
}
