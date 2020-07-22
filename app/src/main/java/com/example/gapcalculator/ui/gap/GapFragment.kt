package com.example.gapcalculator.ui.gap

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gapcalculator.MainActivity
import com.example.gapcalculator.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*


class GapFragment : Fragment() {

    private lateinit var ma: MainActivity
    private lateinit var gapChart: LineChart
    private lateinit var saveButton: Button
    private lateinit var leftInfoPanel: TextView
    private lateinit var rightInfoPanel: TextView

    private val logTag = "GAP FRAGMENT"

    private var kScale: Double = 1.0

    private var gap = 0.0
    private var table = 0.0
    private var startHeight = 0.0
    private var startLength = 0.0
    private var startAngle = 0.0
    private var finishHeight = 0.0
    private var finishLength = 0.0
    private var finishAngle = 0.0
    private var speed = 0.0                //скорость разгона (в начале вылета) в м/с

    private var startRadiusMin = 0.0    //минимальный радиус вылета, при котором перегрузка не превышает 2g
    private var startRadius = 0.0       //фактический радиус вылета

    private var hR = 0.0                //высота разгонки. такая высота относительно начала вылета, с которой можно
                                        //развить введенную пользователем скорость разгона в начале  вылета

    private var hG = 0.0    //как жестко ты приземляешься(эквивалентная высота дропа на плоскость)
    private var xk = 0.0    //координата пересечения траектории с приземлением

    private var v0 = 0.0    //начальная скорость на конце вылета
    private var v0x = 0.0   //гориз. проекция начальной скорости
    private var v0y = 0.0   //вертик. проекция начальной скорости

    private val g = 9.80666
    private val pi = 3.14159265359


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gap, container, false)
        ma = activity as MainActivity
        gapChart = root.findViewById(R.id.gapChart)
        saveButton = root.findViewById(R.id.saveButton)

        leftInfoPanel = root.findViewById(R.id.leftInfoPanel)
        rightInfoPanel = root.findViewById(R.id.rightInfoPanel)

        //Определение соотношения сторон фрагмента
        val metrics = requireContext().resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        if (container != null) {
            kScale = (width.toDouble()/height.toDouble())
            Log.d(logTag, "kScale: $kScale")
        }

        drawChart()

        saveButton.setOnClickListener {
            val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            // получаем путь к SD
//            var screenShotDir: File = Environment.getExternalStorageDirectory()
            var screenShotDir: File? = context?.getExternalFilesDir(null)
            // добавляем свой каталог к пути
            if (screenShotDir != null) {
                screenShotDir = File(screenShotDir.absolutePath.toString() + "/" + getString(R.string.appName))
                if (!screenShotDir.exists()){
                    if (screenShotDir.mkdirs()){
                        Log.d(logTag, "Каталог создан: " + screenShotDir.absolutePath)
                    } else {
                        Toast.makeText(activity, "Невозможно создать каталог " + screenShotDir.absolutePath,Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(logTag, "Каталог уже существует: " + screenShotDir.absolutePath)
                }
                if (gapChart.saveToGallery(
                        "GapCalculator_$currentDate",
                        screenShotDir.path.toString(),
                        "",
                        Bitmap.CompressFormat.PNG,
                        100
                    )){
                    Toast.makeText(activity, getString(R.string.imageSaved) + screenShotDir.path,Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, getString(R.string.imageNotSaved) + screenShotDir.path,Toast.LENGTH_SHORT).show()
                }
            }
        }

        return root
    }

    @SuppressLint("StringFormatMatches")
    private fun drawChart(){

        //Конфигурация графика
        gapChart.description.text = ""
        gapChart.legend.isEnabled=false
        gapChart.setDrawMarkers(false)
        gapChart.setTouchEnabled(true)
        gapChart.setPinchZoom(true)
        val xAxis: XAxis = gapChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTH_SIDED

        //ИНИЦИАЛИЗАЦИЯ ВСЕХ ПАРАМЕТРОВ
        //ИНИЦИАЛИЗАЦИЯ ВСЕХ ПАРАМЕТРОВ
        //ИНИЦИАЛИЗАЦИЯ ВСЕХ ПАРАМЕТРОВ
        gap = ma.gap
        table = ma.table
        startHeight = ma.startHeight
        startAngle = ma.startAngle * pi / 180   //перевод град. в радианы
        finishHeight = ma.finishHeight
        finishAngle = ma.finishAngle * pi / 180 //перевод град. в радианы

        speed = ma.speed * 1000 / 3600             //перевод км/ч в м/с
        v0 = if (startAngle>0){
            sqrt(speed * speed - 2 * g * startHeight)
        } else {
            speed
        }
        v0x = v0 * cos(startAngle)
        v0y = v0 * sin(startAngle)
        hR = speed * speed / (2 * g)

        startRadiusMin = speed * speed / (2 * g)
        startRadius = startHeight / (1 - cos(startAngle))

        startLength = startRadius * sin(startAngle)
        finishLength = abs(finishHeight / tan(finishAngle))

        //Функция траектории полёта
        fun fx(x: Double): Double{
            return if (v0x!==0.0){
                ((v0y*(x/v0x))-((g/2)*((x / v0x).pow(2))))
            } else {
                Log.d(logTag, "in fun fx(x): v0x==0!!!")
                0.0
            }
        }

        //Функция нахождения жесткости приземления (и проверка попадания на приземление)
        fun findRoot(): Double{
            val a: Double = -g/(2*v0x*v0x)
            val b: Double = v0y/v0x+finishHeight/finishLength
            val c: Double = -(finishHeight+finishHeight*gap/finishLength)+startHeight
            val diskriminant: Double = b*b-4*a*c

            if (diskriminant<0){

                return 0.0
            }
            else {
                xk = (-b- sqrt(diskriminant))/(2*a) //точка пересечения траектории полёта и линии приземления (для расчёта жесткости приземления)
                Log.d(logTag,"Xk=$xk")
                val vk = sqrt(v0x*v0x+ (v0y - g * xk / v0x).pow(2))     //скорость в точке касания приземления
                val fi = atan((v0y-g*xk/v0x)/v0x)                           //угол касания
                Log.d(logTag,"Vk=$vk, fi=" + fi*180/pi)
                val hEqui= (vk * sin(abs(abs(fi) - finishAngle))).pow(2) /(2*g)
                if (hEqui>1){
                    // то приземление будет жестким (высота больше 1 на плоскость)
                }
                return hEqui
            }
        }

        //Функция окружности для вылета
        fun funCircle(x: Double, radius: Double, d: Double) : Double{
            //x - текущая координата
            //radius - радиус
            //d - длина вылета
            return -sqrt(radius*radius- (x + d).pow(2)) +radius
        }

        var minX = -startLength.toFloat()
        var maxX : Float = (round(gap)+4F).toFloat()
        var minY = -1F

        if (finishHeight < 0.0) {
            minY = (ceil(abs(finishHeight))-2).toFloat()
        } else if (finishHeight == 0.0){
            minY = -1F
            finishLength = abs(minY/tan(finishAngle))
        } else {
            minY = -1F
        }

        //Предупреждение о большом разрывнике
        if (gap>6){
            //Выдать предупреждение о большом трамплине
            Toast.makeText(activity, getString(R.string.bigGapWarning), Toast.LENGTH_LONG).show()
        }

        //ПОСТРОЕНИЕ ВЫЛЕТА
        val gapLineData = LineData()
        val startEntries = ArrayList<Entry>()
        val startMinREntries = ArrayList<Entry>()
        if (startAngle>0 && startAngle<89){
            //Угол вылета положительный, значит строим стандартный вылет с радиусом.
            val dcx = startLength/40
            var cx = -startLength
            while (cx < 0.01){
                startEntries.add(Entry(cx.toFloat(),funCircle(cx,startRadius,startLength).toFloat()))
                cx += dcx
            }
            //...и дополнительно строим вылет с минимальным радиусом

            val startLengthMin = startRadiusMin* sin(startAngle)
            //var startHeightMin = startRadiusMin-startRadiusMin* cos(startAngle)
            val dcxmin = startLengthMin/40
            cx = -startLengthMin
            while (cx < 0.01){
                startMinREntries.add(Entry(cx.toFloat(),funCircle(cx,startRadiusMin,startLengthMin).toFloat()))
                cx += dcxmin
            }
        }
        else if (startAngle<=0.0){
            //Угол вылета отрицательный, строим дроп с уклоном (с плоской поверхностью)
            minX = -4F
            startEntries.add(Entry(minX,(startHeight+abs(minX*tan(startAngle))).toFloat()))
            startEntries.add(Entry(0F,startHeight.toFloat()))
        }



        if (ma.startAngle<90) {
            val startMinRDataSet = LineDataSet(startMinREntries,"")
            startMinRDataSet.setDrawValues(false)
            startMinRDataSet.setDrawFilled(true)
            startMinRDataSet.setDrawCircles(false)
            startMinRDataSet.lineWidth = 1F
            startMinRDataSet.color = ContextCompat.getColor(ma.applicationContext, R.color.colorMinRStartChart)
            startMinRDataSet.fillColor = ContextCompat.getColor(ma.applicationContext, R.color.colorMinRStartChart)
            startMinRDataSet.fillAlpha = 80
            gapLineData.addDataSet(startMinRDataSet)
        }

        val startDataSet = LineDataSet(startEntries,"")
        startDataSet.setDrawValues(false)
        startDataSet.setDrawFilled(true)
        startDataSet.setDrawCircles(false)
        startDataSet.lineWidth = 5F
        startDataSet.color = ContextCompat.getColor(ma.applicationContext, R.color.colorStartChart)
        gapLineData.addDataSet(startDataSet)

        //ПОСТРОЕНИЕ ПРИЗЕМЛЕНИЯ
        val finishEntries = ArrayList<Entry>()
        if (finishHeight<0){
            minY = (-ceil(abs(finishHeight))-2).toFloat()
        } else if (finishHeight==0.0){
            minY=-1F
            finishLength = abs(minY/tan(finishAngle))
        } else{
            minY =-1F
        }

        var maxY = (ceil((abs(minX) +maxX)/kScale) - abs(minY) -1).toFloat()

        //Проверка параметров приземления
        if (finishAngle == 0.0) {
            //Строим горизонтальное плоское приземление с ограничением по длине
            finishEntries.add(Entry((gap-table).toFloat(),finishHeight.toFloat()))
            finishEntries.add(Entry(gap.toFloat(),finishHeight.toFloat()))
            finishEntries.add(Entry(maxX.toFloat(),finishHeight.toFloat()))
        } else {
            //Строим обычное приземление
            finishEntries.add(Entry((gap-table).toFloat(),finishHeight.toFloat()))
            finishEntries.add(Entry(gap.toFloat(),finishHeight.toFloat()))
            finishEntries.add(Entry((gap+(abs(minY)+finishHeight)/abs(tan(finishAngle))).toFloat(),minY.toFloat()))
        }

        val finishDataSet = LineDataSet(finishEntries,"")
        finishDataSet.setDrawValues(false)
        finishDataSet.setDrawFilled(false)
        finishDataSet.setDrawCircles(false)
        finishDataSet.lineWidth = 5F
        finishDataSet.color = ContextCompat.getColor(ma.applicationContext, R.color.colorStartChart)
        gapLineData.addDataSet(finishDataSet)

        //Проверка попадания на приземление. Жесткость приземления
        hG = findRoot()
        if (xk<gap){
            //Предупреждение о недолёте
            Toast.makeText(activity, getString(R.string.xkLessGapWarning),Toast.LENGTH_SHORT).show()
        } else if (hG > 1) {
            //вывод hg
            Toast.makeText(activity, getString(R.string.gToBigWarning),Toast.LENGTH_LONG).show()
        }

        //ПОСТРОЕНИЕ ТРАЕКТОРИИ ПОЛЁТА
        val flightEntries = ArrayList<Entry>()
        val xBegin: Double = 0.0
        val xEnd: Double = maxX.toDouble()
        val dx: Double = (xEnd-xBegin)/80
        var px: Double = 0.0
        if (ma.startAngle<89){
            //Строим сплайн по точкам
            Log.d(logTag, "startAngle<89: $startAngle")
            while (px < xEnd){
                flightEntries.add(Entry(px.toFloat(),(startHeight+fx(px)).toFloat()))
                px += dx
            }
        } else {
            //Строим вертикальную линию
            flightEntries.add(Entry(0F,startHeight.toFloat()))
            flightEntries.add(Entry(0F,(startHeight + (v0y.pow(2))/(2*g)).toFloat()))
            Log.d(logTag, "v0y: $v0y")
        }

        val flightDataSet = LineDataSet(flightEntries,"")
        flightDataSet.setDrawValues(false)
        flightDataSet.setDrawFilled(false)
        flightDataSet.setDrawCircles(false)
        flightDataSet.lineWidth = 2F
        flightDataSet.color = ContextCompat.getColor(ma.applicationContext, R.color.colorMainTraceChart)
        flightDataSet.fillAlpha = 100
        gapLineData.addDataSet(flightDataSet)

        if (ma.startAngle<84){
            val flightMinusEntries = ArrayList<Entry>()
            val flightPlusEntries = ArrayList<Entry>()
            startAngle = (ma.startAngle - 5) * pi / 180
            v0x = v0 * cos(startAngle)
            v0y = v0 * sin(startAngle)
            px = 0.0
            while (px < xEnd) {
                flightMinusEntries.add(Entry(px.toFloat(), (startHeight + fx(px)).toFloat()))
                px += dx
            }
            startAngle = (ma.startAngle + 5) * pi / 180
            v0x = v0 * cos(startAngle)
            v0y = v0 * sin(startAngle)
            px = 0.0
            while (px < xEnd) {
                flightPlusEntries.add(Entry(px.toFloat(), (startHeight + fx(px)).toFloat()))
                px += dx
            }
            startAngle = ma.startAngle * pi / 180
            v0x = v0 * cos(startAngle)
            v0y = v0 * sin(startAngle)

            val flightMinusDataSet = LineDataSet(flightMinusEntries,"")
            flightMinusDataSet.setDrawValues(false)
            flightMinusDataSet.setDrawFilled(false)
            flightMinusDataSet.setDrawCircles(false)
            flightMinusDataSet.lineWidth = 0.25F
            flightMinusDataSet.color = ContextCompat.getColor(ma.applicationContext, R.color.colorMinusTraceChart)
            gapLineData.addDataSet(flightMinusDataSet)

            val flightPlusDataSet = LineDataSet(flightPlusEntries,"")
            flightPlusDataSet.setDrawValues(false)
            flightPlusDataSet.setDrawFilled(false)
            flightPlusDataSet.setDrawCircles(false)
            flightPlusDataSet.lineWidth = 0.25F
            flightPlusDataSet.color = ContextCompat.getColor(ma.applicationContext, R.color.colorPlusTraceChart)
            gapLineData.addDataSet(flightPlusDataSet)
        }
        
        if (kScale>1){
            maxY = 15F
            maxX = (maxY*kScale + abs(minY)).toFloat()
        }
        gapChart.axisLeft.axisMaximum = maxY
        gapChart.axisLeft.axisMinimum = minY
        gapChart.axisLeft.labelCount = ceil(abs(minY)+maxY+1).toInt()
        gapChart.axisLeft.granularity = 1F
        gapChart.axisLeft.textColor = ContextCompat.getColor(ma.applicationContext, R.color.colorPrimary)

        gapChart.axisRight.axisMaximum = maxY
        gapChart.axisRight.axisMinimum = minY
        gapChart.axisRight.labelCount = ceil(abs(minY)+maxY+1).toInt()
        gapChart.axisRight.granularity = 1F
        gapChart.axisRight.textColor = ContextCompat.getColor(ma.applicationContext, R.color.colorPrimary)

        xAxis.axisMaximum = maxX
        xAxis.axisMinimum = minX
        xAxis.labelCount = ceil(abs(minX)+maxX+1).toInt()
        xAxis.granularity = 1F
        xAxis.isGranularityEnabled = true
        xAxis.textColor = ContextCompat.getColor(ma.applicationContext, R.color.colorPrimary)

        gapChart.clear()
        gapChart.data = gapLineData
        gapChart.invalidate()
        //gapChart.saveToGallery("GapCalculator_OLOLO")

        //Вывод результатов расчёта
        if (ma.startAngle>0.0) {
            leftInfoPanel.text = getString(R.string.leftInfo,
                speed*3600/1000,
                v0*3600/1000,
                startRadius,
                startRadiusMin,
                startHeight,
                startLength,
                startAngle*180/pi,
                gap,
                table
            )
        }
        else {
            leftInfoPanel.text = getString(R.string.leftInfoForDrops,
                v0*3600/1000,
                startHeight,
                startAngle*180/pi,
                gap,
                table
            )
        }
        rightInfoPanel.text = getString(
            R.string.rightInfo,
            finishLength,
            finishAngle * 180 / pi,
            hG,
            hR
        )
    }


}