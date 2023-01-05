package com.gbversion.tool.statussaver.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.RelativeLayout
import android.widget.Toast
import com.gbversion.tool.statussaver.models.Status
import com.google.android.material.snackbar.Snackbar
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AppUtils {
    companion object {
        val USER_AGENT = "User-Agent"
        var CLEANER_TYPE = 0
        var APP_DIR: String? = null
        val MICRO_KIND = 3
        val MINI_KIND = 1
        val STATUS_DIRECTORY =
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator.toString() + "WhatsApp/Media/.Statuses")

        fun copyFile(status: Status, context: Context?, relativeLayout: RelativeLayout?) {
            val str: String
            val file = File(APP_DIR.toString())
            if (!file.exists() && !file.mkdirs()) {
                Snackbar.make(relativeLayout!!, "Something went wrong", -1).show()
            }
            val format: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            str = if (status.isVideo) {
                "VID_$format.mp4"
            } else {
                "IMG_$format.jpg"
            }
            val file2 = File(file.toString() + File.separator + str)
            try {
                if (status.isApi30) {
                    return
                }
                FileUtils.copyFile(status.file, file2)
                file2.setLastModified(System.currentTimeMillis())
                SingleMediaScanner(context, file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun download(
            downloadPath: String?,
            destinationPath: String,
            context: Context,
            fileName: String
        ) {
            Toast.makeText(context, "Downloading started...", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse(downloadPath)
            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(fileName)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                destinationPath + fileName
            )
            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
        }

    }
}