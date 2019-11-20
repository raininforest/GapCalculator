import QtQuick 2.2
import QtQuick.Controls 2.5
import QtQuick.Layouts 1.3

Page{
    ScrollView{
        width: parent.width
        contentWidth: column_layout.width
        anchors.top: parent.top
        anchors.topMargin: 10
        anchors.left: parent.left
        anchors.leftMargin: 10
        height: parent.height-20
        clip: true
        ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
        ColumnLayout{
            id: column_layout
            width: page4.width-20
            spacing: 10
            Label{
                text: "Gap Calculator"
                font.pointSize: 30
                Layout.fillWidth: true
            }
            Label{
                text: "Версия: 1.4.1\nОбратная связь - sergeyvelesko@gmail.com"
                font.pointSize: 14
                color: "#666666"
                Layout.fillWidth: true
            }
            Label{
                Layout.fillWidth: true
                wrapMode: Text.Wrap
                clip: true
                color: "white"
                font.pointSize: 14
                text: "<b>ВНИМАНИЕ!!!</b>
Прыжки на трамплинах очень опасны и могут угрожать Вашему здоровью и даже жизни. Результаты работы приложения являются предварительным расчетом, который не может быть использован в качестве окончательного руководства к действиям. Ответственность за возможный риск здоровью и жизни полностью лежит на пользователе!"
            }
            Label{
                Layout.fillWidth: true
                wrapMode: Text.WordWrap
                font.pointSize: 14
                color: "#B0B0B0"
                text: "Примечания:
- В расчете не учитывается сопротивление воздуха, ветер, поведение райдера, сила трения и другие факторы, которые могут повлиять на траекторию полёта.
- Жёлтым показаны траектории с отклонением угла вылета в 5 град. от номинального значения (зеленая траектория).
- G призем. - условная жесткость приземления. Расчитывается, как эквивалентная высота дропа на плоскость.
- При расчёте минимального радиуса вылета допустимая перегрузка принята 2g.
- Красным показана геометрия вылета с минимальным радиусом. Следите за тем, чтобы расчетный радиус был не меньше минимального.
- При значении уровня приземления >= 0 длина приземления рассчитывается до минимальной отметки на экране.
- При значении угла вылета <= 0 (дроп) длина вылета не рассчитывается, а график вылета не отображается.
"
            }
        }
    }
}


