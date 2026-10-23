//
//  TrajectoryChartView.swift
//  iosGapCalc
//
//  Grid + filled ramps + flight paths, drawn the same way as the Android chart.
//  Pinch to zoom, drag to pan, double tap to reset.
//

import SwiftUI
import sharedGapCalc

private let MAX_ZOOM: CGFloat = 12
private let LEFT_MARGIN = 28.0
private let BOTTOM_MARGIN = 20.0
private let MARGIN = 4.0

struct TrajectoryChartView: View {
    let chartData: ChartData

    @State private var zoom: CGFloat = 1
    @State private var baseZoom: CGFloat = 1
    @State private var pan: CGSize = .zero
    @State private var basePan: CGSize = .zero

    var body: some View {
        // both axes share one scale, so the panel shrinks to the chart's proportions instead of stretching it
        GeometryReader { geometry in
            chart(size: panelSize(in: geometry.size))
                .frame(maxWidth: .infinity, alignment: .top)
        }
    }

    private func panelSize(in available: CGSize) -> CGSize {
        let bounds = chartData.bounds()
        let scale = min(
            (available.width - LEFT_MARGIN - MARGIN) / bounds.width,
            (available.height - BOTTOM_MARGIN - MARGIN) / bounds.height
        )
        return CGSize(
            width: bounds.width * scale + LEFT_MARGIN + MARGIN,
            height: bounds.height * scale + BOTTOM_MARGIN + MARGIN
        )
    }

    private func chart(size panel: CGSize) -> some View {
        Canvas { context, size in
            let viewport = ChartViewport(bounds: chartData.bounds(), size: size, zoom: zoom, pan: pan)
            context.fill(Path(CGRect(origin: .zero, size: size)), with: .color(Color(hex: HexColorsKt.CHART_BACKGROUND)))

            var plot = context
            plot.clip(to: Path(viewport.plotRect))
            viewport.drawGrid(in: &plot)
            for chart in chartData.charts {
                viewport.draw(chart, in: &plot)
            }
            viewport.drawLabels(in: &context)
        }
        .frame(width: panel.width, height: panel.height)
        .clipShape(RoundedRectangle(cornerRadius: PADDING))
        .gesture(
            SimultaneousGesture(
                MagnificationGesture()
                    .onChanged { zoom = min(max(baseZoom * $0, 1), MAX_ZOOM) }
                    .onEnded { _ in baseZoom = zoom },
                DragGesture()
                    .onChanged { pan = CGSize(width: basePan.width + $0.translation.width, height: basePan.height + $0.translation.height) }
                    .onEnded { _ in basePan = pan }
            )
        )
        .onTapGesture(count: 2) {
            zoom = 1; baseZoom = 1
            pan = .zero; basePan = .zero
        }
    }
}

private struct ChartViewport {
    let bounds: ChartBounds
    let size: CGSize
    let zoom: CGFloat
    let pan: CGSize

    private var plotWidth: Double { size.width - LEFT_MARGIN - MARGIN }
    private var plotHeight: Double { size.height - BOTTOM_MARGIN - MARGIN }
    // one scale for both axes: the jump keeps its real proportions
    private var scale: Double { min(plotWidth / bounds.width, plotHeight / bounds.height) }
    private var sx: Double { scale }
    private var sy: Double { scale }
    private var center: CGPoint { CGPoint(x: LEFT_MARGIN + plotWidth / 2, y: MARGIN + plotHeight / 2) }
    private var bottom: Double { size.height - BOTTOM_MARGIN - (plotHeight - bounds.height * sy) / 2 }

    var plotRect: CGRect { CGRect(x: LEFT_MARGIN, y: 0, width: size.width - LEFT_MARGIN, height: size.height - BOTTOM_MARGIN) }

    func point(_ p: Point) -> CGPoint { point(x: p.x, y: p.y) }

    func point(x: Double, y: Double) -> CGPoint {
        let base = CGPoint(x: LEFT_MARGIN + (x - bounds.minX) * sx, y: bottom - (y - bounds.minY) * sy)
        return CGPoint(
            x: center.x + pan.width + (base.x - center.x) * zoom,
            y: center.y + pan.height + (base.y - center.y) * zoom
        )
    }

    private func dataX(screenX: Double) -> Double {
        let base = center.x + (screenX - center.x - pan.width) / zoom
        return bounds.minX + (base - LEFT_MARGIN) / sx
    }

    private func dataY(screenY: Double) -> Double {
        let base = center.y + (screenY - center.y - pan.height) / zoom
        return bounds.minY + (bottom - base) / sy
    }

    private var xTicks: [Double] { ticks(from: dataX(screenX: LEFT_MARGIN), to: dataX(screenX: size.width)) }
    private var yTicks: [Double] { ticks(from: dataY(screenY: size.height - BOTTOM_MARGIN), to: dataY(screenY: 0)) }

    private func ticks(from min: Double, to max: Double) -> [Double] {
        ChartBoundsKt.gridTicks(min: min, max: max, targetCount: 10).map { $0.doubleValue }
    }

    func drawGrid(in context: inout GraphicsContext) {
        let color = Color(hex: HexColorsKt.CHART_GRID)
        for tick in xTicks {
            var line = Path()
            line.move(to: CGPoint(x: point(x: tick, y: 0).x, y: 0))
            line.addLine(to: CGPoint(x: point(x: tick, y: 0).x, y: size.height))
            context.stroke(line, with: .color(color), lineWidth: 1)
        }
        for tick in yTicks {
            var line = Path()
            line.move(to: CGPoint(x: 0, y: point(x: 0, y: tick).y))
            line.addLine(to: CGPoint(x: size.width, y: point(x: 0, y: tick).y))
            context.stroke(line, with: .color(color), lineWidth: 1)
        }
    }

    func draw(_ chart: Chart, in context: inout GraphicsContext) {
        guard let first = chart.function.first, chart.function.count > 1 else { return }
        var line = Path()
        line.move(to: point(first))
        chart.function.dropFirst().forEach { line.addLine(to: point($0)) }

        if let fill = chart.chartConfig.fillHexColor, let last = chart.function.last {
            var area = line
            area.addLine(to: point(x: last.x, y: 0))
            area.addLine(to: point(x: first.x, y: 0))
            area.closeSubpath()
            context.fill(area, with: .color(Color(hex: fill)))
        }
        context.stroke(
            line,
            with: .color(Color(hex: chart.chartConfig.hexColor)),
            style: StrokeStyle(lineWidth: CGFloat(chart.chartConfig.thicknessPx), lineCap: .round, lineJoin: .round)
        )
    }

    func drawLabels(in context: inout GraphicsContext) {
        let color = Color(hex: HexColorsKt.CHART_LABEL)
        func label(_ value: Double) -> Text {
            Text("\(Int(value))").font(.system(size: 10)).foregroundColor(color)
        }
        for tick in xTicks {
            let x = point(x: tick, y: 0).x
            if x >= LEFT_MARGIN && x <= size.width - 8 {
                context.draw(label(tick), at: CGPoint(x: x, y: size.height - BOTTOM_MARGIN / 2), anchor: .center)
            }
        }
        for tick in yTicks {
            let y = point(x: 0, y: tick).y
            if y <= size.height - BOTTOM_MARGIN {
                context.draw(label(tick), at: CGPoint(x: LEFT_MARGIN - 3, y: y), anchor: .trailing)
            }
        }
    }
}
