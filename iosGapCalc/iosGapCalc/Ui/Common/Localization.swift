//
//  Localization.swift
//  iosGapCalc
//
//  Strings live in en.lproj / ru.lproj; the shared Kotlin code only hands over enums and numbers.
//

import Foundation
import sharedGapCalc

func L(_ key: String) -> String {
    NSLocalizedString(key, comment: "")
}

extension GapParameter {
    var label: String {
        switch self {
        case .gap: return L("param_gap")
        case .table: return L("param_table")
        case .startHeight: return L("param_start_height")
        case .startAngle: return L("param_start_angle")
        case .finishHeight: return L("param_finish_height")
        case .finishAngle: return L("param_finish_angle")
        case .startSpeed: return L("param_start_speed")
        case .speed0: return L("param_speed_0")
        case .rStartMin: return L("param_r_start_min")
        case .rStart: return L("param_r_start")
        case .hWhereStart: return L("param_h_where_start")
        case .gLanding: return L("param_g_landing")
        default: return ""
        }
    }

    var unitSymbol: String {
        switch unit {
        case .meter: return L("unit_meter")
        case .degree: return L("unit_degree")
        case .kmh: return L("unit_kmh")
        default: return ""
        }
    }

    /// "Takeoff height, m"
    var inputLabel: String { "\(label), \(unitSymbol)" }

    /// Name of the hint drawing in the asset catalog.
    var iconName: String? {
        switch self {
        case .gap: return "HintGap"
        case .table: return "HintTable"
        case .startHeight: return "HintStartheight"
        case .startAngle: return "HintStartangle"
        case .finishHeight: return "HintFinishheight"
        case .finishAngle: return "HintFinishangle"
        case .startSpeed: return "HintSpeed"
        default: return nil
        }
    }
}

extension ParameterValue {
    /// "Gap = 2 m"
    var line: String { "\(parameter.label) = \(value) \(parameter.unitSymbol)" }
}

extension CalculationWarnings {
    var text: String {
        switch self {
        case .hardLanding: return L("warning_hard_landing")
        case .earlyLanding: return L("warning_early_landing")
        case .bigGap: return L("warning_big_gap")
        default: return ""
        }
    }
}

func parameterLines(_ items: [ParameterValue]) -> String {
    items.map { $0.line }.joined(separator: "\n")
}

/// The text that is handed to the share sheet.
func shareText(_ textData: TextData) -> String {
    parameterLines(textData.inputGapParameters + textData.outputGapParameters)
}
