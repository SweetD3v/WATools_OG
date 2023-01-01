package com.gbversion.tool.statussaver.whatsapp_tools.wa_web

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.gbversion.tool.statussaver.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BlobDownloader(private var context: Context?) {

    private val sameFileDownloadTimeout: Long = 100

    companion object {

        val JsInstance = "Downloader"

        private var lastDownloadBlob = ""
        private var lastDownloadTime: Long = 0

        fun getBase64StringFromBlobUrl(blobUrl: String?): String {
            return if (blobUrl?.startsWith("blob") == true) {
                "javascript: var xhr = new XMLHttpRequest();" +
                        "xhr.open('GET', '" + blobUrl + "', true);" +
                        "xhr.responseType = 'blob';" +
                        "xhr.onload = function(e) {" +
                        "    if (this.status == 200) {" +
                        "        var blobFile = this.response;" +
                        "        var reader = new FileReader();" +
                        "        reader.readAsDataURL(blobFile);" +
                        "        reader.onloadend = function() {" +
                        "            base64data = reader.result;" +
                        "            " + JsInstance + ".getBase64FromBlobData(base64data);" +
                        "        }" +
                        "    }" +
                        "};" +
                        "xhr.send();"
            } else "javascript: console.log('It is not a Blob URL');"
        }
    }

    @JavascriptInterface
    @Throws(IOException::class)
    fun getBase64FromBlobData(base64Data: String) {
        Log.d(WebviewActivity.DEBUG_TAG, "Download triggered " + System.currentTimeMillis())
        lastDownloadTime = System.currentTimeMillis()
        if (System.currentTimeMillis() - lastDownloadTime < sameFileDownloadTimeout) {
            Log.d(WebviewActivity.DEBUG_TAG, "Download within sameFileDownloadTimeout")
            if (lastDownloadBlob == base64Data) {
                Log.d(WebviewActivity.DEBUG_TAG, "Blobs match, ignoring download")
            } else {
                Log.d(WebviewActivity.DEBUG_TAG, "Blobs do not match, downloading")
                lastDownloadBlob = base64Data
                convertBase64StringToFileAndStoreIt(base64Data)
            }
        }
    }

    @Throws(IOException::class)
    private fun convertBase64StringToFileAndStoreIt(base64File: String) {
        val notificationId = System.currentTimeMillis().toInt()
        val strings = base64File.split(",".toRegex()).toTypedArray()
        var extension = MimeTypes.lookupExt(strings[0])
        if (null == extension) {
            extension = strings[0]
            extension = extension.substring(extension.indexOf('/') + 1, extension.indexOf(';'))
        }
        @SuppressLint("SimpleDateFormat") //SDF is just fine for filename
        val currentDateTime = SimpleDateFormat("yyyyMMdd-hhmmss").format(Date())
        val dlFileName = "WAWTG_$currentDateTime.$extension"
        val dlFilePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + File.separator + dlFileName
        )
        val fileAsBytes = Base64.decode(base64File.replaceFirst(strings[0].toRegex(), ""), 0)
        val os = FileOutputStream(dlFilePath, false)
        os.write(fileAsBytes)
        os.flush()
        if (dlFilePath.exists()) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val apkURI = FileProvider.getUriForFile(
                context!!,
                context!!.applicationContext.packageName + ".provider",
                dlFilePath
            )
            intent.setDataAndType(
                apkURI,
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val notificationChannelId = "Downloads"
            val notificationManager =
                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    ?: return
            val notification: Notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    notificationChannelId,
                    "name",
                    NotificationManager.IMPORTANCE_LOW
                )
                notification = Notification.Builder(context, notificationChannelId)
                    .setContentText(
                        String.format(
                            context!!.getString(R.string.notification_text_saved_as),
                            dlFileName
                        )
                    )
                    .setContentTitle(context!!.getString(R.string.notification_title_tap_to_open))
                    .setContentIntent(pendingIntent)
                    .setChannelId(notificationChannelId)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .build()
                notificationManager.createNotificationChannel(notificationChannel)
            } else {
                notification = NotificationCompat.Builder(context!!, notificationChannelId)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(
                        String.format(
                            context!!.getString(R.string.notification_text_saved_as),
                            dlFileName
                        )
                    )
                    .setContentText(context!!.getString(R.string.notification_title_tap_to_open))
                    .build()
            }
            notificationManager.notify(notificationId, notification)
            Toast.makeText(
                context,
                R.string.toast_saved_to_downloads_folder,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}