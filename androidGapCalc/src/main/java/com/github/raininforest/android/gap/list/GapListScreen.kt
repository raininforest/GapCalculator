package com.github.raininforest.android.gap.list

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.green
import com.github.raininforest.android.theme.whiteGray

private const val PADDING = 16
private const val BUTTON_HEIGHT = 48
private const val ITEM_CORNER_RADIUS = 32
private const val ITEM_SPACING = 8
private const val BUTTON_STROKE_WIDTH = 1

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GapListScreen() {
    Scaffold(
        content = {
            Surface(modifier = Modifier.fillMaxSize()) {
                GapList(gapList = getExampleList())
            }
        },
        floatingActionButton = {
            Button(
                modifier = Modifier
                    .height(BUTTON_HEIGHT.dp)
                    .wrapContentWidth(),
                onClick = { },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(backgroundColor = green, contentColor = whiteGray)
            ) {
                Text(text = "Добавить")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    )
}

@Composable
fun GapList(gapList: List<GapListItemUI>) {
    LazyColumn(
        contentPadding = PaddingValues(PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp),
    ) {
        items(
            items = gapList,
            key = { it.id }
        ) {
            GapListItem(item = it)
        }
    }
}

@Composable
fun GapListItem(item: GapListItemUI) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(size = ITEM_CORNER_RADIUS.dp))
            .background(color = MaterialTheme.colors.primary)
            .padding(all = PADDING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = item.title,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body1
            )
            Text(
                text = item.date,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
                .background(Color.Red)
        )
        OutlinedButton(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier.size(BUTTON_HEIGHT.dp),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(BUTTON_STROKE_WIDTH.dp, MaterialTheme.colors.onPrimary),
            colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.onPrimary)
        ) {
            Icon(imageVector = Icons.Default.Clear, "")
        }
    }
}

fun getExampleList() = listOf(
    GapListItemUI(id = 1, title = "Untitled Gap 1", date = "12.02.2021 23:12"),
    GapListItemUI(id = 2, title = "Untitled Gap 2", date = "12.02.2022 23:12"),
    GapListItemUI(id = 3, title = "Untitled Gap 3", date = "12.02.2023 23:12"),
    GapListItemUI(id = 4, title = "Untitled Gap 4", date = "12.02.2024 23:12"),
    GapListItemUI(id = 5, title = "Untitled Gap 5", date = "12.02.2025 23:12"),
    GapListItemUI(id = 6, title = "Untitled Gap 10", date = "11.02.2025 23:12")
)

data class GapListItemUI(
    val id: Int = 0,
    val title: String,
    val date: String
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        GapListScreen()
    }
}
