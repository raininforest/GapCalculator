package com.github.raininforest.android.ui.gap.details

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.ui.gap.common.BottomBar
import com.github.raininforest.android.ui.gap.common.NoData
import com.github.raininforest.android.ui.gap.common.TopBar
import com.github.raininforest.android.ui.gap.details.chart.ChartView
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.TextData
import com.github.raininforest.di.Dependencies
import com.github.raininforest.ui.details.GapDetailsViewModel
import com.github.raininforest.ui.details.data.GapDetailsState

internal const val PADDING = 16

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
                    data = currentState
                )
            }
        }

        else -> {
            topBarTitle = "Нет данных"
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        ChartView(
            PaddingValues(0.dp),
            GapDetailsState.GapDetailsData("", ChartData(emptyList()), TextData(emptyList(), emptyList()))
        )
    }
}