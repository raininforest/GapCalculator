package com.github.raininforest.di

import com.github.raininforest.db.DriverFactory
import com.github.raininforest.db.NativeDriverFactory
import com.github.raininforest.share.MacosShareService
import com.github.raininforest.usecases.share.ShareService

class MacosPlatformDependencies : PlatformDependencies {
    override val sqlDriverFactory: DriverFactory
        get() = NativeDriverFactory()

    override val shareService: ShareService
        get() = MacosShareService()
}
