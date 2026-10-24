package com.github.raininforest.android.ui.gap.list

import android.annotation.SuppressLint
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.TextButton
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.github.raininforest.android.theme.red
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.raininforest.android.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.raininforest.android.GapCalculatorApplication.Companion.deps
import com.github.raininforest.android.theme.GapCalcTheme
import com.github.raininforest.android.theme.green
import com.github.raininforest.android.theme.whiteGray
import com.github.raininforest.ui.list.GapListViewModel
import com.github.raininforest.ui.list.data.GapListItem
import com.github.raininforest.ui.list.data.GapListState

private const val PADDING = 16
private const val BUTTON_HEIGHT = 48
private const val ITEM_SPACING = 8
private const val BUTTON_STROKE_WIDTH = 1
private const val LIST_BOTTOM_PADDING = 88

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GapListScreen(
    onGapClicked: (gapId: Long) -> Unit = {},
    gapListViewModel: GapListViewModel = viewModel(
        factory = GapListVMFactory(gapListRepository = deps.gapListRepository)
    )
) {
    // the view-model survives navigation: reload when the screen (re)enters composition, e.g. after editing
    LaunchedEffect(Unit) { gapListViewModel.fetchList() }

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
        floatingActionButtonPosition = FabPosition.End
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

        is GapListState.GapListEmpty -> EmptyList()
    }
}

@Composable
private fun EmptyList() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(PADDING.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.empty_list_title),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.empty_list_hint),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun FloatingButton(onAddClicked: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onAddClicked,
        backgroundColor = green,
        contentColor = whiteGray,
        text = { Text(text = stringResource(R.string.add), style = MaterialTheme.typography.body1) }
    )
}

@Composable
fun Data(
    currentState: GapListState.GapListData,
    onGapClicked: (gapId: Long) -> Unit,
    onGapRemoveClicked: (gapId: Long) -> Unit
) {
    if (currentState.gapList.isEmpty()) {
        EmptyList()
        return
    }
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
    var pendingRemoval by remember { mutableStateOf<GapListItem?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(
            start = PADDING.dp,
            end = PADDING.dp,
            top = PADDING.dp,
            bottom = LIST_BOTTOM_PADDING.dp
        ),
        verticalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp),
    ) {
        item(key = "title") {
            Text(
                text = stringResource(R.string.title_jumps),
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(vertical = 8.dp).semantics { heading() }
            )
        }
        items(
            items = gapList,
            key = { gapListItem -> gapListItem.id }
        ) { gapListItem ->
            GapListItemView(
                item = gapListItem,
                onGapClicked = onGapClicked,
                onGapRemoveClicked = { pendingRemoval = gapListItem }
            )
        }
    }

    pendingRemoval?.let { item ->
        val title = item.title.ifBlank { stringResource(R.string.untitled) }
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_message, title)) },
            confirmButton = {
                TextButton(onClick = {
                    pendingRemoval = null
                    onGapRemoveClicked(item.id)
                }) { Text(stringResource(R.string.delete), color = red) }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

/** "2026-10-20 18:05:11.000" -> "Oct 20, 2026, 6:05 PM" in the user's locale; unknown formats stay as they are. */
private fun formatDate(raw: String): String = try {
    LocalDateTime.parse(raw.replace(' ', 'T'))
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
} catch (_: DateTimeParseException) {
    raw
}

@Composable
fun GapListItemView(
    item: GapListItem,
    onGapClicked: (gapId: Long) -> Unit,
    onGapRemoveClicked: (gapId: Long) -> Unit
) {
    val shape = RoundedCornerShape(percent = 50) // concentric with the round remove button
    val title = item.title.ifBlank { stringResource(R.string.untitled) }
    Card(shape = shape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onGapClicked.invoke(item.id) })
                .wrapContentHeight()
                .clip(shape)
                .background(color = MaterialTheme.colors.onPrimary)
                .padding(start = PADDING.dp + 8.dp, top = PADDING.dp, end = PADDING.dp, bottom = PADDING.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDate(item.date),
                    color = MaterialTheme.colors.primary.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.body2
                )
            }
            OutlinedButton(
                onClick = { onGapRemoveClicked(item.id) },
                shape = CircleShape,
                modifier = Modifier.size(BUTTON_HEIGHT.dp),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(BUTTON_STROKE_WIDTH.dp, MaterialTheme.colors.onPrimary),
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.onPrimary)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.delete) + ": " + title
                )
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
