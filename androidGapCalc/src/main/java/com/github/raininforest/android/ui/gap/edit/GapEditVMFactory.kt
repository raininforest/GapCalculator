package com.github.raininforest.android.ui.gap.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.raininforest.data.GapEditRepository
import com.github.raininforest.ui.edit.GapEditViewModel

internal class GapEditVMFactory(private val gapEditRepository: GapEditRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GapEditViewModel(gapEditRepository = gapEditRepository) as T
    }
}
