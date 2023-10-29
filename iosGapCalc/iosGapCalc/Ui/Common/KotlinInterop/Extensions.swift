//
//  Extensions.swift
//  iosGapCalc
//
//  Created by Sergey Velesko on 29.10.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import sharedGapCalc

enum GapListUiState {
    case success(data: GapListState.GapListData)
    case empty
}

extension GapListUiState {
    init?(_ value: GapListState) {
        switch value {
        case let success as GapListState.GapListData:
            self = GapListUiState.success(data: success)
        default:
            self = GapListUiState.empty
        }
    }
}

extension GapListState {
    func data() -> GapListState.GapListData? {
        return self as? GapListState.GapListData
    }
}
