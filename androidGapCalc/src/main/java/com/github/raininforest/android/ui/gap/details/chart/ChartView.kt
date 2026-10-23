package com.github.raininforest.android.ui.gap.details.chart

import android.graphics.Paint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.raininforest.android.ui.gap.details.PADDING
import androidx.compose.ui.platform.LocalContext
import com.github.raininforest.android.ui.gap.common.parameterLines
import com.github.raininforest.data.CHART_BACKGROUND
import com.github.raininforest.data.entity.ParameterValue
import com.github.raininforest.data.CHART_GRID
import com.github.raininforest.data.CHART_LABEL
import com.github.raininforest.data.entity.Chart
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.ui.details.chart.ChartBounds
import com.github.raininforest.ui.details.chart.bounds
import com.github.raininforest.ui.details.chart.gridTicks
import com.github.raininforest.ui.details.data.GapDetailsState
import kotlin.math.min

private const val MAX_ZOOM = 12f
private const val LEFT_MARGIN_DP = 28
private const val BOTTOM_MARGIN_DP = 20
private const val MARGIN_DP = 4
private const val LABEL_SIZE_DP = 10f

@Composable
fun ChartView(
    paddingValues: PaddingValues,
    data: GapDetailsState.GapDetailsData
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(horizontal = PADDING.dp)
    ) {
        if (maxWidth > maxHeight) {
            // landscape: parameters on the left, the chart gets the full height on the right
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    Parameters(data.textData.inputGapParameters, TextAlign.Start)
                    Spacer(modifier = Modifier.height(PADDING.dp))
                    Parameters(data.textData.outputGapParameters, TextAlign.Start)
                }
                Spacer(modifier = Modifier.width(PADDING.dp))
                TrajectoryChart(
                    chartData = data.chartData,
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .padding(bottom = PADDING.dp)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Parameters(data.textData.inputGapParameters, TextAlign.Start, Modifier.weight(1f))
                    Parameters(data.textData.outputGapParameters, TextAlign.End, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(PADDING.dp))
                TrajectoryChart(
                    chartData = data.chartData,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = PADDING.dp)
                )
            }
        }
    }
}

@Composable
private fun Parameters(items: List<ParameterValue>, textAlign: TextAlign, modifier: Modifier = Modifier) {
    Text(
        text = LocalContext.current.parameterLines(items),
        style = MaterialTheme.typography.caption,
        textAlign = textAlign,
        modifier = modifier
    )
}

/** Grid + filled ramps + flight paths; pinch to zoom, drag to pan, double tap to reset. */
@Composable
private fun TrajectoryChart(chartData: ChartData, modifier: Modifier = Modifier) {
    val bounds = remember(chartData) { chartData.bounds() }
    var zoom by remember(chartData) { mutableStateOf(1f) }
    var pan by remember(chartData) { mutableStateOf(Offset.Zero) }

    // both axes share one scale, so the panel shrinks to the chart's proportions instead of stretching it
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        val plotWidth = maxWidth - (LEFT_MARGIN_DP + MARGIN_DP).dp
        val plotHeight = maxHeight - (BOTTOM_MARGIN_DP + MARGIN_DP).dp
        val scale = min(plotWidth.value / bounds.width.toFloat(), plotHeight.value / bounds.height.toFloat())
        val panelWidth = (bounds.width.toFloat() * scale).dp + (LEFT_MARGIN_DP + MARGIN_DP).dp
        val panelHeight = (bounds.height.toFloat() * scale).dp + (BOTTOM_MARGIN_DP + MARGIN_DP).dp

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .size(width = panelWidth, height = panelHeight)
                .clip(RoundedCornerShape(PADDING.dp))
                .pointerInput(chartData) {
                    detectTransformGestures { centroid, panChange, zoomChange, _ ->
                        val newZoom = (zoom * zoomChange).coerceIn(1f, MAX_ZOOM)
                        // keep the point under the fingers in place while zooming
                        pan = (pan - centroid) * (newZoom / zoom) + centroid + panChange
                        zoom = newZoom
                    }
                }
                .pointerInput(chartData) {
                    detectTapGestures(onDoubleTap = {
                        zoom = 1f
                        pan = Offset.Zero
                    })
                }
        ) {
            drawRect(color = parseColor(CHART_BACKGROUND))
            val view = ChartViewport(size.width, size.height, bounds, zoom, pan, density)
            clipRect(left = view.plotLeft, top = 0f, right = size.width, bottom = view.plotBottom) {
                view.drawGrid(this)
                chartData.charts.forEach { view.drawChart(this, it) }
            }
            view.drawLabels(this)
        }
    }
}

private class ChartViewport(
    private val width: Float,
    private val height: Float,
    private val bounds: ChartBounds,
    private val zoom: Float,
    pan: Offset,
    private val density: Float
) {
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor(CHART_LABEL)
        textSize = LABEL_SIZE_DP * density
    }
    private val margin = MARGIN_DP * density
    val plotLeft = LEFT_MARGIN_DP * density
    val plotBottom = height - BOTTOM_MARGIN_DP * density
    private val plotWidth = width - plotLeft - margin
    private val plotHeight = plotBottom - margin

    // one scale for both axes: the jump keeps its real proportions
    private val sx = min((plotWidth / bounds.width).toFloat(), (plotHeight / bounds.height).toFloat())
    private val sy = sx
    private val originX = plotLeft + pan.x
    private val originY = plotBottom - (plotHeight - bounds.height.toFloat() * sy) / 2 + pan.y

    fun x(value: Double) = originX + ((value - bounds.minX) * sx * zoom).toFloat()
    fun y(value: Double) = originY - ((value - bounds.minY) * sy * zoom).toFloat()

    private val visibleMinX get() = bounds.minX + (plotLeft - originX) / (sx * zoom)
    private val visibleMaxX get() = bounds.minX + (width - originX) / (sx * zoom)
    private val visibleMinY get() = bounds.minY + (originY - plotBottom) / (sy * zoom)
    private val visibleMaxY get() = bounds.minY + (originY - margin) / (sy * zoom)

    fun drawGrid(scope: DrawScope) = with(scope) {
        val gridColor = parseColor(CHART_GRID)
        gridTicks(visibleMinX, visibleMaxX).forEach {
            drawLine(gridColor, Offset(x(it), 0f), Offset(x(it), plotBottom), strokeWidth = 1f)
        }
        gridTicks(visibleMinY, visibleMaxY).forEach {
            drawLine(gridColor, Offset(plotLeft, y(it)), Offset(width, y(it)), strokeWidth = 1f)
        }
    }

    fun drawChart(scope: DrawScope, chart: Chart) = with(scope) {
        if (chart.function.size < 2) return@with
        val line = Path().apply {
            chart.function.forEachIndexed { i, p ->
                if (i == 0) moveTo(x(p.x), y(p.y)) else lineTo(x(p.x), y(p.y))
            }
        }
        chart.chartConfig.fillHexColor?.let { fill ->
            val area = Path().apply {
                addPath(line)
                lineTo(x(chart.function.last().x), y(0.0))
                lineTo(x(chart.function.first().x), y(0.0))
                close()
            }
            drawPath(area, parseColor(fill))
        }
        drawPath(
            path = line,
            color = parseColor(chart.chartConfig.hexColor),
            style = Stroke(
                width = chart.chartConfig.thicknessPx * density,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    fun drawLabels(scope: DrawScope) = with(scope) {
        drawIntoCanvas { canvas ->
            labelPaint.textAlign = Paint.Align.CENTER
            gridTicks(visibleMinX, visibleMaxX).forEach {
                if (x(it) in plotLeft..(width - 8 * density)) {
                    canvas.nativeCanvas.drawText(label(it), x(it), height - 5 * density, labelPaint)
                }
            }
            labelPaint.textAlign = Paint.Align.RIGHT
            gridTicks(visibleMinY, visibleMaxY).forEach {
                if (y(it) <= plotBottom) {
                    canvas.nativeCanvas.drawText(
                        label(it),
                        plotLeft - 3 * density,
                        y(it) + 3 * density,
                        labelPaint
                    )
                }
            }
        }
    }

    private fun label(value: Double) = value.toLong().toString()
}

private fun parseColor(hex: String) = Color(android.graphics.Color.parseColor(hex))
