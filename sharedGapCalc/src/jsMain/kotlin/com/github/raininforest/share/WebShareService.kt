package com.github.raininforest.share

import com.github.raininforest.usecases.share.ShareService
import kotlinx.browser.window

/** Uses the Web Share API where the browser has it, otherwise copies the text to the clipboard. */
class WebShareService : ShareService {
    override fun share(data: String) {
        val navigator = window.navigator.asDynamic()
        if (navigator.share != undefined) {
            navigator.share(js("({text: data})"))
        } else {
            navigator.clipboard.writeText(data)
        }
    }
}
