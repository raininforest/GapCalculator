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
let ITEM_SPACING = 8.0
let ITEM_CORNER_RADIUS = 32.0
let CLOSE_BUTTON_SIZE = 48.0
let CLOSE_BUTTON_ICON_NAME = "xmark"

//struct GapListScreen: View {
//    let onGapCLicked: (Int64) -> Void
//    //todo init vm and collect state
//    
//    var body: some View {
//        
//    }
//}

struct Content: View {
    let gapListState: GapListState
    let onGapCLicked: (Int64) -> Void
    let onGapRemovedClicked: (Int64) -> Void
    
    var body: some View {
        if let state = gapListState.data() {
            Data(
                currenState: state,
                onGapCLicked: onGapCLicked,
                onGapRemovedClicked: onGapRemovedClicked
            )
        } else {
            NoData()
        }
    }
}

struct Data: View {
    let currenState: GapListState.GapListData
    let onGapCLicked: (Int64) -> Void
    let onGapRemovedClicked: (Int64) -> Void
    
    var body: some View {
        GapList(
            gapList: currenState.gapList,
            onGapCLicked: onGapCLicked,
            onGapRemovedClicked: onGapRemovedClicked
        )
    }
}

struct GapList: View {
    let gapList: [GapListItem]
    let onGapCLicked: (Int64) -> Void
    let onGapRemovedClicked: (Int64) -> Void
    
    var body: some View {
        LazyVStack(spacing: ITEM_SPACING) {
            ForEach(gapList, id: \GapListItem.id) { item in
                GapListItemView(
                    gapListItem: item,
                    onGapClicked: onGapCLicked,
                    onGapRemovedClicked: onGapRemovedClicked
                )
            }
        }
        .padding(.horizontal, PADDING)
        .frame(maxHeight: .infinity, alignment: .topLeading)
    }
}

struct GapListItemView: View {
    let gapListItem: GapListItem
    let onGapClicked: (Int64) -> Void
    let onGapRemovedClicked: (Int64) -> Void
    
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

struct GapListScreen_Previews: PreviewProvider {
    static var previews: some View {
        GapList(gapList: 
            [
                GapListItem(
                    id: 123,
                    title: "Без названия 1",
                    date: "2023-10-01"
                ),
                GapListItem(
                    id: 234,
                    title: "Без названия 2",
                    date: "2021-10-02"
                ),
                GapListItem(
                    id: 1234,
                    title: "Без названия 3",
                    date: "2021-10-02"
                )
            ],
                onGapCLicked: { id in },
                onGapRemovedClicked: { id in }
        )
        .background(Colors.lightGray)
    }
}
