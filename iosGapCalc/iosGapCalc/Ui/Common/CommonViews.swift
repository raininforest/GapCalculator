//
//  CommonViews.swift
//  iosGapCalc
//
//  Created by Sergey Velesko on 29.10.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

let BUTTON_HEIGHT = 48.0

struct NoData: View {
    var body: some View {
        Text(L("empty"))
            .foregroundColor(Colors.darkGray)
    }
}

struct EmptyListView: View {
    var body: some View {
        VStack(spacing: ITEM_SPACING) {
            Text(L("empty_list_title"))
                .font(.headline)
                .foregroundColor(Colors.darkGray)
            Text(L("empty_list_hint"))
                .font(.subheadline)
                .foregroundColor(Colors.darkGray.opacity(0.75))
                .multilineTextAlignment(.center)
        }
        .padding(PADDING)
        .frame(maxWidth: .infinity, minHeight: 300)
        .accessibilityElement(children: .combine)
    }
}

struct SectionTitle: View {
    let text: String

    init(_ text: String) { self.text = text }

    var body: some View {
        Text(text)
            .font(.subheadline.weight(.semibold))
            .foregroundColor(Colors.darkGray.opacity(0.75))
            .accessibilityAddTraits(.isHeader)
    }
}

/// "2026-10-20 18:05:11.000" -> "Oct 20, 2026 at 6:05 PM" in the user's locale; unknown formats stay as they are.
func formatGapDate(_ raw: String) -> String {
    let parser = DateFormatter()
    parser.locale = Locale(identifier: "en_US_POSIX")
    parser.dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
    guard let date = parser.date(from: raw) else { return raw }
    let output = DateFormatter()
    output.dateStyle = .medium
    output.timeStyle = .short
    return output.string(from: date)
}

struct PrimaryButton: View {
    let title: String
    var fullWidth: Bool = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.body)
                .foregroundColor(Colors.white)
                .padding(.horizontal, 24)
                .frame(maxWidth: fullWidth ? .infinity : nil, minHeight: BUTTON_HEIGHT)
                .background(Colors.green)
                .clipShape(Capsule())
        }
        .padding(.horizontal, fullWidth ? PADDING : 0)
    }
}

/// One font for all toolbar icons: SF Symbols differ in stroke weight unless it is set explicitly.
let TOOLBAR_ICON_FONT = Font.system(size: 17, weight: .semibold)

struct BackToolbarButton: ToolbarContent {
    let action: () -> Void

    var body: some ToolbarContent {
        ToolbarItem(placement: .navigationBarLeading) {
            Button(action: action) {
                Image(systemName: "chevron.left").font(TOOLBAR_ICON_FONT)
            }
        }
    }
}

/// Hiding the system back button also disables the swipe-back gesture; this brings it back.
extension UINavigationController: UIGestureRecognizerDelegate {
    override open func viewDidLoad() {
        super.viewDidLoad()
        interactivePopGestureRecognizer?.delegate = self
    }

    public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        viewControllers.count > 1
    }
}
