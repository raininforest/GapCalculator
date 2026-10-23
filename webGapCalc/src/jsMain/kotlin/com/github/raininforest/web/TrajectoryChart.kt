package com.github.raininforest.web

import com.github.raininforest.data.CHART_BACKGROUND
import com.github.raininforest.data.CHART_GRID
import com.github.raininforest.data.CHART_LABEL
import com.github.raininforest.data.entity.Chart
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.ui.details.chart.ChartBounds
import com.github.raininforest.ui.details.chart.bounds
import com.github.raininforest.ui.details.chart.gridTicks
import kotlinx.browser.window
import org.w3c.dom.CanvasLineCap
import org.w3c.dom.CanvasLineJoin
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import kotlin.math.min
import kotlin.math.pow

private const val LEFT_MARGIN = 28.0
private const val BOTTOM_MARGIN = 20.0
private const val MARGIN = 4.0
private const val MAX_ZOOM = 12.0

/**
 * Same picture as the mobile charts: grid, filled ramps, flight paths, one scale for both axes.
 * Wheel zooms, dragging pans, double click resets.
 */
class TrajectoryChart(private val chartData: ChartData, private val canvas: HTMLCanvasElement) {
    private val bounds = chartData.bounds()
    private var zoom = 1.0
    private var panX = 0.0
    private var panY = 0.0
    private var width = 0.0
    private var height = 0.0

    init {
        canvas.onwheel = { event -> onWheel(event) }
        canvas.ondblclick = { reset() }
        var dragging = false
        var lastX = 0.0
        var lastY = 0.0
        canvas.onmousedown = { event: MouseEvent ->
            dragging = true
            lastX = event.clientX.toDouble()
            lastY = event.clientY.toDouble()
        }
        window.onmouseup = { dragging = false }
        canvas.onmousemove = { event: MouseEvent ->
            if (dragging) {
                panX += event.clientX - lastX
                panY += event.clientY - lastY
                lastX = event.clientX.toDouble()
                lastY = event.clientY.toDouble()
                draw()
            }
        }
    }

    /** Sizes the canvas to the chart's proportions inside [availableWidth] x [availableHeight] css pixels. */
    fun layout(availableWidth: Double, availableHeight: Double) {
        val scale = min(
            (availableWidth - LEFT_MARGIN - MARGIN) / bounds.width,
            (availableHeight - BOTTOM_MARGIN - MARGIN) / bounds.height
        )
        width = bounds.width * scale + LEFT_MARGIN + MARGIN
        height = bounds.height * scale + BOTTOM_MARGIN + MARGIN
        val ratio = window.devicePixelRatio
        canvas.style.width = "${width}px"
        canvas.style.height = "${height}px"
        canvas.width = (width * ratio).toInt()
        canvas.height = (height * ratio).toInt()
        draw()
    }

    private fun reset() {
        zoom = 1.0
        panX = 0.0
        panY = 0.0
        draw()
    }

    private fun onWheel(event: WheelEvent) {
        event.preventDefault()
        val rect = canvas.getBoundingClientRect()
        val pointX = event.clientX - rect.left
        val pointY = event.clientY - rect.top
        val newZoom = (zoom * 1.1.pow(-event.deltaY / 100.0)).coerceIn(1.0, MAX_ZOOM)
        // keep the point under the cursor in place
        panX = (panX - pointX) * (newZoom / zoom) + pointX
        panY = (panY - pointY) * (newZoom / zoom) + pointY
        zoom = newZoom
        draw()
    }

    private fun draw() {
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val ratio = window.devicePixelRatio
        context.setTransform(ratio, 0.0, 0.0, ratio, 0.0, 0.0)
        context.fillStyle = css(CHART_BACKGROUND)
        context.fillRect(0.0, 0.0, width, height)

        val view = Viewport(bounds, width, height, zoom, panX, panY)
        context.save()
        context.beginPath()
        context.rect(LEFT_MARGIN, 0.0, width - LEFT_MARGIN, height - BOTTOM_MARGIN)
        context.clip()
        view.drawGrid(context)
        chartData.charts.forEach { view.drawChart(context, it) }
        context.restore()
        view.drawLabels(context)
    }
}

private class Viewport(
    private val bounds: ChartBounds,
    private val width: Double,
    private val height: Double,
    private val zoom: Double,
    pan: Double,
    panY: Double
) {
    private val plotWidth = width - LEFT_MARGIN - MARGIN
    private val plotHeight = height - BOTTOM_MARGIN - MARGIN
    private val scale = min(plotWidth / bounds.width, plotHeight / bounds.height)
    private val originX = LEFT_MARGIN + pan
    private val originY = height - BOTTOM_MARGIN - (plotHeight - bounds.height * scale) / 2 + panY

    private fun x(value: Double) = originX + (value - bounds.minX) * scale * zoom
    private fun y(value: Double) = originY - (value - bounds.minY) * scale * zoom

    private val visibleMinX get() = bounds.minX + (LEFT_MARGIN - originX) / (scale * zoom)
    private val visibleMaxX get() = bounds.minX + (width - originX) / (scale * zoom)
    private val visibleMinY get() = bounds.minY + (originY - (height - BOTTOM_MARGIN)) / (scale * zoom)
    private val visibleMaxY get() = bounds.minY + (originY - MARGIN) / (scale * zoom)

    private val xTicks get() = gridTicks(visibleMinX, visibleMaxX, 10)
    private val yTicks get() = gridTicks(visibleMinY, visibleMaxY, 10)

    fun drawGrid(context: CanvasRenderingContext2D) {
        context.strokeStyle = css(CHART_GRID)
        context.lineWidth = 1.0
        xTicks.forEach {
            context.beginPath()
            context.moveTo(x(it), 0.0)
            context.lineTo(x(it), height - BOTTOM_MARGIN)
            context.stroke()
        }
        yTicks.forEach {
            context.beginPath()
            context.moveTo(LEFT_MARGIN, y(it))
            context.lineTo(width, y(it))
            context.stroke()
        }
    }

    fun drawChart(context: CanvasRenderingContext2D, chart: Chart) {
        val points = chart.function
        if (points.size < 2) return
        context.beginPath()
        points.forEachIndexed { i, p -> if (i == 0) context.moveTo(x(p.x), y(p.y)) else context.lineTo(x(p.x), y(p.y)) }
        chart.chartConfig.fillHexColor?.let { fill ->
            context.save()
            context.lineTo(x(points.last().x), y(0.0))
            context.lineTo(x(points.first().x), y(0.0))
            context.closePath()
            context.fillStyle = css(fill)
            context.fill()
            context.restore()
            // the fill path was closed; rebuild the open line for stroking
            context.beginPath()
            points.forEachIndexed { i, p ->
                if (i == 0) {
                    context.moveTo(
                        x(p.x),
                        y(p.y)
                    )
                } else {
                    context.lineTo(x(p.x), y(p.y))
                }
            }
        }
        context.strokeStyle = css(chart.chartConfig.hexColor)
        context.lineWidth = chart.chartConfig.thicknessPx.toDouble()
        context.lineCap = "round".unsafeCast<CanvasLineCap>()
        context.lineJoin = "round".unsafeCast<CanvasLineJoin>()
        context.stroke()
    }

    fun drawLabels(context: CanvasRenderingContext2D) {
        context.fillStyle = css(CHART_LABEL)
        context.font = "10px system-ui, sans-serif"
        context.textAlign = "center".unsafeCast<CanvasTextAlign>()
        context.textBaseline = "middle".unsafeCast<CanvasTextBaseline>()
        xTicks.forEach {
            if (x(it) in LEFT_MARGIN..(width - 8.0)) context.fillText(label(it), x(it), height - BOTTOM_MARGIN / 2)
        }
        context.textAlign = "right".unsafeCast<CanvasTextAlign>()
        yTicks.forEach {
            if (y(it) <= height - BOTTOM_MARGIN) context.fillText(label(it), LEFT_MARGIN - 3.0, y(it))
        }
    }

    private fun label(value: Double) = value.toLong().toString()
}

/** "#RRGGBB" / "#AARRGGBB" (android style) to a css color. */
private fun css(hex: String): String {
    val digits = hex.removePrefix("#")
    if (digits.length != 8) return "#$digits"
    val alpha = digits.substring(0, 2).toInt(16) / 255.0
    val red = digits.substring(2, 4).toInt(16)
    val green = digits.substring(4, 6).toInt(16)
    val blue = digits.substring(6, 8).toInt(16)
    return "rgba($red, $green, $blue, $alpha)"
}
