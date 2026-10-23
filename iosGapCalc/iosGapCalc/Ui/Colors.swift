//
//  Colors.swift
//  iosGapCalc
//
//  Created by Sergey Velesko on 08.10.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct Colors {
    static let white: Color = Color("WhiteColor")
    static let lightGray: Color = Color("LightGrayColor")
    static let darkGray: Color = Color("DarkGrayColor")
    static let darkestGray: Color = Color("DarkestGrayColor")
}

extension Colors {
    static let green: Color = Color("GreenColor")
}

extension Color {
    /// Parses `#RRGGBB` or `#AARRGGBB` (same convention as Android's `Color.parseColor`).
    init(hex: String) {
        let digits = hex.trimmingCharacters(in: CharacterSet(charactersIn: "#"))
        var value: UInt64 = 0
        Scanner(string: digits).scanHexInt64(&value)
        let hasAlpha = digits.count == 8
        let alpha = hasAlpha ? Double((value >> 24) & 0xFF) / 255 : 1
        self.init(
            .sRGB,
            red: Double((value >> 16) & 0xFF) / 255,
            green: Double((value >> 8) & 0xFF) / 255,
            blue: Double(value & 0xFF) / 255,
            opacity: alpha
        )
    }
}
