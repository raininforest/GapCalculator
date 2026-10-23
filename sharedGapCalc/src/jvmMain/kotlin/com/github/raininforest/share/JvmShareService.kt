package com.github.raininforest.share

import com.github.raininforest.usecases.share.ShareService
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/** There is no share sheet on desktop JVM: the text goes to the clipboard. */
class JvmShareService : ShareService {
    override fun share(data: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(data), null)
    }
}
