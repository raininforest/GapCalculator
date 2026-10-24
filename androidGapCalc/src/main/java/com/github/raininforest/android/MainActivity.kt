package com.github.raininforest.android

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.github.raininforest.android.theme.MAX_CONTENT_WIDTH_DP
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.raininforest.android.ui.gap.details.GapDetailsScreen
import com.github.raininforest.android.ui.gap.edit.GapEditScreen
import com.github.raininforest.android.ui.gap.list.GapListScreen
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.navigation.NavDestinations

private const val SPLASH_MIN_MS = 1200L
private const val SPLASH_FADE_MS = 350L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // switches the launch theme to AppTheme; the splash stays a little longer than the first frame needs
        // and fades out instead of vanishing
        val splashScreen = installSplashScreen()
        val splashStart = SystemClock.uptimeMillis()
        splashScreen.setKeepOnScreenCondition { SystemClock.uptimeMillis() - splashStart < SPLASH_MIN_MS }
        splashScreen.setOnExitAnimationListener { splash ->
            splash.view.animate()
                .alpha(0f)
                .setDuration(SPLASH_FADE_MS)
                .withEndAction { splash.remove() }
                .start()
        }
        super.onCreate(savedInstanceState)
        // draw behind the system bars on every API level; the screens add the insets themselves (see Main)
        enableEdgeToEdge()
        setContent {
            GapCalcTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()

    // status/navigation bars are padded here; the keyboard is handled by the screens that have text fields
    Box(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.TopCenter
    ) {
        NavHost(
            navController = navController,
            startDestination = NavDestinations.GAP_LIST.link,
            modifier = Modifier.widthIn(max = MAX_CONTENT_WIDTH_DP.dp)
        ) {
            composable(route = NavDestinations.GAP_LIST.link) {
                GapListScreen(
                    onGapClicked = { gapId ->
                        navController.navigate("${NavDestinations.GAP_DETAILS.link}/$gapId")
                    }
                )
            }
            composable(
                route = "${NavDestinations.GAP_DETAILS.link}/{${NavDestinations.GAP_DETAILS.argument}}",
                arguments = listOf(navArgument(NavDestinations.GAP_DETAILS.argument) { type = NavType.LongType })
            ) {
                GapDetailsScreen(
                    gapId = it.arguments?.getLong(NavDestinations.GAP_DETAILS.argument),
                    onEditClicked = { gapId ->
                        navController.navigate("${NavDestinations.GAP_EDIT.link}/$gapId")
                    },
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = "${NavDestinations.GAP_EDIT.link}/{${NavDestinations.GAP_EDIT.argument}}",
                arguments = listOf(navArgument(NavDestinations.GAP_EDIT.argument) { type = NavType.LongType })
            ) {
                GapEditScreen(
                    gapId = it.arguments?.getLong(NavDestinations.GAP_EDIT.argument),
                    onApplyClicked = {
                        navController.popBackStack()
                    },
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
