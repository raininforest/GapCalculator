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
let CLOSE_BUTTON_ICON_NAME = "xmark"

@available(iOS 15.0, *)
struct GapListItemView: View {
    let gapListItem: GapListItem
    let onGapClicked: (Int64) -> Void = {_ in }
    let onGapRemovedClicked: (Int64) -> Void = {_ in }
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(gapListItem.title)
                    .font(.body)
                    .foregroundColor(Colors.darkGray)
                Text(gapListItem.date)
                    .font(.body)
                    .foregroundColor(Colors.darkGray)
            }
            Spacer()
            Button(
                action: { onGapRemovedClicked(gapListItem.id) }
            ) {
                Image(systemName: CLOSE_BUTTON_ICON_NAME)
                    .foregroundColor(Colors.white)
                    .frame(
                        width: CLOSE_BUTTON_SIZE,
                        height: CLOSE_BUTTON_SIZE
                    )
            }
            .background(Colors.darkestGray)
            .clipShape(Circle())
        }
        .padding(.all, PADDING)
        .background(Colors.white)
        .clipShape(
            RoundedRectangle(cornerRadius: ITEM_CORNER_RADIUS)
        )
        .onTapGesture {
            onGapClicked(gapListItem.id)
        }
    }
}

@available(iOS 15.0, *)
struct GapListScreen_Previews: PreviewProvider {
    static var previews: some View {
        GapListItemView(
            gapListItem: GapListItem(
                id: 123,
                title: "Без названия",
                date: "2023-10-03"
            )
        )
            .background(.gray)
    }
}
