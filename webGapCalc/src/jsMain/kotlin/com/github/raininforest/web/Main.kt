package com.github.raininforest.web

import com.github.raininforest.di.Dependencies
import com.github.raininforest.di.WebPlatformDependencies
import com.github.raininforest.ui.details.GapDetailsViewModel
import com.github.raininforest.ui.details.data.GapDetailsState
import com.github.raininforest.ui.edit.GapEditField
import com.github.raininforest.ui.edit.GapEditViewModel
import com.github.raininforest.ui.list.GapListViewModel
import com.github.raininforest.ui.list.data.GapListState
import androidx.lifecycle.viewModelScope
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

private val deps = Dependencies(WebPlatformDependencies())

fun main() {
    val root = document.getElementById("app")!!
    document.title = Strings.title
    val app = WebApp(root)
    window.onhashchange = { app.route() }
    app.route()
}

/** Hash-routed single page app: `#/` list, `#/gap/<id>` details, `#/gap/<id>/edit` edit. */
private class WebApp(private val root: Element) {
    private var screenScope: CoroutineScope? = null
    private var onLeave: () -> Unit = {}

    fun route() {
        onLeave()
        screenScope?.cancel()
        val scope = MainScope().also { screenScope = it }
        root.clear()
        val parts = window.location.hash.removePrefix("#").split("/").filter { it.isNotEmpty() }
        val gapId = parts.getOrNull(1)?.toLongOrNull()
        when {
            parts.size == 3 && parts[0] == "gap" && parts[2] == "edit" && gapId != null -> editScreen(gapId, scope)
            parts.size == 2 && parts[0] == "gap" && gapId != null -> detailsScreen(gapId, scope)
            else -> listScreen(scope)
        }
    }

    private fun go(hash: String) {
        window.location.hash = hash
    }

    private fun listScreen(scope: CoroutineScope) {
        val viewModel = GapListViewModel(deps.gapListRepository)
        onLeave = { viewModel.viewModelScope.cancel() }
        val content = root.add("div", "cards")
        root.add("div", "footer") { button("primary", Strings.add) { viewModel.addClicked() } }
        scope.launch {
            viewModel.gapList.collect { state ->
                content.clear()
                when (state) {
                    is GapListState.GapListData -> state.gapList.forEach { item ->
                        content.add("div", "card clickable") {
                            (this as HTMLElement).onclick = { go("#/gap/${item.id}") }
                            add("div", "grow") {
                                add("div", text = item.title.ifBlank { Strings.untitled })
                                add("div", "sub", item.date)
                            }
                            button("remove", "✕") { viewModel.removeGapClicked(item.id) }.also {
                                it.onclick = { event ->
                                    event.stopPropagation()
                                    viewModel.removeGapClicked(item.id)
                                }
                            }
                        }
                    }
                    else -> content.add("div", "empty", Strings.empty)
                }
            }
        }
    }

    private fun detailsScreen(gapId: Long, scope: CoroutineScope) {
        val viewModel = GapDetailsViewModel(deps.gapDetailsRepository)
        onLeave = { viewModel.viewModelScope.cancel() }
        val bar = root.add("div", "bar")
        val body = root.add("div", "cards")
        viewModel.fetchGapDetails(gapId)
        scope.launch {
            viewModel.gapDetails.collect { state ->
                bar.clear()
                body.clear()
                bar.button("round", "←") { go("#/") }.title = Strings.back
                val data = state as? GapDetailsState.GapDetailsData
                if (state is GapDetailsState.Loading) return@collect
                bar.add("div", "title", data?.gapTitle?.ifBlank { Strings.untitled } ?: Strings.empty)
                if (data == null) return@collect
                bar.button("round", "⤴") { share(Strings.shareText(data.textData)) }.title = Strings.share

                body.add("div", "params") {
                    add("div", text = Strings.lines(data.textData.inputGapParameters))
                    add("div", "right", Strings.lines(data.textData.outputGapParameters))
                }
                if (data.warnings.isNotEmpty()) {
                    body.add("div", "warnings", data.warnings.joinToString("\n") { Strings.warning(it) })
                }
                val canvas = body.add("canvas") as HTMLCanvasElement
                val chart = TrajectoryChart(data.chartData, canvas)
                chart.layout(
                    availableWidth = body.clientWidth.toDouble().coerceAtLeast(280.0),
                    availableHeight = window.innerHeight * 0.55
                )
                window.onresize = {
                    chart.layout(body.clientWidth.toDouble().coerceAtLeast(280.0), window.innerHeight * 0.55)
                }
            }
        }
        root.add("div", "footer") { button("primary wide", Strings.edit) { go("#/gap/$gapId/edit") } }
    }

    private fun editScreen(gapId: Long, scope: CoroutineScope) {
        val viewModel = GapEditViewModel(deps.gapEditRepository)
        onLeave = { viewModel.viewModelScope.cancel() }
        val bar = root.add("div", "bar")
        bar.button("round", "←") { go("#/gap/$gapId") }.title = Strings.back
        val titleInput = bar.input("title") { viewModel.gapTitleChanged(it) }
        titleInput.placeholder = Strings.untitled

        val fields = root.add("div", "cards")
        GapEditField.entries.forEach { field ->
            val input = fields.add("div", "field") {
                field.parameter.iconFile()?.let { file ->
                    add("img", "hint") { setAttribute("src", "icons/$file.svg") }
                }
                add("label", text = Strings.inputLabel(field.parameter))
            }.let { row ->
                row.input { viewModel.onFieldChanged(field, it) }.also { it.inputMode = "decimal" }
            }
            scope.launch { viewModel.fieldState(field).collect { if (input.value != it) input.value = it } }
        }
        scope.launch { viewModel.gapTitleState.collect { if (titleInput.value != it) titleInput.value = it } }
        viewModel.fetchGapParameters(gapId)
        root.add("div", "footer") {
            button("primary wide", Strings.apply) { viewModel.onApplyClicked { go("#/gap/$gapId") } }
        }
    }

    private fun share(text: String) {
        deps.shareService.share(text)
    }
}

private var HTMLInputElement.inputMode: String
    get() = getAttribute("inputmode") ?: ""
    set(value) = setAttribute("inputmode", value)
