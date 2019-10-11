import QtQuick.Controls 2.0
import QtQuick 2.0

Row{

    property string label_text
    property string edit_text

    property var row_height: (swipeView.height-5) / 7 - 5
    property var left_column_width: swipeView.width / 1.5
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
            if (text<=70){
                edit_text=text
            }
            else {
                text=""
            }
        }

    }
}
