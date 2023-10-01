package com.github.raininforest.di

import com.github.raininforest.db.DriverFactory
import com.github.raininforest.usecases.share.ShareService

interface PlatformDependencies {
    val sqlDriverFactory: DriverFactory
    val shareService: ShareService
}
