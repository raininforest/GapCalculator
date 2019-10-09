import QtQuick 2.12
import QtQuick.Controls 2.5

Page {
    id: page
    width: 600
    height: 400
    property var font_height: 12
    property string field_color: "#373737"
    property string edit_field_color: "#555555"
    property var row_height: page.height/7-6
    property var left_column_width: page.width/1.5
    property var right_column_width: page.width-15-gap_label.width


    Column{
        id: main_column
        anchors.fill: parent
        anchors.margins: 5
        spacing: 5
        Row{
            id: gap_row
            spacing: 5
            Label{
                id: gap_label
                text: "Гэп, м"
                rightPadding: 5
                transformOrigin: Item.Center
                //font.weightfont.weight: Font.Bold
                renderType: Text.QtRendering
                fontSizeMode: Text.Fit
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignRight
                wrapMode: Text.WordWrap

                font.pointSize: font_height
                height: row_height
                width: left_column_width
                background: Rectangle{
                    color: field_color
                }
            }

            TextArea{

                id: gap_text
                text: "4.5"
                leftPadding: 5
                antialiasing: true
                topPadding: 0
                bottomPadding: 0
                font.pointSize: 18
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignLeft
                wrapMode: Text.WordWrap
                clip: true
                color: "white"
                height: row_height
                width: right_column_width
                background: Rectangle{
                    color: edit_field_color
                }
            }
        }
        Row{
            id: table_row
            spacing:5
            Label{
                id: table_label
                text: "Стол, м"
                rightPadding: 5
                transformOrigin: Item.Center
                //font.weight: Font.Bold
                renderType: Text.QtRendering
                fontSizeMode: Text.Fit
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignRight
                wrapMode: Text.WordWrap

                font.pointSize: font_height
                height: row_height
                width: left_column_width
                background: Rectangle{
                    color: field_color
                }
            }


            TextArea{
                id: table_text
                text: "1.5"
                leftPadding: 5
                antialiasing: true
                topPadding: 0
                bottomPadding: 0
                font.pointSize: 18
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignLeft
                wrapMode: Text.WordWrap
                color: "white"
                height: row_height
                width: right_column_width
                background: Rectangle{
                    color: edit_field_color
                }
            }
        }
        Row{
            id: h_v_row
            spacing: 5

            Label{
                id: h_v_label
                text: "Высота вылета, м"
                rightPadding: 5
                transformOrigin: Item.Center
                //font.weight: Font.Bold
                renderType: Text.QtRendering
                fontSizeMode: Text.Fit
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignRight
                wrapMode: Text.WordWrap

                font.pointSize: font_height
                height: row_height
                width: left_column_width
                background: Rectangle{
                    color: field_color
                }
            }

            TextArea{
                id: h_v_text
                text: "1"
                leftPadding: 5
                antialiasing: true
                topPadding: 0
                bottomPadding: 0
                font.pointSize: 18
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignLeft
                wrapMode: Text.WordWrap
                color: "white"
                height: row_height
                width: right_column_width
                background: Rectangle{
                    color: edit_field_color
                }

            }
        }
        Row{
            id: h_p_row
            spacing:5
            Label{
                id: h_p_label
                text: "Высота призмеления, м"
                rightPadding: 5
                transformOrigin: Item.Center
                //font.weight: Font.Bold
                renderType: Text.QtRendering
                fontSizeMode: Text.Fit
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignRight
                wrapMode: Text.WordWrap

                font.pointSize: font_height
                height:row_height
                width: left_column_width
                background: Rectangle{
                    color: field_color
                }
            }
            TextArea{
                id: h_p_text
                text: "0.7"
                leftPadding: 5
                antialiasing: true
                topPadding: 0
                bottomPadding: 0
                font.pointSize: 18
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignLeft
                wrapMode: Text.WordWrap
                color: "white"
                height: row_height
                width: right_column_width
                background: Rectangle{
                    color: edit_field_color
                }
            }
        }
        Row{
            id: angle_v_row
            spacing: 5
            Label{
                id: angle_v_label
                text: "Угол вылета, град."
                rightPadding: 5
                transformOrigin: Item.Center
                //font.weight: Font.Bold
                renderType: Text.QtRendering
                fontSizeMode: Text.Fit
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignRight
                wrapMode: Text.WordWrap

                font.pointSize: font_height
                height: row_height
                width: left_column_width
                background: Rectangle{
                    color: field_color
                }
            }

            TextArea{
                id: angle_v_text
                text: "25"
                leftPadding: 5
                antialiasing: true
                topPadding: 0
                bottomPadding: 0
                font.pointSize: 18
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignLeft
                wrapMode: Text.WordWrap
                color: "white"
                height: row_height
                width: right_column_width
                background: Rectangle{
                    color: edit_field_color
                }
            }
        }
        Row{
            id: angle_p_row
            spacing:5
            Label{
                id: angle_p_label
                text: "Угол призмеления, град."
                rightPadding: 5
                transformOrigin: Item.Center
                //font.weight: Font.Bold
                renderType: Text.QtRendering
                fontSizeMode: Text.Fit
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignRight
                wrapMode: Text.WordWrap

                font.pointSize: font_height
                height: row_height
                width: left_column_width
                background: Rectangle{
                    color: field_color
                }
            }
            TextArea{
                id: angle_p_text
                text: "20"
                leftPadding: 5
                antialiasing: true
                topPadding: 0
                bottomPadding: 0
                font.pointSize: 18
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignLeft
                wrapMode: Text.WordWrap
                color: "white"
                height: row_height
                width: right_column_width
                background: Rectangle{
                    color: edit_field_color
                }
            }
        }
        Row{
            id: v_row
            spacing: 5
            Label{
                id: v_label
                text: "Скорость на вылете, км/ч"
                rightPadding: 5
                transformOrigin: Item.Center
                //font.weight: Font.Bold
                renderType: Text.QtRendering
                fontSizeMode: Text.Fit
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignRight
                wrapMode: Text.WordWrap

                font.pointSize: font_height
                height: row_height
                width: left_column_width
                background: Rectangle{
                    color: field_color
                }
            }

            TextArea{
                id: v_text
                text: "30"
                leftPadding: 5
                antialiasing: true
                topPadding: 0
                bottomPadding: 0
                font.pointSize: 18
                verticalAlignment: Text.AlignVCenter
                horizontalAlignment: Text.AlignLeft
                wrapMode: Text.WordWrap
                color: "white"
                height: row_height
                width: right_column_width
                background: Rectangle{
                    color: edit_field_color
                }
            }
        }

    }

}

