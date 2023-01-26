package com.gbversion.tool.statussaver.tools.video_downloader

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityInstaDpDownloaderBinding
import com.gbversion.tool.statussaver.databinding.DialogConnectingServerBinding
import com.gbversion.tool.statussaver.databinding.DialogServerDownBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.downloader.BasicImageDownloader
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.utils.SMType
import com.gbversion.tool.statussaver.utils.getClipBoardItems
import org.json.JSONException
import org.json.JSONObject

class InstaDPDownloaderActivity : BaseActivity() {

    val binding by lazy { ActivityInstaDpDownloaderBinding.inflate(layoutInflater) }

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

                AdsUtils.loadNativeProgress(
                    this@InstaDPDownloaderActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame,
                    adProgress
                )
            }


            imgBack.setOnClickListener {
                onBackPressed()
            }

            imgDownloads.visibility = View.VISIBLE
            imgDownloads.setOnClickListener {
                startActivity(
                    Intent(
                        this@InstaDPDownloaderActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "insta_dp")
                    }
                )
            }

            btnPaste.setOnClickListener {
                etText.setText(getClipboardItemsSpecific(SMType.INSTA))
            }

            btnDownload.setOnClickListener {
                if (etText.text.isNotEmpty()) {
                    if (NetworkState.isOnline()) {
                        downloadDP()
                    } else Toast.makeText(
                        this@InstaDPDownloaderActivity,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@InstaDPDownloaderActivity,
                        "Please enter link",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            imgClear.setOnClickListener { etText.text.clear() }
        }
    }

    private fun downloadDP() {
        val profileName = binding.etText.text.trim().toString()

        val dpURL = "https://www.instagram.com/${profileName}/?__a=1&__d=dis"

        val queue = Volley.newRequestQueue(this)

        showConnectingDialog()
        handler.post(runnable)

        val request: StringRequest = object :
            StringRequest(Method.GET, dpURL, com.android.volley.Response.Listener { response ->
                var downloadUrl = ""

                hideConnectingDialog()
                handler.removeCallbacks(runnable)
                count = 0

                try {
                    val obj = JSONObject(response)
                    val graphql = obj.getJSONObject("graphql")
                    val user = graphql.getJSONObject("user")
                    val profile_pic_url_hd = user.getString("profile_pic_url_hd")
                    downloadUrl = profile_pic_url_hd

                    BasicImageDownloader(this@InstaDPDownloaderActivity)
                        .saveImageToExternalInstaDP(
                            downloadUrl
                        ) {
                            Toast.makeText(
                                this@InstaDPDownloaderActivity,
                                "Profile Pic Saved!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } catch (e: JSONException) {
                    Log.e("TAG", "dpExc: ${e.message}")
                    e.printStackTrace()
                    hideConnectingDialog()
                    handler.removeCallbacks(runnable)
                    count = 0
                    showErrorDialog()
                }

            }, com.android.volley.Response.ErrorListener { error ->
                Log.e("TAG", "dpError: ${error.message}")
                hideConnectingDialog()
                handler.removeCallbacks(runnable)
                count = 0
                showErrorDialog()
            }) {}

        queue.add(request)
    }

    var alertDialogError: AlertDialog? = null

    private fun showErrorDialog() {

        val dialogServerDownBinding = DialogServerDownBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.RoundedCornersDialog).setCancelable(true)
            .setView(dialogServerDownBinding.root)

        if (alertDialogError == null)
            alertDialogError = builder.create()

        if (alertDialogError?.isShowing != true)
            alertDialogError?.show()


        dialogServerDownBinding.run {
            txtError.text = getString(R.string.server_error)

            btnOk.setOnClickListener {
                alertDialogError?.dismiss()
            }
        }
    }

    fun getClipboardItemsSpecific(type: SMType): String {
        val clipboardItems = getClipBoardItems(this)

        if (clipboardItems.isNotEmpty()) {
            val fbList = mutableListOf<String>()
            val instaList = mutableListOf<String>()
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("facebook")) {
                    fbList.add(clipboardItems[i])
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("instagram")) {
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