package com.gbversion.tool.statussaver.tools.video_downloader

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityInstaDownloaderHomeBinding
import com.gbversion.tool.statussaver.databinding.DialogConnectingServerBinding
import com.gbversion.tool.statussaver.databinding.DialogMethodsInstaBinding
import com.gbversion.tool.statussaver.databinding.DialogServerDownBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.apis.InstaModel
import com.gbversion.tool.statussaver.tools.apis.POST_TYPE
import com.gbversion.tool.statussaver.tools.apis.RestApiClient
import com.gbversion.tool.statussaver.tools.downloader.BasicImageDownloader
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstaDownloaderHomeActivity : BaseActivity() {

    val binding by lazy { ActivityInstaDownloaderHomeBinding.inflate(layoutInflater) }

    val handler = Handler(Looper.getMainLooper())
    var count = 0
    val runnable = object : Runnable {
        override fun run() {
            count++
            handler.postDelayed(this, 1000)
            if (count >= 10) {
                handler.removeCallbacks(this)
                count = 0
                hideConnectingDialog()
            }
        }
    }

    var alertDialogConnecting: AlertDialog? = null

    fun showConnectingDialog() {
        val bindingConnecting = DialogConnectingServerBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.RoundedCornersDialog)
            .setCancelable(false)
            .setView(bindingConnecting.root)

        if (alertDialogConnecting == null)
            alertDialogConnecting = builder.create()
        alertDialogConnecting?.show()
    }

    fun hideConnectingDialog() {
        alertDialogConnecting?.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@InstaDownloaderHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@InstaDownloaderHomeActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            imgBack.setOnClickListener {
                onBackPressed()
            }

            imgDownloads.setOnClickListener {
                startActivity(
                    Intent(
                        this@InstaDownloaderHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "insta_downloader")
                    }
                )
            }

            btnPaste.setOnClickListener {
                etText.setText(getClipboardItemsSpecific(SMType.INSTA))
            }

            btnDownload.setOnClickListener {
                if (etText.text.isNotEmpty()) {
                    if (NetworkState.isOnline()) {
                        AdsUtils.loadInterstitialAd(this@InstaDownloaderHomeActivity,
                            RemoteConfigUtils.adIdInterstital(),
                            object : AdsUtils.Companion.FullScreenCallback() {
                                override fun continueExecution() {
                                    showMethodsDialog(etText.text.trim().toString())
                                }
                            })
                    } else Toast.makeText(
                        this@InstaDownloaderHomeActivity,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@InstaDownloaderHomeActivity,
                        "Please enter link",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            imgClear.setOnClickListener { etText.text.clear() }
        }
    }

    lateinit var methodsDialogBinding: DialogMethodsInstaBinding

    private fun showMethodsDialog(dUrl: String) {
        var type = 0

        methodsDialogBinding = DialogMethodsInstaBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.RoundedCornersDialog)
            .setCancelable(true)
            .setView(methodsDialogBinding.root)

        val alertDialog = builder.create()
        alertDialog.setOnDismissListener {
            when (type) {
                0 -> {
                    AdsUtils.loadInterstitialAd(this@InstaDownloaderHomeActivity,
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startDownloadVolley(dUrl)
                            }
                        })
                }
                1 -> {
                    AdsUtils.loadInterstitialAd(this@InstaDownloaderHomeActivity,
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startDownload(dUrl)
                            }
                        })
                }
            }
        }
        alertDialog.show()

        methodsDialogBinding.run {
            txtMethod1.setOnClickListener {
                type = 0
                alertDialog.dismiss()
            }
            txtMethod2.setOnClickListener {
                type = 1
                alertDialog.dismiss()
            }
        }
    }

    private fun startDownloadVolley(url: String) {
        val baseUrl = getString(R.string.base_url_insta_new)
        val queue = Volley.newRequestQueue(this)

        showConnectingDialog()
        handler.post(runnable)

        val request: StringRequest = object : StringRequest(
            Method.GET, "$baseUrl?url=$url",
            com.android.volley.Response.Listener { response ->
                var downloadUrl = ""

                try {
                    Log.e("TAG", "instaResponse: ${response}")
                    val obj = JSONObject(response)
                    if (response.contains("video", true))
                        downloadUrl = obj.getString("video")
                    else downloadUrl = obj.getString("image")


                    hideConnectingDialog()
                    handler.removeCallbacks(runnable)
                    count = 0
                    BasicImageDownloader(this@InstaDownloaderHomeActivity)
                        .saveVideoToExternalInsta(
                            downloadUrl
                        ) {
                            Toast.makeText(
                                this@InstaDownloaderHomeActivity,
                                "Video Downloaded.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                } catch (e: JSONException) {
                    Log.e("TAG", "fbExc: ${e.message}")
                    e.printStackTrace()
                    hideConnectingDialog()
                    handler.removeCallbacks(runnable)
                    count = 0
                    showErrorDialog()

//                    startDownload(url)
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Log.e("TAG", "fbExcError: ${error.message}")
                hideConnectingDialog()
                handler.removeCallbacks(runnable)
                count = 0
                showErrorDialog()

//                startDownload(url)
            }) {
//            override fun getParams(): Map<String, String> {
//                val params: MutableMap<String, String> = HashMap()
//                params["url"] = url
//                Log.e("TAG", "getParamsUrl: $url")
//                return params
//            }

//            override fun getUrl(): String {
//                val stringBuilder = StringBuilder()
//                var key: String
//                var value: String
//                try {
//                    key = URLEncoder.encode("url", "UTF-8")
//                    value = URLEncoder.encode(url, "UTF-8")
//                    stringBuilder.append("?$key=$value")
//                } catch (e: UnsupportedEncodingException) {
//                    e.printStackTrace()
//                }
//                return stringBuilder.toString()
//            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["X-RapidAPI-Key"] = getString(R.string.rapid_api_key)
//                headers["X-RapidAPI-Host"] = getString(R.string.rapid_api_host_insta_new)
                return headers
            }
        }
        queue.add(request)
    }

    private fun startDownload(url: String) {
        Log.e("TAG", "startDownload: $url")
        MyProgressDialog.showDialog(this, "Please wait...", false)
        val service = RestApiClient.getInstance(RestApiClient.Companion.SOCIAL_TYPE.INSTA).service

        val call: Call<InstaModel> = service.getMediaUrlInstagram(
            getString(R.string.rapid_api_key),
            getString(R.string.rapid_api_host_insta),
            url
        )

        call.enqueue(object : Callback<InstaModel> {
            override fun onResponse(call: Call<InstaModel>, response: Response<InstaModel>) {
                MyProgressDialog.dismissDialog()
                Log.e("TAG", "isShowing: ${MyProgressDialog.dialog?.isShowing}")
                if (response.isSuccessful) {
                    val instaModel = response.body()

                    instaModel?.let { model ->
                        if (model.getPostType() == POST_TYPE.PHOTO) {
                            BasicImageDownloader(this@InstaDownloaderHomeActivity)
                                .saveImageToExternalInsta(
                                    instaModel.media
                                ) {
                                    Toast.makeText(
                                        this@InstaDownloaderHomeActivity,
                                        "Image Downloaded.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            BasicImageDownloader(this@InstaDownloaderHomeActivity)
                                .saveVideoToExternalInsta(
                                    instaModel.media
                                ) {
                                    Toast.makeText(
                                        this@InstaDownloaderHomeActivity,
                                        "Video Downloaded.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } ?: run {
                        MyProgressDialog.dismissDialog()
                        showErrorDialog()
                    }
                } else {
                    Log.e("TAG", "onResponseError: ${response.errorBody()}")
                    MyProgressDialog.dismissDialog()
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<InstaModel>, t: Throwable) {
                MyProgressDialog.dismissDialog()
                showErrorDialog()
            }
        })
    }

    private fun showErrorDialog() {
        val dialogServerDownBinding = DialogServerDownBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.RoundedCornersDialog80).setCancelable(true)
            .setView(dialogServerDownBinding.root)

        val alertDialog = builder.create()
        alertDialog.show()

        dialogServerDownBinding.run {
            btnOk.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    fun getClipboardItemsSpecific(type: SMType): String {
        val clipboardItems = getClipBoardItems(this)

        if (clipboardItems.isNotEmpty()) {
            val fbList = mutableListOf<String>()
            val instaList = mutableListOf<String>()
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.facebook.com")) {
                    fbList.add(clipboardItems[i])
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.instagram.com")) {
                    instaList.add(clipboardItems[i])
                }
            }

            when (type) {
                SMType.INSTA -> {
                    if (instaList.isNotEmpty()) {
                        return instaList[0]
                    }
                    return ""
                }
                SMType.FACEBOOK -> {
                    if (fbList.isNotEmpty()) {
                        return fbList[0]
                    }
                    return ""
                }
                else -> {
                    return if (clipboardItems[0].contains("http"))
                        clipboardItems[0]
                    else ""
                }
            }
        }
        return ""
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}