package com.github.raininforest.share

import android.content.Context
import android.content.Intent
import com.github.raininforest.usecases.share.ShareService

class AndroidShareService(private val context: Context) : ShareService {

    private companion object {
        const val MIME_TYPE = "text/plain"
    }

    override fun share(data: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data)
            type = MIME_TYPE
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(shareIntent)
    }
}
