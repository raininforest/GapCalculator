package com.github.raininforest.android.gap.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.gap.common.BottomBar
import com.github.raininforest.android.gap.common.NoData
import com.github.raininforest.android.gap.common.TopBar
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.TextData
import com.github.raininforest.di.Dependencies
import com.github.raininforest.ui.details.GapDetailsViewModel
import com.github.raininforest.ui.details.data.GapDetailsState

@Composable
fun GapDetailsScreen(
    gapId: Long?,
    onEditClicked: (gapId: Long?) -> Unit = {},
    onBackClicked: () -> Unit = {},
    gapDetailsViewModel: GapDetailsViewModel = viewModel(
        factory = GapDetailsVMFactory(gapDetailsRepository = Dependencies.gapDetailsRepository)
    )
) {
    gapId?.let(gapDetailsViewModel::fetchGapDetails)
    val gapDetailsState by gapDetailsViewModel.gapDetails.collectAsState()

    val topBarTitle: String
    val mainContentComposable: @Composable (paddingValues: PaddingValues) -> Unit
    when (val currentState = gapDetailsState) {
        is GapDetailsState.GapDetailsData -> {
            topBarTitle = currentState.gapTitle
            mainContentComposable = {
                ChartView(
                    paddingValues = it,
                    chartData = currentState.chartData,
                    textData = currentState.textData
                )
            }
        }

        else -> {
            topBarTitle = ""
            mainContentComposable = { NoData() }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onBackClicked = onBackClicked,
                titleComposable = { modifier ->
                    Text(
                        text = topBarTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = modifier
                    )
                }
            )
        },
        content = mainContentComposable,
        bottomBar = {
            BottomBar(
                buttonText = "Редактировать",
                onButtonClicked = { onEditClicked.invoke(gapId) }
            )
        }
    )
}

@Composable
fun ChartView(paddingValues: PaddingValues, chartData: ChartData, textData: TextData) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // todo chart from lib
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        GapDetailsScreen(1)
    }
}
