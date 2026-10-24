//
//  GapDetailsScreen.swift
//  iosGapCalc
//

import SwiftUI
import sharedGapCalc

struct GapDetailsScreen: View {
    let gapId: Int64
    @StateObject private var observable = GapDetailsObservable()
    @Environment(\.presentationMode) private var presentationMode

    var body: some View {
        ZStack {
            Colors.lightGray.ignoresSafeArea()
            if let data = observable.state as? GapDetailsState.GapDetailsData {
                GapDetailsContent(data: data, gapId: gapId)
            } else if !(observable.state is GapDetailsState.Loading) {
                NoData()
            }
        }
        .navigationTitle(title)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .toolbar {
            BackToolbarButton { presentationMode.wrappedValue.dismiss() }
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: observable.share) {
                    Image(systemName: "square.and.arrow.up").font(TOOLBAR_ICON_FONT)
                }
            }
        }
        .onAppear { observable.load(gapId: gapId) }
    }

    private var title: String {
        if observable.state is GapDetailsState.Loading { return "" }
        guard let data = observable.state as? GapDetailsState.GapDetailsData else { return L("no_data") }
        return data.gapTitle.isEmpty ? L("untitled") : data.gapTitle
    }
}

private let CARD_RADIUS = ITEM_CORNER_RADIUS / 2
private let CHART_HEIGHT = 320.0

private struct GapDetailsContent: View {
    let data: GapDetailsState.GapDetailsData
    let gapId: Int64

    var body: some View {
        GeometryReader { geometry in
            if geometry.size.width > geometry.size.height {
                // landscape: numbers on the left, the chart gets the full height on the right
                HStack(alignment: .top, spacing: PADDING) {
                    ScrollView {
                        VStack(alignment: .leading, spacing: PADDING) {
                            warnings
                            results
                            inputParameters
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .frame(width: geometry.size.width * 0.4)
                    VStack(spacing: PADDING) {
                        TrajectoryChartView(chartData: data.chartData)
                        ChartLegend()
                        editButton
                    }
                }
                .padding(.horizontal, PADDING)
                .padding(.top, PADDING)
            } else {
                VStack(spacing: 0) {
                    ScrollView {
                        VStack(alignment: .leading, spacing: PADDING) {
                            warnings
                            let chart = TrajectoryChartView(chartData: data.chartData)
                            chart.frame(
                                height: chart.idealHeight(forWidth: geometry.size.width - PADDING * 2, maxHeight: CHART_HEIGHT)
                            )
                            ChartLegend()
                            results
                            inputParameters
                        }
                        .padding(.horizontal, PADDING)
                        .padding(.vertical, PADDING)
                    }
                    editButton
                }
            }
        }
    }

    /// Every warning on its own row; the colour says how bad it is, the icon and text keep it readable without colour.
    @ViewBuilder
    private var warnings: some View {
        if !data.warnings.isEmpty {
            VStack(spacing: ITEM_SPACING) {
                ForEach(data.warnings, id: \.self) { warning in
                    HStack(spacing: 12) {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .foregroundColor(warning.color)
                            .accessibilityLabel(L("warning"))
                        Text(warning.text)
                            .font(.body)
                            .foregroundColor(Colors.darkGray)
                        Spacer(minLength: 0)
                    }
                    .padding(PADDING)
                    .background(warning.color.opacity(0.2))
                    .clipShape(RoundedRectangle(cornerRadius: CARD_RADIUS))
                    .overlay(RoundedRectangle(cornerRadius: CARD_RADIUS).stroke(warning.color, lineWidth: 1))
                    .accessibilityElement(children: .combine)
                }
            }
        }
    }

    /// The computed values as big cards, two per row.
    private var results: some View {
        let items = data.textData.outputGapParameters
        let rows = stride(from: 0, to: items.count, by: 2).map { Array(items[$0..<min($0 + 2, items.count)]) }
        return VStack(alignment: .leading, spacing: ITEM_SPACING) {
            SectionTitle(L("section_results"))
            ForEach(rows.indices, id: \.self) { row in
                HStack(spacing: ITEM_SPACING) {
                    ForEach(rows[row].indices, id: \.self) { column in
                        let item = rows[row][column]
                        VStack(alignment: .leading, spacing: 2) {
                            Text(item.parameter.label)
                                .font(.caption)
                                .foregroundColor(Colors.darkGray.opacity(0.75))
                            Text(item.valueWithUnit)
                                .font(.title3.weight(.semibold))
                                .foregroundColor(Colors.darkGray)
                                .lineLimit(1)
                                .minimumScaleFactor(0.7)
                        }
                        .padding(12)
                        .frame(maxWidth: .infinity, minHeight: 64, alignment: .leading)
                        .background(Colors.white)
                        .clipShape(RoundedRectangle(cornerRadius: CARD_RADIUS))
                        .accessibilityElement(children: .combine)
                    }
                    if rows[row].count == 1 { Spacer().frame(maxWidth: .infinity) }
                }
            }
        }
    }

    /// The entered values as a label / value table.
    private var inputParameters: some View {
        VStack(alignment: .leading, spacing: ITEM_SPACING) {
            SectionTitle(L("section_parameters"))
            VStack(spacing: 8) {
                ForEach(data.textData.inputGapParameters.indices, id: \.self) { index in
                    let item = data.textData.inputGapParameters[index]
                    HStack {
                        Text(item.parameter.label)
                            .foregroundColor(Colors.darkGray.opacity(0.75))
                        Spacer()
                        Text(item.valueWithUnit)
                            .fontWeight(.medium)
                            .foregroundColor(Colors.darkGray)
                    }
                    .font(.subheadline)
                    .accessibilityElement(children: .combine)
                }
            }
            .padding(.horizontal, PADDING)
            .padding(.vertical, 12)
            .background(Colors.white)
            .clipShape(RoundedRectangle(cornerRadius: CARD_RADIUS))
        }
    }

    private var editButton: some View {
        NavigationLink(destination: GapEditScreen(gapId: gapId)) {
            Text(L("edit"))
                .foregroundColor(Colors.white)
                .frame(maxWidth: .infinity, minHeight: BUTTON_HEIGHT)
                .background(Colors.green)
                .clipShape(Capsule())
        }
        .padding(.horizontal, PADDING)
        .padding(.bottom, PADDING)
    }
}

/// What the colours of the chart mean.
private struct ChartLegend: View {
    private let entries: [(String, String)] = [
        (HexColorsKt.ELEMENTS_BLUE, "legend_ramp"),
        (HexColorsKt.TRACE_GREEN, "legend_trajectory"),
        (HexColorsKt.TOLERANCE_OLIVE, "legend_tolerance"),
        (HexColorsKt.MIN_R_LINE, "legend_min_radius")
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            LazyVGrid(
                columns: [GridItem(.flexible(), alignment: .leading), GridItem(.flexible(), alignment: .leading)],
                alignment: .leading,
                spacing: 4
            ) {
                ForEach(entries, id: \.1) { entry in
                    HStack(spacing: 8) {
                        Capsule().fill(Color(hex: entry.0)).frame(width: 20, height: 4)
                        Text(L(entry.1)).font(.caption).foregroundColor(Colors.darkGray)
                    }
                }
            }
            Text(L("axis_unit"))
                .font(.caption)
                .foregroundColor(Colors.darkGray.opacity(0.75))
        }
    }
}
