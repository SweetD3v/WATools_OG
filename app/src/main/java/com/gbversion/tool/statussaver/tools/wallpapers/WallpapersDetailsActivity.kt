package com.gbversion.tool.statussaver.tools.wallpapers

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityWallpaperDetailsBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.downloader.BasicImageDownloader
import com.gbversion.tool.statussaver.utils.*
import java.io.File

class WallpapersDetailsActivity : BaseActivity() {
    val binding by lazy { ActivityWallpaperDetailsBinding.inflate(layoutInflater) }
    var isAllVisible: Boolean = false
    var imageUrl: String? = null
    var downloadUrl = ""
    var downloadBmp: Bitmap? = null
    val walpType by lazy {
        if (intent.hasExtra("walpType")) intent.getStringExtra("walpType")
        else ""
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra(WallpapersActivity.WALLPAPER_ORIGINAL_URL))
            downloadUrl = intent.getStringExtra(WallpapersActivity.WALLPAPER_ORIGINAL_URL)!!
        loadWallpaper()

        binding.run {

            if (NetworkState.isOnline()) {
                AdsUtils.loadBanner(
                    this@WallpapersDetailsActivity, RemoteConfigUtils.adIdBanner(),
                    binding.bannerContainer
                )
            }

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
                            downloadBmp?.let {
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        FileUtilsss.saveBitmapAPI30(
                                            this@WallpapersDetailsActivity,
                                            it,
                                            "IMG_${System.currentTimeMillis()}",
                                            "image/png",
                                            RootDirectoryWallpapers
                                        ) {
                                            Toast.makeText(
                                                this@WallpapersDetailsActivity,
                                                "Photo saved!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        FileUtilsss.saveBitmapAsFileDir(
                                            this@WallpapersDetailsActivity,
                                            downloadBmp, RootDirectoryWallpapers.name, ".png"
                                        )
                                        Toast.makeText(
                                            this@WallpapersDetailsActivity,
                                            "Photo saved!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                }
                            } ?: let {
                                imageUrl?.let { url ->
                                    BasicImageDownloader(this@WallpapersDetailsActivity)
                                        .saveImageToExternal(
                                            url,
                                            if (walpType == "wallpapers")
                                                RootDirectoryWallpapers
                                            else RootDirectoryStatus
                                        )
                                }
                            }
                        }
                    })
            }

            fabShare.setOnClickListener {
                imageUrl = downloadUrl
                downloadBmp?.let { bmp ->
                    BasicImageDownloader(this@WallpapersDetailsActivity)
                        .saveImageBmpToTemp(this@WallpapersDetailsActivity, bmp) {
                            Log.e("TAG", "onCreate: ${it}")
                            shareMediaUri(this@WallpapersDetailsActivity, arrayListOf(it))
                        }
                }
            }

            fabSetWP.setOnClickListener {
                val wallpaperManager = WallpaperManager.getInstance(applicationContext)

                downloadBmp?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val savedFile = FileUtilsss.saveBitmapAsFileDirFile(
                            this@WallpapersDetailsActivity,
                            downloadBmp, File(cacheDir, "temp_wallpapers"), ".png"
                        )

                        val uri = FileProvider.getUriForFile(
                            this@WallpapersDetailsActivity,
                            "${packageName}.provider",
                            savedFile
                        )

                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                val intent = Intent(Intent.ACTION_ATTACH_DATA)
                                intent.addCategory(Intent.CATEGORY_DEFAULT)
                                grantUriPermission(
                                    packageName,
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                                intent.setDataAndType(uri, contentResolver.getType(uri))
                                intent.putExtra("mimeType", contentResolver.getType(uri))

                                val resInfoList: List<ResolveInfo> =
                                    packageManager.queryIntentActivities(
                                        intent,
                                        PackageManager.MATCH_DEFAULT_ONLY
                                    )
                                for (resolveInfo in resInfoList) {
                                    val packageName: String =
                                        resolveInfo.activityInfo.packageName
                                    grantUriPermission(
                                        packageName,
                                        uri,
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    )
                                }

                                startActivity(Intent.createChooser(intent, "Set as:"))
                            },
                            500
                        )
                    } else {
                        val savedFile = FileUtilsss.saveBitmapAsFileDirFile(
                            this@WallpapersDetailsActivity,
                            downloadBmp, File(cacheDir, "temp_wallpapers"), ".png"
                        )

                        val uri = FileProvider.getUriForFile(
                            this@WallpapersDetailsActivity,
                            "${packageName}.provider",
                            savedFile
                        )

                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                val intent = Intent(Intent.ACTION_ATTACH_DATA)
                                intent.addCategory(Intent.CATEGORY_DEFAULT)
                                grantUriPermission(
                                    packageName,
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                                intent.setDataAndType(uri, contentResolver.getType(uri))
                                intent.putExtra("mimeType", contentResolver.getType(uri))

                                val resInfoList: List<ResolveInfo> =
                                    packageManager.queryIntentActivities(
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
                            },
                            500
                        )
                    }
                }

//                BasicImageDownloader(this@WallpapersDetailsActivity)
//                    .saveImageToTemp(
//                        downloadUrl,
//                        File(cacheDir, "temp_wallpapers"), false, { bmp ->
//                        }) { uri ->
//                        Log.e("TAG", "uriWP: ${uri}")
//
//                    }
            }
        }
    }

    private fun loadWallpaper() {
        Log.e("TAG", "loadWallpaper: ${downloadUrl}")

        Glide.with(this)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .load(downloadUrl)
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    downloadBmp = null
                    Log.e("TAG", "onLoadFailed: ${e?.message}")
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    downloadBmp = resource
                    Log.e("TAG", "downloadBmp: ${downloadBmp?.width}")
                    return false
                }
            })
            .transition(BitmapTransitionOptions.withCrossFade())
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