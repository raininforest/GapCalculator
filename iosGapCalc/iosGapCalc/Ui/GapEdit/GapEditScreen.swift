//
//  GapEditScreen.swift
//  iosGapCalc
//

import SwiftUI
import sharedGapCalc

private let HINT_ICON_SIZE = 40.0

struct GapEditScreen: View {
    let gapId: Int64
    @StateObject private var observable = GapEditObservable()
    @Environment(\.presentationMode) private var presentationMode

    var body: some View {
        ZStack {
            Colors.lightGray.ignoresSafeArea()
            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: ITEM_SPACING) {
                        GapEditTitleRow(observable: observable)
                        GapEditRow(parameter: .gap, field: observable.gap)
                        GapEditRow(parameter: .table, field: observable.table)
                        GapEditRow(parameter: .startHeight, field: observable.startHeight)
                        GapEditRow(parameter: .startAngle, field: observable.startAngle)
                        GapEditRow(parameter: .finishHeight, field: observable.finishHeight)
                        GapEditRow(parameter: .finishAngle, field: observable.finishAngle)
                        GapEditRow(parameter: .startSpeed, field: observable.startSpeed)
                    }
                    .padding(PADDING)
                }
                PrimaryButton(title: L("apply")) {
                    observable.apply { presentationMode.wrappedValue.dismiss() }
                }
                .padding(.vertical, PADDING)
            }
        }
        .navigationTitle(L("edit_title"))
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .toolbar { BackToolbarButton { presentationMode.wrappedValue.dismiss() } }
        .onAppear { observable.load(gapId: gapId) }
    }
}

private struct GapEditTitleRow: View {
    @ObservedObject var observable: GapEditObservable
    @ObservedObject private var title: StringFlowField

    init(observable: GapEditObservable) {
        self.observable = observable
        self.title = observable.title
    }

    var body: some View {
        TextField(L("untitled"), text: Binding(get: { title.text }, set: title.set))
            .font(.title3)
            .foregroundColor(Colors.darkGray)
            .padding(PADDING)
            .background(Colors.white)
            .clipShape(RoundedRectangle(cornerRadius: ITEM_CORNER_RADIUS))
    }
}

private struct GapEditRow: View {
    let parameter: GapParameter
    @ObservedObject var field: StringFlowField

    var body: some View {
        HStack(spacing: ITEM_SPACING) {
            if let icon = parameter.iconName {
                Image(icon)
                    .resizable()
                    .renderingMode(.template)
                    .scaledToFit()
                    .frame(width: HINT_ICON_SIZE, height: HINT_ICON_SIZE)
                    .foregroundColor(Colors.darkGray)
            }
            Text(parameter.inputLabel)
                .font(.body)
                .foregroundColor(Colors.darkGray)
                .lineLimit(2)
            Spacer()
            TextField("0", text: Binding(get: { field.text }, set: field.set))
                .keyboardType(.numbersAndPunctuation)
                .multilineTextAlignment(.trailing)
                .font(.title2)
                .foregroundColor(Colors.darkGray)
                .frame(width: 124)
        }
        .padding(PADDING)
        .frame(minHeight: 72)
        .background(Colors.white)
        .clipShape(RoundedRectangle(cornerRadius: ITEM_CORNER_RADIUS))
    }
}
