package com.github.raininforest.android.gap.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.gap.common.BottomBar
import com.github.raininforest.android.gap.common.TopBar
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.customTextSelectionColors
import com.github.raininforest.android.theme.whiteGray
import com.github.raininforest.data.DEGREE
import com.github.raininforest.data.FINISH_ANGLE
import com.github.raininforest.data.FINISH_HEIGHT
import com.github.raininforest.data.GAP
import com.github.raininforest.data.KMH
import com.github.raininforest.data.METER
import com.github.raininforest.data.START_ANGLE
import com.github.raininforest.data.START_HEIGHT
import com.github.raininforest.data.START_SPEED
import com.github.raininforest.data.TABLE
import com.github.raininforest.di.Dependencies
import com.github.raininforest.ui.edit.GapEditViewModel
import kotlinx.coroutines.flow.MutableStateFlow

private const val PADDING = 16
private const val ITEM_SPACING = 8
private const val ITEM_HEIGHT = 72
private const val ITEM_CORNER_RADIUS = 32
private const val ITEM_LABEL_WIDTH = 192
private const val ITEM_FIELD_WIDTH = 92

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GapEditScreen(
    gapId: Long?,
    onApplyClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    gapEditViewModel: GapEditViewModel = viewModel(
        factory = GapEditVMFactory(gapEditRepository = Dependencies.gapEditRepository)
    )
) {
    gapId?.let(gapEditViewModel::fetchGapParameters)

    Scaffold(
        topBar = {
            TopBar(
                onBackClicked = onBackClicked,
                titleComposable = { modifier ->
                    CompositionLocalProvider(
                        LocalTextSelectionColors provides customTextSelectionColors,
                    ) {
                        val gapTitleState by gapEditViewModel.gapTitleState.collectAsState()
                        OutlinedTextField(
                            value = gapTitleState,
                            onValueChange = { newText ->
                                gapEditViewModel.gapTitleChanged(newText)
                            },
                            modifier = modifier
                        )
                    }
                },
                hasShare = false
            )
        },
        content = {
            Data(gapEditViewModel)
        },
        bottomBar = {
            BottomBar(
                onButtonClicked = {
                    gapEditViewModel.onApplyClicked()
                    onApplyClicked.invoke()
                },
                buttonText = "Применить"
            )
        }
    )
}

@Composable
fun Data(gapEditViewModel: GapEditViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(all = PADDING.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp),
        ) {

            GapEditItem(label = "$GAP, $METER", vmState = gapEditViewModel.gapState)
            GapEditItem(label = "$TABLE, $METER", vmState = gapEditViewModel.tableState)
            GapEditItem(label = "$START_HEIGHT, $METER", vmState = gapEditViewModel.startHeightState)
            GapEditItem(label = "$START_ANGLE, $DEGREE", vmState = gapEditViewModel.startAngleState)
            GapEditItem(label = "$FINISH_HEIGHT, $METER", vmState = gapEditViewModel.finishHeightState)
            GapEditItem(label = "$FINISH_ANGLE, $DEGREE", vmState = gapEditViewModel.finishAngleState)
            GapEditItem(label = "$START_SPEED, $KMH", vmState = gapEditViewModel.startSpeedState)

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun GapEditItem(label: String, vmState: MutableStateFlow<String>) {
    Row(
        modifier = Modifier
            .height(ITEM_HEIGHT.dp)
            .clip(shape = RoundedCornerShape(ITEM_CORNER_RADIUS.dp))
            .fillMaxWidth()
            .background(whiteGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = PADDING.dp)
                .width(ITEM_LABEL_WIDTH.dp),
            color = MaterialTheme.colors.primary,
            text = label,
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.weight(1f))

        CompositionLocalProvider(
            LocalTextSelectionColors provides customTextSelectionColors,
        ) {
            val state by vmState.collectAsState()
            OutlinedTextField(
                modifier = Modifier
                    .padding(end = PADDING.dp)
                    .width(ITEM_FIELD_WIDTH.dp),
                value = state,
                onValueChange = {
                    vmState.value = it
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.primary,
                    cursorColor = MaterialTheme.colors.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = whiteGray
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        GapEditScreen(1)
    }
}
