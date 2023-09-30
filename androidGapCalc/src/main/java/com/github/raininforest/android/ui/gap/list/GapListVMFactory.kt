package com.github.raininforest.android.ui.gap.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.raininforest.data.GapListRepository
import com.github.raininforest.ui.list.GapListViewModel

internal class GapListVMFactory(private val gapListRepository: GapListRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GapListViewModel(gapListRepository = gapListRepository) as T
    }
}
