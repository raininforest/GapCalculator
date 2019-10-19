import QtQuick 2.2
import QtQuick.Controls 2.5
import QtQuick.Layouts 1.3

Page{
    ScrollView{
        width: parent.width
        contentWidth: column_layout.width
        anchors.left: parent.left
        anchors.leftMargin: 10
        height: parent.height
        clip: true
        ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
        ColumnLayout{
            id: column_layout
            width: page3.width-20
            spacing: 10
            Label{
                text: "Gap Calculator"
                font.pointSize: 30
                Layout.fillWidth: true
            }
            Label{
                text: "Версия: 1.2.2\nОбратная связь - sergeyvelesko@gmail.com"
                font.pointSize: 16
                color: "#555555"
                Layout.fillWidth: true
            }
            Label{
                Layout.fillWidth: true
                wrapMode: Text.Wrap

                clip: true
                color: "red"
                font.pointSize: 16
                text: "<b>ВНИМАНИЕ!!!</b>
Прыжки на трамплинах очень опасны и могут угрожать Вашему здоровью и даже жизни. Результаты работы приложения являются предварительным расчетом, который не может быть использован в качестве окончательного руководства к действиям. Ответственность за возможный риск здоровью и жизни полностью лежит на пользователе!"
            }
            Label{
                Layout.fillWidth: true
                wrapMode: Text.WordWrap
                font.pointSize: 16
                text: "Примечания:
- В расчете не учитывается сопротивление воздуха, ветер, поведение райдера, сила трения и другие факторы, которые могут повлиять на траекторию полёта.\n- Красным показаны траектории с отклонением угла вылета в 5 град. от номинального значения (зеленая траектория).\n- При расчёте рекомендуемого минимального радиуса вылета допустимая перегрузка принята 2g."
            }
        }
    }
}


