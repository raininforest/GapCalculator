package com.github.raininforest.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.raininforest.android.gap.details.GapDetailsScreen
import com.github.raininforest.android.gap.edit.GapEditScreen
import com.github.raininforest.android.gap.list.GapListScreen
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.navigation.NavDestinations

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    NavHost(navController = navController, startDestination = NavDestinations.GAP_LIST.link) {
        composable(route = NavDestinations.GAP_LIST.link) {
            GapListScreen(
                onGapClicked = { gapId ->
                    navController.navigate("${NavDestinations.GAP_DETAILS.link}/$gapId")
                },
                onAddClicked = {
                    navController.navigate("${NavDestinations.GAP_DETAILS.link}/new")
                }
            )
        }
        composable(
            route = "${NavDestinations.GAP_DETAILS.link}/{${NavDestinations.GAP_DETAILS.argument}}",
            arguments = listOf(navArgument(NavDestinations.GAP_DETAILS.argument) { type = NavType.StringType })
        ) {
            GapDetailsScreen(
                gapId = it.arguments?.getString(NavDestinations.GAP_DETAILS.argument),
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
            arguments = listOf(navArgument(NavDestinations.GAP_EDIT.argument) { type = NavType.StringType })
        ) {
            GapEditScreen(
                gapId = it.arguments?.getString(NavDestinations.GAP_EDIT.argument),
                onApplyClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}
