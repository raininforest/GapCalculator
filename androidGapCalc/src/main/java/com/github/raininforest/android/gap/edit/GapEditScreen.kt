package com.github.raininforest.android.gap.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.github.raininforest.android.gap.common.NoData
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.green
import com.github.raininforest.android.theme.whiteGray
import com.github.raininforest.data.GapEditRepository
import com.github.raininforest.ui.edit.GapEditViewModel
import com.github.raininforest.ui.edit.data.GapEditState

private const val PADDING = 16
private const val ITEM_SPACING = 8
private const val ITEM_HEIGHT = 72
private const val ITEM_CORNER_RADIUS = 32
private const val ITEM_LABEL_WIDTH = 192
private const val ITEM_FIELD_WIDTH = 92
private const val BUTTON_HEIGHT = 48

@Composable
fun GapEditScreen(
    gapId: String?,
    onApplyClicked: () -> Unit,
    gapEditViewModel: GapEditViewModel = viewModel(factory = GapEditVMFactory(gapEditRepository = GapEditRepository())) // todo inject this
) {
    gapId?.let(gapEditViewModel::getEditParametersForGap)
    val gapEditState by gapEditViewModel.gapEdit.collectAsState()
    when (val currentState = gapEditState) {
        is GapEditState.GapEditData -> Data(currentState, onApplyClicked)
        else -> NoData()
    }
}

@Composable
fun Data(currentState: GapEditState.GapEditData, onApplyClicked: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(all = PADDING.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp),
        ) {
            GapEditItem(label = "Гэп, м", value = currentState.gap)
            GapEditItem(label = "Стол, м", value = currentState.table)
            GapEditItem(label = "Высота вылета, м", value = currentState.startHeight)
            GapEditItem(label = "Угол вылета, град", value = currentState.startAngle)
            GapEditItem(label = "Высота приземления, м", value = currentState.finishHeight)
            GapEditItem(label = "Угол приземления, град", value = currentState.finishAngle)
            GapEditItem(label = "Скорость разгона, км/ч", value = currentState.startSpeed)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .height(BUTTON_HEIGHT.dp)
                    .fillMaxWidth(),
                onClick = onApplyClicked,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(backgroundColor = green, contentColor = whiteGray)
            ) {
                Text(
                    text = "Применить",
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun GapEditItem(label: String, value: String) {
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
        OutlinedTextField(
            modifier = Modifier
                .padding(end = PADDING.dp)
                .width(ITEM_FIELD_WIDTH.dp),
            value = value,
            onValueChange = {},
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.primary,
                cursorColor = MaterialTheme.colors.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = whiteGray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        GapEditScreen("1", {})
    }
}
