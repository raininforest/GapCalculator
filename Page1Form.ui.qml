import QtQuick 2.12
import QtQuick.Controls 2.5

Page {
    id: page
    width: 600
    height: 400

    property string field_color: "#373737"
    property string edit_field_color: "#444444"
    property var gap: gap_row.edit_text
    property var table: table_row.edit_text
    property var h_v: h_v_row.edit_text
    property var h_p: h_p_row.edit_text
    property var angle_v: angle_v_row.edit_text
    property var angle_p: angle_p_row.edit_text
    property var v: v_row.edit_text



    Column {
        id: main_column
        anchors.fill: parent
        anchors.margins: 5
        spacing: 5
        Jc_row {
            id: gap_row
            label_text: "Гэп, м"
            edit_text: "4.5"

        }
        Jc_row {
            id: table_row
            label_text: "Стол, м"
            edit_text: "1"
        }
        Jc_row {
            id: h_v_row
            label_text: "Высота вылета, м"
            edit_text: "1.2"
        }
        Jc_row {
            id: h_p_row
            label_text: "Высота приземления, м"
            edit_text: "1"
        }
        Jc_row {
            id: angle_v_row
            label_text: "Угол вылета, град."
            edit_text: "20"
        }
        Jc_row {
            id: angle_p_row
            label_text: "Угол приземления, град."
            edit_text: "15"
        }
        Jc_row {
            id: v_row
            label_text: "Скорость на вылете, км/ч"
            edit_text: "30"
        }
    }
}


