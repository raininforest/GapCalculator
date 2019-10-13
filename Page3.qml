import QtQuick 2.0
import QtQuick.Controls 2.0

Page{
    Column{
        spacing:30
        anchors.fill: parent
        anchors.margins: 20
        Label{
            text: "Gap Calculator"
            font.pointSize: 30
        }
        Label{
            text: "Версия: 1.0\n© Велеско С.А.\nОбратная связь - sergeyvelesko@gmail.com"
            font.pointSize: 16
            color: "light gray"
        }
        Label{
            width: parent.width
            wrapMode: Text.WordWrap
            font.pointSize: 16
            text: "Приложение предназначено для предварительного расчета трамплина и грубой оценки долёта/недолёта. В расчете не учитывается сопротивление воздуха, ветер и другие факторы, которые могут повлиять на траекторию полёта."
        }
    }
}


