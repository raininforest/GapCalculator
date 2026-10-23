package com.github.raininforest.share

import com.github.raininforest.usecases.share.ShareService
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class IosShareService : ShareService {
    override fun share(data: String) {
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        val activityController = UIActivityViewController(
            activityItems = listOf(data),
            applicationActivities = null
        )
        root.topPresented().presentViewController(activityController, animated = true, completion = null)
    }

    private fun UIViewController.topPresented(): UIViewController =
        presentedViewController?.topPresented() ?: this
}
