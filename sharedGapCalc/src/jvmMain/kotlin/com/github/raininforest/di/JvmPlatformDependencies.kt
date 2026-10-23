package com.github.raininforest.di

import com.github.raininforest.db.DriverFactory
import com.github.raininforest.db.JvmDriverFactory
import com.github.raininforest.share.JvmShareService
import com.github.raininforest.usecases.share.ShareService

class JvmPlatformDependencies : PlatformDependencies {
    override val sqlDriverFactory: DriverFactory
        get() = JvmDriverFactory()

    override val shareService: ShareService
        get() = JvmShareService()
}
