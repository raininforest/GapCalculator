package com.example.gapcalculator

import android.Manifest
import android.content.Context

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val logTag = "MAIN ACTIVITY"

    private val GAP: String = "gap"
    private val TABLE: String = "table"
    private val START_HEIGHT: String = "startHeight"
    private val START_ANGLE: String = "startAngle"
    private val FINISH_HEIGHT: String = "finishHeight"
    private val FINISH_ANGLE: String = "finishAngle"
    private val SPEED: String = "speed"

    //GLOBAL PARAMETERS FOR ALL FRAGMENTS AND OBJECTS
    //GLOBAL PARAMETERS FOR ALL FRAGMENTS AND OBJECTS
    //GLOBAL PARAMETERS FOR ALL FRAGMENTS AND OBJECTS
    var gap = 5.0
    var table = 2.0
    var startHeight = 1.0
    var startAngle = 30.0
    var finishHeight = 1.0
    var finishAngle = 20.0
    var speed = 35.0
    //GLOBAL PARAMETERS FOR ALL FRAGMENTS AND OBJECTS
    //GLOBAL PARAMETERS FOR ALL FRAGMENTS AND OBJECTS
    //GLOBAL PARAMETERS FOR ALL FRAGMENTS AND OBJECTS



    override fun onPause() {
        super.onPause()
        Log.d(logTag,"onPause")
        save()
    }

//    private fun getScreenShot(view: View): Bitmap {
//        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(returnedBitmap)
//        val bgDrawable = view.background
//        if (bgDrawable != null) bgDrawable.draw(canvas)
//        else canvas.drawColor(Color.WHITE)
//        view.draw(canvas)
//        return returnedBitmap
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(logTag,"onCreate")
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        //val lay = findViewById<ConstraintLayout>(R.id.container)
        //getScreenShot(lay)
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1)
        load()
    }

    private fun load(){
        val sPref = getPreferences(Context.MODE_PRIVATE) ?: return
        Log.d(logTag, "loading parameters...")
        gap = sPref.getFloat(GAP,5.0F).toDouble()
        table = sPref.getFloat(TABLE,2.0F).toDouble()
        startHeight = sPref.getFloat(START_HEIGHT,1.0F).toDouble()
        startAngle = sPref.getFloat(START_ANGLE,30.0F).toDouble()
        finishHeight = sPref.getFloat(FINISH_HEIGHT,1.0F).toDouble()
        finishAngle = sPref.getFloat(FINISH_ANGLE,20.0F).toDouble()
        speed = sPref.getFloat(SPEED,35.0F).toDouble()
    }

    private fun save(){
        val sPref = getPreferences(Context.MODE_PRIVATE) ?: return
        Log.d(logTag, "saving parameters...")
        val ed = sPref.edit()
        ed.putFloat(GAP,gap.toFloat())
        ed.putFloat(TABLE,table.toFloat())
        ed.putFloat(START_HEIGHT,startHeight.toFloat())
        ed.putFloat(START_ANGLE,startAngle.toFloat())
        ed.putFloat(FINISH_HEIGHT,finishHeight.toFloat())
        ed.putFloat(FINISH_ANGLE,finishAngle.toFloat())
        ed.putFloat(SPEED,speed.toFloat())
        ed.commit()
    }



}

