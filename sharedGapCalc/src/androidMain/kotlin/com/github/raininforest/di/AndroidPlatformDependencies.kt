package com.github.raininforest.di

import android.content.Context
import com.github.raininforest.db.AndroidDriverFactory
import com.github.raininforest.db.DriverFactory
import com.github.raininforest.share.AndroidShareService
import com.github.raininforest.share.ShareService

class AndroidPlatformDependencies(private val context: Context) : PlatformDependencies {
    override val sqlDriverFactory: DriverFactory
        get() = AndroidDriverFactory(context = context)

    override val shareService: ShareService
        get() = AndroidShareService(context)
}
