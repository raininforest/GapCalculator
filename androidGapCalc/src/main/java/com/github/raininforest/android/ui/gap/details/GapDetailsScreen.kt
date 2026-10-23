package com.github.raininforest.android.ui.gap.details

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.raininforest.android.R
import com.github.raininforest.android.ui.gap.common.shareText
import com.github.raininforest.android.ui.gap.common.textRes
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.GapCalculatorApplication.Companion.deps
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.ui.gap.common.BottomBar
import com.github.raininforest.android.ui.gap.common.NoData
import com.github.raininforest.android.ui.gap.common.TopBar
import com.github.raininforest.android.ui.gap.details.chart.ChartView
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.TextData
import com.github.raininforest.ui.details.GapDetailsViewModel
import com.github.raininforest.ui.details.data.GapDetailsState

internal const val PADDING = 16

@Composable
fun GapDetailsScreen(
    gapId: Long?,
    onEditClicked: (gapId: Long?) -> Unit = {},
    onBackClicked: () -> Unit = {},
    gapDetailsViewModel: GapDetailsViewModel = viewModel(
        factory = GapDetailsVMFactory(gapDetailsRepository = deps.gapDetailsRepository)
    )
) {
    LaunchedEffect(gapId) { gapId?.let(gapDetailsViewModel::fetchGapDetails) }
    val gapDetailsState by gapDetailsViewModel.gapDetails.collectAsState()

    val context = LocalContext.current
    val topBarTitle: String
    val mainContentComposable: @Composable (paddingValues: PaddingValues) -> Unit
    val snackbarHostState = remember { SnackbarHostState() }

    when (val currentState = gapDetailsState) {
        is GapDetailsState.GapDetailsData -> {
            topBarTitle = currentState.gapTitle.ifBlank { stringResource(R.string.untitled) }
            mainContentComposable = {
                ChartView(
                    paddingValues = it,
                    data = currentState
                )
            }
            currentState.ShowWarnings(snackbarHostState = snackbarHostState)
        }

        is GapDetailsState.GapDetailsEmpty -> {
            topBarTitle = stringResource(R.string.no_data)
            mainContentComposable = { NoData() }
        }

        is GapDetailsState.Loading -> {
            topBarTitle = ""
            mainContentComposable = {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(
                onBackClicked = onBackClicked,
                onShareClicked = {
                    (gapDetailsState as? GapDetailsState.GapDetailsData)?.let {
                        deps.shareService.share(context.shareText(it.textData))
                    }
                },
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
                buttonText = stringResource(R.string.edit),
                onButtonClicked = { onEditClicked.invoke(gapId) }
            )
        }
    )
}

@Composable
private fun GapDetailsState.GapDetailsData.ShowWarnings(snackbarHostState: SnackbarHostState) {
    if (warnings.isNotEmpty()) {
        val message = warnings.map { stringResource(it.textRes()) }.joinToString("\n")
        LaunchedEffect(key1 = this, key2 = message) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        ChartView(
            PaddingValues(0.dp),
            GapDetailsState.GapDetailsData(
                gapTitle = "",
                chartData = ChartData(emptyList()),
                textData = TextData(emptyList(), emptyList()),
                warnings = emptyList()
            )
        )
    }
}
