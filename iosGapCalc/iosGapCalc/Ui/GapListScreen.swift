//
//  GapList.swift
//  iosGapCalc
//
//  Created by Sergey Velesko on 08.10.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import sharedGapCalc

let PADDING = 16.0
let ITEM_CORNER_RADIUS = 32.0
let CLOSE_BUTTON_SIZE = 48.0

@available(iOS 15.0, *)
struct GapListItem: View {
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text("Без названия")
                    .font(.title2)
                Text("2023-10-04")
                    .font(.title3)
            }
            Spacer()
            Button(action: {}) {
                Image(systemName: "xmark")
                    .foregroundColor(.white)
                    .frame(width: CLOSE_BUTTON_SIZE, height: CLOSE_BUTTON_SIZE)
            }
            .background(.black)
            .clipShape(Circle())
        }
        .padding(.all, PADDING)
        .background(.white)
        .clipShape(RoundedRectangle(
            cornerRadius: ITEM_CORNER_RADIUS)
        )
    }
}

@available(iOS 15.0, *)
struct GapListScreen_Previews: PreviewProvider {
    static var previews: some View {
        GapListItem()
            .background(.gray)
    }
}
