import QtQuick.Controls 2.0
import QtQuick 2.0

Row{

    property string label_text
    property string edit_text

    property var row_height: (swipeView.height-5) / 7 - 5
    property var left_column_width: swipeView.width/3*2-6
    property var right_column_width: this_row.width-left_column_width - 5

    id: this_row
    spacing: 5
    anchors.left: parent.left
    anchors.right: parent.right
    Label{
        id: this_label
        text: label_text
        lineHeight: 1
        rightPadding: 10
        transformOrigin: Item.Center
        renderType: Text.QtRendering
        fontSizeMode: Text.Fit
        verticalAlignment: Text.AlignVCenter
        horizontalAlignment: Text.AlignRight
        wrapMode: Text.WordWrap
        font.pointSize: 18


        height: row_height
        width: left_column_width
        background: Rectangle{
            color: field_color
        }
        MouseArea{
            anchors.fill: parent
            onClicked: Qt.inputMethod.hide();
        }
    }

    TextField{
        id: this_text
        text: edit_text
        font.letterSpacing: 0
        leftPadding: 10
        cursorVisible: false


        antialiasing: true
        topPadding: 0
        bottomPadding: 0
        font.pointSize: 30
        verticalAlignment: Text.AlignVCenter
        horizontalAlignment: Text.AlignLeft
        wrapMode: Text.NoWrap
        clip: true
        color: "white"
        height: row_height
        width: right_column_width
        background: Rectangle{

            color: edit_field_color
        }
        inputMethodHints: Qt.ImhFormattedNumbersOnly
        onTextChanged: {
            if (label_text==="Гэп, м"){
                if ((text<=10)&(text>=0)) edit_text=text
                else if (text>0) text="10"
                else text="0"
            }
            else if (label_text==="Стол, м"){
                if ((text<=10)&(text>=0)&(text<=page.gap)) edit_text=text
                else if (text>page.gap) text=page.gap
                else if (text>0) text="10"
                else text="0"
            }
            else if (label_text==="Высота вылета, м"){
                if ((text<=5)&(text>=0)) edit_text=text
                else if (text>0) text="5"
                else text="0"
            }
            else if (label_text==="Высота приземления, м"){
                if ((text<=5)&(text>=-5)) edit_text=text
                else if (text<-5) edit_text="-5"
                else if (text>5) edit_text="5"
                else if (text.charAt(0)=="-") edit_text=text
                else edit_text = "0"
            }
            else if (label_text==="Угол вылета, град."){
                if ((text<=90)&(text>=-60)) edit_text=text
                else if (text<-60) edit_text="-60"
                else if (text>90) edit_text="90"
                else if (text.charAt(0)=="-") edit_text=text
                else edit_text = "0"
            }
            else if (label_text==="Угол приземления, град."){
                if ((text<=80)&(text>=0)) edit_text=text
                else if (text>0) text="80"
                else text="0"
            }
            else if (label_text==="Скорость разгона, км/ч"){
                if ((text<=70)&(text>=0)) edit_text=text
                else if (text>0) text="70"
                else text="0"
            }

        }
    }
}
