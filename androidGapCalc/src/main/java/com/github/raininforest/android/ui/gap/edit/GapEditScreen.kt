package com.github.raininforest.android.ui.gap.edit

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.raininforest.android.R
import com.github.raininforest.android.ui.gap.common.iconRes
import com.github.raininforest.android.ui.gap.common.inputLabel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.GapCalculatorApplication.Companion.deps
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.customTextSelectionColors
import com.github.raininforest.android.ui.gap.common.BottomBar
import com.github.raininforest.android.ui.gap.common.TopBar
import com.github.raininforest.ui.edit.GapEditField
import com.github.raininforest.ui.edit.GapEditViewModel

private const val PADDING = 16
private const val ITEM_SPACING = 8
private const val ITEM_HEIGHT = 72
private const val ITEM_CORNER_RADIUS = 32
private const val ITEM_ICON_SIZE = 40
private const val ITEM_FIELD_WIDTH = 124

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GapEditScreen(
    gapId: Long?,
    onApplyClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    gapEditViewModel: GapEditViewModel = viewModel(
        factory = GapEditVMFactory(gapEditRepository = deps.gapEditRepository)
    )
) {
    LaunchedEffect(gapId) { gapId?.let(gapEditViewModel::fetchGapParameters) }

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
                            placeholder = { Text(stringResource(R.string.untitled)) },
                            singleLine = true,
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
                onButtonClicked = { gapEditViewModel.onApplyClicked(onSaved = onApplyClicked) },
                buttonText = stringResource(R.string.apply)
            )
        }
    )
}

@Composable
fun Data(gapEditViewModel: GapEditViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = PADDING.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp),
        ) {
            GapEditField.entries.forEach { field ->
                GapEditItem(field = field, viewModel = gapEditViewModel)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun GapEditItem(field: GapEditField, viewModel: GapEditViewModel) {
    Row(
        modifier = Modifier
            .height(ITEM_HEIGHT.dp)
            .clip(shape = RoundedCornerShape(ITEM_CORNER_RADIUS.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colors.onPrimary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        field.parameter.iconRes()?.let { iconRes ->
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(start = PADDING.dp)
                    .size(ITEM_ICON_SIZE.dp)
            )
        }
        Text(
            modifier = Modifier
                .padding(horizontal = ITEM_SPACING.dp)
                .weight(1f),
            color = MaterialTheme.colors.primary,
            text = LocalContext.current.inputLabel(field.parameter),
            style = MaterialTheme.typography.body2.copy(fontSize = 18.sp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        CompositionLocalProvider(
            LocalTextSelectionColors provides customTextSelectionColors,
        ) {
            val state by viewModel.fieldState(field).collectAsState()
            OutlinedTextField(
                modifier = Modifier
                    .padding(end = PADDING.dp)
                    .width(ITEM_FIELD_WIDTH.dp),
                value = state,
                onValueChange = { inputString ->
                    viewModel.onFieldChanged(field, inputString)
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, fontSize = 28.sp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.primary,
                    cursorColor = MaterialTheme.colors.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = MaterialTheme.colors.onPrimary
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
