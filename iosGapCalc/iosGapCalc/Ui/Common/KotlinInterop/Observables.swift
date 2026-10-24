//
//  Observables.swift
//  iosGapCalc
//
//  SwiftUI adapters over the shared Kotlin view-models: they collect StateFlows
//  into @Published properties and clear the view-model's scope on deinit.
//

import Foundation
import SwiftUI
import sharedGapCalc

enum AppDependencies {
    static let shared = Dependencies(platformDependencies: IosPlatformDependencies())
}

final class GapListObservable: ObservableObject {
    @Published private(set) var state: GapListState = GapListState.GapListEmpty.shared

    private let viewModel = GapListViewModel(gapListRepository: AppDependencies.shared.gapListRepository)
    private var watcher: FlowWatcher<GapListState>?

    init() {
        watcher = FlowWatcher(flow: viewModel.gapList) { [weak self] in self?.state = $0 }
    }

    func refresh() { viewModel.fetchList() }
    func add() { viewModel.addClicked() }
    func remove(id: Int64) { viewModel.removeGapClicked(id: id) }

    deinit {
        watcher?.cancel()
        viewModel.dispose()
    }
}

final class GapDetailsObservable: ObservableObject {
    @Published private(set) var state: GapDetailsState = GapDetailsState.Loading.shared

    private let viewModel = GapDetailsViewModel(gapDetailsRepository: AppDependencies.shared.gapDetailsRepository)
    private var watcher: FlowWatcher<GapDetailsState>?

    init() {
        watcher = FlowWatcher(flow: viewModel.gapDetails) { [weak self] in self?.state = $0 }
    }

    func load(gapId: Int64) { viewModel.fetchGapDetails(gapId: gapId) }
    func share() {
        guard let data = state as? GapDetailsState.GapDetailsData else { return }
        AppDependencies.shared.shareService.share(data: shareText(data.textData))
    }

    deinit {
        watcher?.cancel()
        viewModel.dispose()
    }
}

/// Two-way bridge for a text field owned by the Kotlin view-model; writes go through `write`.
final class StringFlowField: ObservableObject {
    @Published private(set) var text: String = ""

    private let write: (String) -> Void
    private let transform: (String) -> String
    private var watcher: FlowWatcher<NSString>?

    /// `transform` filters what the user typed before it is shown (the view-model applies the same filter).
    init(
        flow: Kotlinx_coroutines_coreStateFlow,
        transform: @escaping (String) -> String = { $0 },
        write: @escaping (String) -> Void
    ) {
        self.write = write
        self.transform = transform
        watcher = FlowWatcher(flow: flow) { [weak self] in self?.text = $0 as String }
    }

    func set(_ newValue: String) {
        let value = transform(newValue)
        text = value
        write(value)
    }

    func cancel() { watcher?.cancel() }
}

final class GapEditObservable: ObservableObject {
    private let viewModel = GapEditViewModel(gapEditRepository: AppDependencies.shared.gapEditRepository)

    let title: StringFlowField
    let gap: StringFlowField
    let table: StringFlowField
    let startHeight: StringFlowField
    let startAngle: StringFlowField
    let finishHeight: StringFlowField
    let finishAngle: StringFlowField
    let startSpeed: StringFlowField

    init() {
        let vm = viewModel
        title = StringFlowField(flow: vm.gapTitleState) { vm.gapTitleChanged(text: $0) }
        func field(_ field: GapEditField) -> StringFlowField {
            StringFlowField(
                flow: vm.fieldState(field: field),
                transform: { vm.sanitized(text: $0) },
                write: { vm.onFieldChanged(field: field, text: $0) }
            )
        }
        gap = field(.gap)
        table = field(.table)
        startHeight = field(.startHeight)
        startAngle = field(.startAngle)
        finishHeight = field(.finishHeight)
        finishAngle = field(.finishAngle)
        startSpeed = field(.startSpeed)
    }

    func load(gapId: Int64) { viewModel.fetchGapParameters(gapId: gapId) }

    func isValid(_ text: String) -> Bool { viewModel.isFieldValid(text: text) }

    func apply(onSaved: @escaping () -> Void) { viewModel.onApplyClicked(onSaved: onSaved) }

    deinit {
        [title, gap, table, startHeight, startAngle, finishHeight, finishAngle, startSpeed].forEach { $0.cancel() }
        viewModel.dispose()
    }
}
