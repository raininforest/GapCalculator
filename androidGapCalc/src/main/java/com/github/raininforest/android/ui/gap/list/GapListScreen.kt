package com.github.raininforest.android.ui.gap.list

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Card
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.GapCalculatorApplication.Companion.deps
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.green
import com.github.raininforest.android.theme.whiteGray
import com.github.raininforest.android.ui.gap.common.NoData
import com.github.raininforest.ui.list.GapListViewModel
import com.github.raininforest.ui.list.data.GapListItem
import com.github.raininforest.ui.list.data.GapListState

private const val PADDING = 16
private const val BUTTON_HEIGHT = 48
private const val ITEM_CORNER_RADIUS = 32
private const val ITEM_SPACING = 8
private const val BUTTON_STROKE_WIDTH = 1

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GapListScreen(
    onGapClicked: (gapId: Long) -> Unit = {},
    gapListViewModel: GapListViewModel = viewModel(
        factory = GapListVMFactory(gapListRepository = deps.gapListRepository)
    )
) {
    gapListViewModel.fetchList() // todo сделать swipe refresh

    val gapListState by gapListViewModel.gapList.collectAsState()

    Scaffold(
        content = {
            Content(
                gapListState = gapListState,
                onGapClicked = onGapClicked,
                onGapRemoveClicked = gapListViewModel::removeGapClicked
            )
        },
        floatingActionButton = {
            FloatingButton(onAddClicked = gapListViewModel::addClicked)
        },
        floatingActionButtonPosition = FabPosition.Center
    )
}

@Composable
fun Content(
    gapListState: GapListState,
    onGapClicked: (gapId: Long) -> Unit,
    onGapRemoveClicked: (gapId: Long) -> Unit
) {

    when (gapListState) {
        is GapListState.GapListData -> Data(
            currentState = gapListState,
            onGapClicked = onGapClicked,
            onGapRemoveClicked = onGapRemoveClicked
        )

        else -> NoData()
    }
}

@Composable
fun FloatingButton(onAddClicked: () -> Unit) {
    Button(
        modifier = Modifier
            .height(BUTTON_HEIGHT.dp)
            .wrapContentWidth(),
        onClick = onAddClicked,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(backgroundColor = green, contentColor = whiteGray)
    ) {
        Text(
            text = "Добавить",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun Data(
    currentState: GapListState.GapListData,
    onGapClicked: (gapId: Long) -> Unit,
    onGapRemoveClicked: (gapId: Long) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        GapList(
            gapList = currentState.gapList,
            onGapClicked = onGapClicked,
            onGapRemoveClicked = onGapRemoveClicked
        )
    }
}

@Composable
fun GapList(
    gapList: List<GapListItem>,
    onGapClicked: (gapId: Long) -> Unit,
    onGapRemoveClicked: (gapId: Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp),
    ) {
        items(
            items = gapList,
            key = { gapListItem -> gapListItem.id }
        ) { gapListItem ->
            GapListItemView(
                item = gapListItem,
                onGapClicked = onGapClicked,
                onGapRemoveClicked = onGapRemoveClicked
            )
        }
    }
}

@Composable
fun GapListItemView(
    item: GapListItem,
    onGapClicked: (gapId: Long) -> Unit,
    onGapRemoveClicked: (gapId: Long) -> Unit
) {
    val shape = RoundedCornerShape(size = ITEM_CORNER_RADIUS.dp)
    Card(shape = shape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onGapClicked.invoke(item.id) })
                .wrapContentHeight()
                .clip(shape)
                .background(color = MaterialTheme.colors.onPrimary)
                .padding(all = PADDING.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.title,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = item.date,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body2
                )
            }
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Red)
            )
            OutlinedButton(
                onClick = { onGapRemoveClicked(item.id) },
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
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GapCalcTheme {
        GapListScreen()
    }
}
