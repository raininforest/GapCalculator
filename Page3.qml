import QtQuick 2.0
import QtQuick.Controls 2.0

Page{
    Column{
        spacing:15
        anchors.fill: parent
        anchors.margins: 20
        Label{
            text: "Gap Calculator"
            font.pointSize: 30
        }
        Label{
            text: "Версия: 1.1\nОбратная связь - sergeyvelesko@gmail.com"
            font.pointSize: 10
            color: "light gray"
        }
        Label{
            width: parent.width
            wrapMode: Text.WordWrap
            color: "red"
            font.pointSize: 14
            text: "<b>ВНИМАНИЕ!!!</b>\nПрыжки на трамплинах очень опасны и могут угрожать Вашему здоровью и даже жизни. Результаты работы приложения являются предварительным расчетом, который не может быть использован в качестве окончательного руководства к действиям. Ответственность за возможный риск здоровью и жизни полностью лежит на пользователе!"
        }
        Label{
            width: parent.width
            wrapMode: Text.WordWrap
            font.pointSize: 14
            text: "В расчете не учитывается сопротивление воздуха, ветер, поведение райдера и другие факторы, которые могут повлиять на траекторию полёта. Красным показаны траектории с отклонением угла вылета в 5 град. от номинального значения (зеленая траектория)."
        }
    }
}


