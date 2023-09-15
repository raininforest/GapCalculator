package com.github.raininforest.android.gap.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.gap.common.NoData
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.green
import com.github.raininforest.android.theme.whiteGray
import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.TextData
import com.github.raininforest.ui.details.DetailsViewModel
import com.github.raininforest.ui.details.data.GapDetailsState

private const val PADDING = 16
private const val BUTTON_HEIGHT = 48
private const val BUTTON_STROKE_WIDTH = 2

@Composable
fun GapDetailsScreen(
    gapId: String?,
    onEditClicked: (gapId: String) -> Unit = {},
    onBackClicked: () -> Unit = {},
    gapDetailsViewModel: DetailsViewModel = viewModel(factory = GapDetailsVMFactory(gapDetailsRepository = GapDetailsRepository())) // todo inject
) {
    gapId?.let(gapDetailsViewModel::getGapDetails)
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
            TopBar(onBackClicked = onBackClicked, topBarTitle)
        },
        content = mainContentComposable,
        bottomBar = {
            BottomBar(gapId = gapId, onEditClicked = onEditClicked)
        }
    )
}

@Composable
fun BottomBar(gapId: String?, onEditClicked: (gapId: String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(all = PADDING.dp)
            .fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
                .height(BUTTON_HEIGHT.dp)
                .fillMaxWidth(),
            onClick = { gapId?.let(onEditClicked) },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(backgroundColor = green, contentColor = whiteGray)
        ) {
            Text(
                text = "Редактировать",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun ChartView(paddingValues: PaddingValues, chartData: ChartData, textData: TextData) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize(),
            text = "График",
            style = MaterialTheme.typography.h1
        )
    }
}

@Composable
fun TopBar(onBackClicked: () -> Unit, gapTitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(all = PADDING.dp)
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onBackClicked,
            shape = CircleShape,
            modifier = Modifier
                .safeContentPadding()
                .padding(end = PADDING.dp)
                .size(BUTTON_HEIGHT.dp),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(BUTTON_STROKE_WIDTH.dp, MaterialTheme.colors.primary),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colors.primary,
                backgroundColor = MaterialTheme.colors.background
            )
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, "")
        }
        Text(
            text = gapTitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier
                .safeContentPadding()
                .padding(start = PADDING.dp)
                .size(BUTTON_HEIGHT.dp),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(BUTTON_STROKE_WIDTH.dp, MaterialTheme.colors.primary),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colors.primary,
                backgroundColor = MaterialTheme.colors.background
            )
        ) {
            Icon(imageVector = Icons.Default.Share, "")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        GapDetailsScreen("1")
    }
}
