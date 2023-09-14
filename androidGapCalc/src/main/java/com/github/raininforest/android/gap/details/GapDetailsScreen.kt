package com.github.raininforest.android.gap.details

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.green
import com.github.raininforest.android.theme.whiteGray

private const val PADDING = 16
private const val BUTTON_HEIGHT = 48
private const val BUTTON_STROKE_WIDTH = 2

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GapDetailsScreen(gapId: Int) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopBar()
        },
        content = {
            ChartView(it)
        },
        bottomBar = {
            BottomBar()
        }
    )
}

@Composable
fun BottomBar() {
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
            onClick = { },
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
fun ChartView(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "График",
            style = MaterialTheme.typography.h1
        )
    }
}

@Composable
fun TopBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(all = PADDING.dp)
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = {},
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
            text = "Untitled gap 1",
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
        GapDetailsScreen(1)
    }
}
