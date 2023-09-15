package com.github.raininforest.di

import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.data.GapEditRepository
import com.github.raininforest.data.GapListRepository

object Dependencies {
    val gapListRepository: GapListRepository
        get() = GapListRepository()

    val gapDetailsRepository: GapDetailsRepository
        get() = GapDetailsRepository()

    val gapEditRepository: GapEditRepository
        get() = GapEditRepository()
}
