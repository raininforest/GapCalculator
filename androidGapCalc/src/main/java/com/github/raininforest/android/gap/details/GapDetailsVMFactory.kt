package com.github.raininforest.android.gap.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.ui.details.DetailsViewModel

internal class GapDetailsVMFactory(private val gapDetailsRepository: GapDetailsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsViewModel(gapDetailsRepository = gapDetailsRepository) as T
    }
}
