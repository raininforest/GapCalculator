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

private struct GapDetailsContent: View {
    let data: GapDetailsState.GapDetailsData
    let gapId: Int64

    var body: some View {
        GeometryReader { geometry in
            if geometry.size.width > geometry.size.height {
                // landscape: parameters on the left, the chart gets the full height on the right
                HStack(alignment: .top, spacing: PADDING) {
                    ScrollView {
                        VStack(alignment: .leading, spacing: PADDING) {
                            parameters(data.textData.inputGapParameters, alignment: .leading)
                            parameters(data.textData.outputGapParameters, alignment: .leading)
                            warnings
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .frame(width: geometry.size.width * 0.3)
                    VStack(spacing: PADDING) {
                        TrajectoryChartView(chartData: data.chartData)
                        editButton
                    }
                }
                .padding(.horizontal, PADDING)
                .padding(.top, PADDING)
            } else {
                VStack(spacing: PADDING) {
                    HStack(alignment: .top) {
                        parameters(data.textData.inputGapParameters, alignment: .leading)
                        Spacer()
                        parameters(data.textData.outputGapParameters, alignment: .trailing)
                    }
                    .padding(.horizontal, PADDING)
                    warnings.padding(.horizontal, PADDING)
                    TrajectoryChartView(chartData: data.chartData)
                        .padding(.horizontal, PADDING)
                    editButton.padding(.horizontal, PADDING)
                }
                .padding(.top, PADDING)
            }
        }
    }

    private func parameters(_ lines: [ParameterValue], alignment: TextAlignment) -> some View {
        Text(parameterLines(lines))
            .font(.caption)
            .foregroundColor(Colors.darkGray)
            .multilineTextAlignment(alignment)
    }

    @ViewBuilder
    private var warnings: some View {
        if !data.warnings.isEmpty {
            Text(data.warnings.map { $0.text }.joined(separator: "\n"))
                .font(.body)
                .foregroundColor(Colors.white)
                .frame(maxWidth: .infinity)
                .padding(PADDING)
                .background(Colors.darkestGray)
                .clipShape(RoundedRectangle(cornerRadius: ITEM_CORNER_RADIUS / 2))
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
        .padding(.bottom, PADDING)
    }
}
