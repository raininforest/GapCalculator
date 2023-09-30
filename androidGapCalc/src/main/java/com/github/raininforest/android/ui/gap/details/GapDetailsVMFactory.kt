package com.github.raininforest.android.ui.gap.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.share.ShareService
import com.github.raininforest.ui.details.GapDetailsViewModel

internal class GapDetailsVMFactory(
    private val gapDetailsRepository: GapDetailsRepository,
    private val shareService: ShareService
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GapDetailsViewModel(
            gapDetailsRepository = gapDetailsRepository,
            shareService = shareService
        ) as T
    }
}
