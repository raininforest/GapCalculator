package com.example.gapcalculator.ui.input

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gapcalculator.MainActivity
import com.example.gapcalculator.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class InputFragment : Fragment() {

    private val logTag: String = "INPUT FRAGMENT"

    private lateinit var sPref: SharedPreferences
    private lateinit var ma: MainActivity

    private lateinit var gapEditText: EditText
    private lateinit var tableEditText: EditText
    private lateinit var startHeightEditText: EditText
    private lateinit var startAngleEditText: EditText
    private lateinit var finishHeightEditText: EditText
    private lateinit var finishAngleEditText: EditText
    private lateinit var speedEditText: EditText

    private fun updateValues(){
        ma.gap = gapEditText.text.toString().toDouble()
        ma.table = tableEditText.text.toString().toDouble()
        ma.startHeight = startHeightEditText.text.toString().toDouble()
        ma.startAngle = startAngleEditText.text.toString().toDouble()
        ma.finishHeight = finishHeightEditText.text.toString().toDouble()
        ma.finishAngle = finishAngleEditText.text.toString().toDouble()
        ma.speed = speedEditText.text.toString().toDouble()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        if (savedInstanceState != null) {
            Log.d(logTag,"onCreateView")
        }

        val root = inflater.inflate(R.layout.fragment_input, container, false)
        ma = activity as MainActivity

        gapEditText = root.findViewById<EditText>(R.id.editTextGap)
        tableEditText = root.findViewById<EditText>(R.id.editTextTable)
        startHeightEditText = root.findViewById<EditText>(R.id.editTextStartHeight)
        startAngleEditText = root.findViewById<EditText>(R.id.editTextStartAngle)
        finishHeightEditText = root.findViewById<EditText>(R.id.editTextFinishHeight)
        finishAngleEditText = root.findViewById<EditText>(R.id.editTextFinishAngle)
        speedEditText = root.findViewById<EditText>(R.id.editTextSpeed)

        val otherSymbols = DecimalFormatSymbols(Locale.getDefault())
        otherSymbols.decimalSeparator = '.'
        val decF = DecimalFormat("0.0", otherSymbols)

        gapEditText.setText(decF.format(ma.gap))
        tableEditText.setText(decF.format(ma.table))
        startHeightEditText.setText(decF.format(ma.startHeight))
        startAngleEditText.setText(decF.format(ma.startAngle))
        finishHeightEditText.setText(decF.format(ma.finishHeight))
        finishAngleEditText.setText(decF.format(ma.finishAngle))
        speedEditText.setText(decF.format(ma.speed))

        gapEditText.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 -> if (!p1) {
            if (gapEditText.text.toString().toDouble() < tableEditText.text.toString().toDouble()){
                tableEditText.text = gapEditText.text
                //WARNING Стол сброшен в 0, т.к. гэп равен нулю.
                Toast.makeText(activity, R.string.gapFocusWarning,Toast.LENGTH_SHORT).show()
            }
        }
            ma.gap = gapEditText.text.toString().toDouble()
        }
        tableEditText.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 ->
            if (!p1) {
                if (tableEditText.text.toString().toDouble() > gapEditText.text.toString().toDouble()){
                    gapEditText.text = tableEditText.text
                    //WARNING Стол сброшен в 0, т.к. гэп меньше стола.
                    Toast.makeText(activity, R.string.tableFocusWarning,Toast.LENGTH_SHORT).show()
                }
            }
            ma.table = tableEditText.text.toString().toDouble()
        }
        startHeightEditText.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 ->
            if (!p1) {
                if (startHeightEditText.text.toString().toDouble() == 0.0)
                {
                    if (ma.startAngle>0){
                        startAngleEditText.setText((0.0).toString())
                        //WARNING Угол сброшен в 0, т.к. высота вылета равна нулю. Сначала увеличьте высоту вылета.
                        Toast.makeText(activity, R.string.startHeightFocusWarning,Toast.LENGTH_LONG).show()
                    }
                }
                ma.startHeight = startHeightEditText.text.toString().toDouble()
            }
        }
        startAngleEditText.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 -> if (!p1) {
            if (startAngleEditText.text.toString().toDouble() > 0.0 && startHeightEditText.text.toString().toDouble() == 0.0)
            {
                startAngleEditText.setText((0.0).toString())
                //WARNING Угол сброшен в 0, т.к. высота вылета равна нулю. Для ненулевого угла увеличьте высоту вылета.
                Toast.makeText(activity, R.string.startAngleFocusWarning,Toast.LENGTH_LONG).show()
            }
            ma.startAngle = startAngleEditText.text.toString().toDouble()
            }
        }
//        finishHeightEditText.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 -> if (!p1) { ma.finishHeight = finishHeightEditText.text.toString().toDouble() } }
//        finishAngleEditText.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 -> if (!p1) { ma.finishAngle = finishAngleEditText.text.toString().toDouble() } }
//        speedEditText.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 -> if (!p1) { ma.speed = speedEditText.text.toString().toDouble() } }

        val gapKeyListener = View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (gapEditText.text.isEmpty()||gapEditText.text.toString() == ".") {
                gapEditText.setText(".0")
            } else if (gapEditText.text.toString().toDouble() > 25) {
                Toast.makeText(activity, R.string.gapFieldWarning,Toast.LENGTH_SHORT).show()
                gapEditText.setText((25.0).toString())
            }
//            if (gapEditText.text.toString().toDouble() < ma.table){
//                tableEditText.setText((ma.gap).toString())
//                Toast.makeText(activity, R.string.tableFieldWarning,Toast.LENGTH_LONG).show()
//            }
            ma.gap = gapEditText.text.toString().toDouble()
            return@OnKeyListener false
        }

        val tableKeyListener = View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (tableEditText.text.isEmpty()||tableEditText.text.toString() == ".") {
                tableEditText.setText(".0")
            } else if (tableEditText.text.toString().toDouble() > ma.gap) {
                tableEditText.setText((ma.gap).toString())
                Toast.makeText(activity, R.string.tableFieldWarning,Toast.LENGTH_SHORT).show()
            }
            ma.table = tableEditText.text.toString().toDouble()
            return@OnKeyListener false
        }

        val startHeightKeyListener = View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (startHeightEditText.text.isEmpty()||startHeightEditText.text.toString() == ".") {
                startHeightEditText.setText(".0")
            } else if (startHeightEditText.text.toString().toDouble() > 10.0) {
                startHeightEditText.setText((10.0).toString())
                Toast.makeText(activity, R.string.startHeightFieldWarning,Toast.LENGTH_SHORT).show()
            }
            ma.startHeight = startHeightEditText.text.toString().toDouble()
            return@OnKeyListener false
        }

        val startAngleKeyListener = View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (startAngleEditText.text.toString() == "-") {
                return@OnKeyListener false
            }
            if (startAngleEditText.text.isEmpty()||startAngleEditText.text.toString() == ".") {
                startAngleEditText.setText(".0")
            } else if (startAngleEditText.text.toString().toDouble() > 90) {
                startAngleEditText.setText((90.0).toString())
                Toast.makeText(activity, R.string.startAngleFieldWarning,Toast.LENGTH_SHORT).show()
            }
            else if (startAngleEditText.text.toString().toDouble() < -60) {
                startAngleEditText.setText((-60.0).toString())
                Toast.makeText(activity, R.string.startAngleFieldWarning,Toast.LENGTH_SHORT).show()
            }
            if (startAngleEditText.text.toString().toDouble() > 0.0 && ma.startHeight==0.0) {
                //Warning!!!!
                Toast.makeText(activity, R.string.startAngleFieldWarning2,Toast.LENGTH_LONG).show()
                startAngleEditText.setText("0.0")
            }
            ma.startAngle = startAngleEditText.text.toString().toDouble()
            return@OnKeyListener false
        }

        val finishHeightKeyListener = View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (finishHeightEditText.text.toString() == "-") {
                return@OnKeyListener false
            }
            if (finishHeightEditText.text.isEmpty()||finishHeightEditText.text.toString() == ".") {
                finishHeightEditText.setText(".0")
            } else if (finishHeightEditText.text.toString().toDouble() > 10) {
                finishHeightEditText.setText((10.0).toString())
                Toast.makeText(activity, R.string.finishHeightFieldWarning,Toast.LENGTH_SHORT).show()
            }
            else if (finishHeightEditText.text.toString().toDouble() < -10) {
                finishHeightEditText.setText((-10.0).toString())
                Toast.makeText(activity, R.string.finishHeightFieldWarning,Toast.LENGTH_SHORT).show()
            }
            ma.finishHeight = finishHeightEditText.text.toString().toDouble()
            return@OnKeyListener false
        }

        val finishAngleKeyListener = View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (finishAngleEditText.text.isEmpty()||finishAngleEditText.text.toString() == ".") {
                finishAngleEditText.setText(".0")
            } else if (finishAngleEditText.text.toString().toDouble() > 80) {
                finishAngleEditText.setText((80.0).toString())
                Toast.makeText(activity, R.string.finishAngleFieldWarning,Toast.LENGTH_SHORT).show()
            }
            ma.finishAngle = finishAngleEditText.text.toString().toDouble()
            return@OnKeyListener false
        }

        val speedKeyListener = View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (speedEditText.text.isEmpty()||speedEditText.text.toString() == ".") {
                speedEditText.setText(".0")
            } else if (speedEditText.text.toString().toDouble() > 80) {
                speedEditText.setText((80.0).toString())
                Toast.makeText(activity, R.string.speedFieldWarning,Toast.LENGTH_SHORT).show()
            }
            ma.speed = speedEditText.text.toString().toDouble()
            return@OnKeyListener false
        }

        gapEditText.setOnKeyListener(gapKeyListener)
        tableEditText.setOnKeyListener(tableKeyListener)
        startHeightEditText.setOnKeyListener(startHeightKeyListener)
        startAngleEditText.setOnKeyListener(startAngleKeyListener)
        finishHeightEditText.setOnKeyListener(finishHeightKeyListener)
        finishAngleEditText.setOnKeyListener(finishAngleKeyListener)
        speedEditText.setOnKeyListener(speedKeyListener)

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(logTag, "onSaveInstanceState")
    }

    override fun onPause() {
        super.onPause()
        Log.d(logTag,"onPause")
        updateValues()
    }

}