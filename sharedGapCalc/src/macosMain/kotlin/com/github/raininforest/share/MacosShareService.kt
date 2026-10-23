package com.github.raininforest.share

import com.github.raininforest.usecases.share.ShareService
import platform.AppKit.NSPasteboard
import platform.AppKit.NSPasteboardTypeString

/** The text goes to the general pasteboard: a share picker needs a window to anchor to, which this layer has none. */
class MacosShareService : ShareService {
    override fun share(data: String) {
        val pasteboard = NSPasteboard.generalPasteboard
        pasteboard.clearContents()
        pasteboard.setString(data, forType = NSPasteboardTypeString)
    }
}
