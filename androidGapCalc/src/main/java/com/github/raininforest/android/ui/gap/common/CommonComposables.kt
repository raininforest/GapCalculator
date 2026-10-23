package com.github.raininforest.android.ui.gap.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.annotation.DrawableRes
import com.github.raininforest.android.R
import androidx.compose.ui.semantics.Role
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.raininforest.android.theme.green
import com.github.raininforest.android.theme.whiteGray

private const val PADDING = 16
private const val BUTTON_HEIGHT = 48
private const val BUTTON_STROKE_WIDTH = 2
private const val PRESSED_ALPHA = 0.18f

@Composable
fun NoData() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.empty))
        }
    }
}

@Composable
fun TopBar(
    onBackClicked: () -> Unit,
    onShareClicked: () -> Unit = {},
    titleComposable: @Composable (modifier: Modifier) -> Unit,
    hasShare: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = PADDING.dp, end = PADDING.dp, bottom = PADDING.dp)
            .fillMaxWidth()
            .height(72.dp)
    ) {
        CircleIconButton(
            iconRes = R.drawable.ic_arrow_back,
            contentDescription = stringResource(R.string.back),
            onClick = onBackClicked,
            modifier = Modifier.padding(end = PADDING.dp)
        )

        titleComposable(Modifier.weight(1f))

        if (hasShare) {
            CircleIconButton(
                iconRes = R.drawable.ic_share,
                contentDescription = stringResource(R.string.share),
                onClick = onShareClicked,
                modifier = Modifier.padding(start = PADDING.dp)
            )
        }
    }
}

/** Round outlined icon button that visibly reacts to touch: tinted background while pressed plus a ripple. */
@Composable
private fun CircleIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val color = MaterialTheme.colors.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(BUTTON_HEIGHT.dp)
            .clip(CircleShape)
            .background(if (pressed) color.copy(alpha = PRESSED_ALPHA) else Color.Transparent)
            .border(BUTTON_STROKE_WIDTH.dp, color, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = contentDescription, tint = color)
    }
}

@Composable
fun BottomBar(onButtonClicked: () -> Unit, buttonText: String) {
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
            onClick = onButtonClicked,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(backgroundColor = green, contentColor = whiteGray)
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.body1
            )
        }
    }
}
