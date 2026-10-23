//
//  Extensions.swift
//  iosGapCalc
//
//  Created by Sergey Velesko on 29.10.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import sharedGapCalc

extension GapListState {
    func data() -> GapListState.GapListData? {
        return self as? GapListState.GapListData
    }
}
