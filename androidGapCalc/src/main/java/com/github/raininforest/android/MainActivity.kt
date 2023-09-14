package com.github.raininforest.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.raininforest.android.gap.edit.GapEditScreen
import com.github.raininforest.android.theme.GapCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GapCalcTheme {
                GapEditScreen(1)
            }
        }
    }
}
