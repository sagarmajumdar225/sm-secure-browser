package com.smsecure.browser.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import org.mozilla.geckoview.WebResponse
import timber.log.Timber

object DownloadHelper {
    fun downloadFile(context: Context, response: WebResponse) {
        val uri = Uri.parse(response.uri)
        val request = DownloadManager.Request(uri)

        val contentDisposition = response.headers["Content-Disposition"]
        val mimeType = response.headers["Content-Type"]

        var fileName = uri.lastPathSegment ?: "download"
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            fileName = contentDisposition.substringAfter("filename=")
                .substringBefore(";")
                .trim('"', ' ')
        }

        if (mimeType != null) {
            request.setMimeType(mimeType)
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        try {
            dm.enqueue(request)
        } catch (e: Exception) {
            Timber.e(e, "Failed to enqueue download")
        }
    }
}
