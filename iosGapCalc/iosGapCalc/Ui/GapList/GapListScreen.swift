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

struct GapListScreen: View {
    @StateObject private var observable = GapListObservable()

    var body: some View {
        ZStack(alignment: .bottom) {
            Colors.lightGray.ignoresSafeArea()
            ScrollView {
                GapListContent(
                    gapListState: observable.state,
                    onGapRemovedClicked: observable.remove
                )
                .padding(.bottom, BUTTON_HEIGHT + PADDING * 2)
            }
            PrimaryButton(title: L("add"), fullWidth: false, action: observable.add)
                .padding(.bottom, PADDING)
        }
        .navigationTitle(L("gap_list_title"))
        .onAppear(perform: observable.refresh)
    }
}

struct GapListContent: View {
    let gapListState: GapListState
    let onGapRemovedClicked: (Int64) -> Void
    
    var body: some View {
        if let state = gapListState.data() {
            GapListDataView(
                currenState: state,
                onGapRemovedClicked: onGapRemovedClicked
            )
        } else {
            NoData()
                .frame(maxWidth: .infinity, minHeight: 300)
        }
    }
}

struct GapListDataView: View {
    let currenState: GapListState.GapListData
    let onGapRemovedClicked: (Int64) -> Void
    
    var body: some View {
        GapList(
            gapList: currenState.gapList,
            onGapRemovedClicked: onGapRemovedClicked
        )
    }
}

struct GapList: View {
    let gapList: [GapListItem]
    let onGapRemovedClicked: (Int64) -> Void
    
    var body: some View {
        LazyVStack(spacing: ITEM_SPACING) {
            ForEach(gapList, id: \GapListItem.id) { item in
                NavigationLink(destination: GapDetailsScreen(gapId: item.id)) {
                    GapListItemView(
                        gapListItem: item,
                        onGapRemovedClicked: onGapRemovedClicked
                    )
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
        .padding(.horizontal, PADDING)
        .padding(.top, PADDING)
    }
}

struct GapListItemView: View {
    let gapListItem: GapListItem
    let onGapRemovedClicked: (Int64) -> Void
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(gapListItem.title.isEmpty ? L("untitled") : gapListItem.title)
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
        // the end caps of the capsule are concentric with the round remove button
        .frame(minHeight: CLOSE_BUTTON_SIZE + PADDING * 2)
        .background(Colors.white)
        .clipShape(Capsule())
    }
}

struct GapListScreen_Previews: PreviewProvider {
    static var previews: some View {
        GapList(gapList: 
            [
                GapListItem(
                    id: 123,
                    title: "Jump 1",
                    date: "2023-10-01"
                ),
                GapListItem(
                    id: 234,
                    title: "Jump 2",
                    date: "2021-10-02"
                ),
                GapListItem(
                    id: 1234,
                    title: "Jump 3",
                    date: "2021-10-02"
                )
            ],
                onGapRemovedClicked: { id in }
        )
        .background(Colors.lightGray)
    }
}
