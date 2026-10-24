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
                        GapEditRow(parameter: .gap, field: observable.gap, observable: observable)
                        GapEditRow(parameter: .table, field: observable.table, observable: observable)
                        GapEditRow(parameter: .startHeight, field: observable.startHeight, observable: observable)
                        GapEditRow(parameter: .startAngle, field: observable.startAngle, observable: observable)
                        GapEditRow(parameter: .finishHeight, field: observable.finishHeight, observable: observable)
                        GapEditRow(parameter: .finishAngle, field: observable.finishAngle, observable: observable)
                        GapEditRow(parameter: .startSpeed, field: observable.startSpeed, observable: observable)
                    }
                    .padding(PADDING)
                }
                PrimaryButton(title: L("apply")) {
                    observable.apply { presentationMode.wrappedValue.dismiss() }
                }
                .padding(.vertical, PADDING)
            }
        }
        .toolbar {
            // the numbers keyboard has no return key
            ToolbarItemGroup(placement: .keyboard) {
                Spacer()
                Button(L("done")) {
                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                }
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
    let observable: GapEditObservable

    private var isValid: Bool { observable.isValid(field.text) }

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
            // a tinted box so it reads as an input, not as a plain number
            TextField("0", text: Binding(get: { field.text }, set: field.set))
                .keyboardType(.numbersAndPunctuation)
                .multilineTextAlignment(.trailing)
                .font(.title2)
                .foregroundColor(Colors.darkGray)
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .frame(width: 124)
                .background(Colors.darkGray.opacity(0.08))
                .clipShape(RoundedRectangle(cornerRadius: 20))
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Colors.warningRed, lineWidth: isValid ? 0 : 2)
                )
                .accessibilityLabel(parameter.inputLabel)
                .accessibilityValue(isValid ? field.text : L("invalid_number"))
        }
        .padding(PADDING)
        .frame(minHeight: 72)
        .background(Colors.white)
        .clipShape(RoundedRectangle(cornerRadius: ITEM_CORNER_RADIUS))
    }
}
