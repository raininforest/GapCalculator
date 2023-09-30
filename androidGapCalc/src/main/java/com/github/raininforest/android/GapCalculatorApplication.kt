package com.github.raininforest.android

import android.app.Application
import com.github.raininforest.di.AndroidPlatformDependencies
import com.github.raininforest.di.Dependencies

class GapCalculatorApplication : Application() {

    companion object {
        private lateinit var innerDependencies: Dependencies
        val deps: Dependencies
            get() = innerDependencies
    }

    override fun onCreate() {
        super.onCreate()

        initDependencies()
    }

    private fun initDependencies() {
        innerDependencies = Dependencies(
            platformDependencies = AndroidPlatformDependencies(context = this)
        )
    }
}