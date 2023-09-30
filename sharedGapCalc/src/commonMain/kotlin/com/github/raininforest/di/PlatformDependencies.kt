package com.github.raininforest.di

import com.github.raininforest.db.DriverFactory
import com.github.raininforest.share.ShareService

interface PlatformDependencies {
    val sqlDriverFactory: DriverFactory
    val shareService: ShareService
}
