package com.gbversion.tool.statussaver.tools.wallpapers

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityWallpaperDetailsBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.downloader.BasicImageDownloader
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.utils.originalPath
import com.gbversion.tool.statussaver.utils.shareMediaUri
import java.io.File

class WallpapersDetailsActivity : BaseActivity() {
    val binding by lazy { ActivityWallpaperDetailsBinding.inflate(layoutInflater) }
    var isAllVisible: Boolean = false
    var imageUrl: String? = null
    var downloadUrl = ""
    val walpType by lazy { intent.getStringExtra("walpType") }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra(WallpapersActivity.WALLPAPER_ORIGINAL_URL))
            downloadUrl = intent.getStringExtra(WallpapersActivity.WALLPAPER_ORIGINAL_URL)!!
        loadWallpaper()

        binding.run {

            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@WallpapersDetailsActivity, RemoteConfigUtils.adIdBanner(),
                    binding.bannerContainer
                )

            appTitle.text = ""
            setSupportActionBar(binding.toolbar)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            fabDownload.hide()
            fabShare.hide()
            fabSetWP.hide()
            txtDownload.visibility = View.GONE
            txtShare.visibility = View.GONE
            txtSetWp.visibility = View.GONE

            fabMore.setOnClickListener {
                isAllVisible = if (!isAllVisible) {
                    fabDownload.show()
                    fabShare.show()
                    fabSetWP.show()
                    txtDownload.visibility = View.VISIBLE
                    txtShare.visibility = View.VISIBLE
                    txtSetWp.visibility = View.VISIBLE

                    true
                } else {
                    fabDownload.hide()
                    fabShare.hide()
                    fabSetWP.hide()
                    txtDownload.visibility = View.GONE
                    txtShare.visibility = View.GONE
                    txtSetWp.visibility = View.GONE

                    false
                }
            }

            fabDownload.setOnClickListener {
                AdsUtils.loadInterstitialAd(
                    this@WallpapersDetailsActivity,
                    RemoteConfigUtils.adIdInterstital(),
                    object : AdsUtils.Companion.FullScreenCallback() {
                        override fun continueExecution() {
                            imageUrl = downloadUrl
                            imageUrl?.let { url ->
                                BasicImageDownloader(this@WallpapersDetailsActivity)
                                    .saveImageToExternal(
                                        url,
                                        File(
                                            originalPath,
                                            if (walpType == "wallpapers") {
                                                "Wallpapers"
                                            } else {
                                                "Status"
                                            }
                                        )
                                    )
                            }
                        }
                    })
            }

            fabShare.setOnClickListener {
                imageUrl = downloadUrl
                imageUrl?.let { url ->
                    val file = getExternalFilesDir("Wallpapers")
                    if (file?.exists() == false)
                        file.mkdirs()
                    BasicImageDownloader(this@WallpapersDetailsActivity)
                        .saveImageToTemp(url, file!!, false, {
                        }) {
                            Log.e("TAG", "onCreate: ${it}")
                            shareMediaUri(this@WallpapersDetailsActivity, arrayListOf(it))
                        }
                }
            }

            fabSetWP.setOnClickListener {
                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                BasicImageDownloader(this@WallpapersDetailsActivity)
                    .saveImageToTemp(
                        downloadUrl,
                        File(cacheDir, "temp_wallpapers"), false, { bmp ->
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                wallpaperManager.setBitmap(
//                                    bmp,
//                                    null,
//                                    true,
//                                    WallpaperManager.FLAG_SYSTEM
//                                )
//                            } else {
//                                wallpaperManager.setBitmap(bmp)
//                            }
                        }) { uri ->
                        Log.e("TAG", "uriWP: ${uri}")
                        val intent = Intent(Intent.ACTION_ATTACH_DATA)
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.setDataAndType(uri, contentResolver.getType(uri))
                        intent.putExtra("mimeType", contentResolver.getType(uri))

                        val resInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(
                            intent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        )
                        for (resolveInfo in resInfoList) {
                            val packageName: String = resolveInfo.activityInfo.packageName
                            grantUriPermission(
                                packageName,
                                uri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        }

                        startActivity(Intent.createChooser(intent, "Set as:"))
                    }
            }
        }
    }

    private fun loadWallpaper() {
        Log.e("TAG", "loadWallpaper: ${downloadUrl}")

        Glide.with(this).load(downloadUrl)
            .placeholder(R.drawable.ic_wallpapers)
            .into(binding.imgWallpaper)
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }

    override fun onBackPressed() {
        AdsUtils.clicksCountWallp++
        if (AdsUtils.clicksCountWallp == 4) {
            AdsUtils.clicksCountWallp = 0

            AdsUtils.loadInterstitialAd(
                this,
                RemoteConfigUtils.adIdInterstital(),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        finish()
                    }
                })
            return
        }
        finish()
    }
}