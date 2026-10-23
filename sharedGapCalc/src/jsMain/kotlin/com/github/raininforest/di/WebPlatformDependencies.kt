package com.github.raininforest.di

import com.github.raininforest.db.DriverFactory
import com.github.raininforest.db.WebDriverFactory
import com.github.raininforest.share.WebShareService
import com.github.raininforest.usecases.share.ShareService

class WebPlatformDependencies : PlatformDependencies {
    override val sqlDriverFactory: DriverFactory
        get() = WebDriverFactory()

    override val shareService: ShareService
        get() = WebShareService()
}
