package com.gbversion.tool.statussaver.tools.photo_filters

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhotoFilterBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.cartoonify.CartoonActivity
import com.gbversion.tool.statussaver.utils.*
import com.zomato.photofilters.SampleFilters

class PhotoFilterActivity : AppCompatActivity() {
    init {
        System.loadLibrary("NativeImageProcessor")
    }

    val binding by lazy { ActivityPhotoFilterBinding.inflate(layoutInflater) }
    val photoUri by lazy {
        intent.getStringExtra(CartoonActivity.SELECTED_PHOTO).toString().toUri()
    }
    var orgBitmap: Bitmap? = null
    var filterBmp: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setDarkStatusBar(this)

        if (NetworkState.isOnline())
            AdsUtils.loadBanner(
                this, RemoteConfigUtils.adIdBanner(),
                binding.bannerContainer
            )

        val bmp = getBitmapFromUri(this, photoUri)
        orgBitmap = bmp?.copy(bmp.config, true)
        filterBmp = orgBitmap

        binding.imgPhoto.setImageBitmap(orgBitmap)

        binding.rvFiltersList.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.HORIZONTAL
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            filterBmp?.let { filterBmp ->
                object : AsyncTaskRunner<Bitmap, String?>(this@PhotoFilterActivity) {
                    var pathStr: String = ""
                    override fun doInBackground(params: Bitmap?): String {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            saveBitmapImage(
                                this@PhotoFilterActivity,
                                params,
                                "IMG_FILTER_${System.currentTimeMillis()}",
                                "PhotoFilter"
                            ) {
                                pathStr = it
                            }
                        } else {
                            FileUtilsss.saveBitmapAsFileA10(
                                this@PhotoFilterActivity,
                                params,
                                "IMG_FILTER_${System.currentTimeMillis()}",
                                "PhotoFilter"
                            ) {
                                pathStr = it
                            }
                        }
                        return pathStr
                    }

                    override fun onPostExecute(result: String?) {
                        super.onPostExecute(result)
                        result?.let { path ->
                            MediaScannerConnection.scanFile(
                                this@PhotoFilterActivity, arrayOf(
                                    path
                                ), null
                            ) { path1: String?, uri1: Uri? -> }
                            Toast.makeText(
                                this@PhotoFilterActivity,
                                "Saved to: $path",
                                Toast.LENGTH_SHORT
                            ).show()

                            AdsUtils.loadInterstitialAd(this@PhotoFilterActivity,
                                RemoteConfigUtils.adIdInterstital(),
                                object : AdsUtils.Companion.FullScreenCallback() {
                                    override fun continueExecution() {
                                        PhotoFiltersUtils.photoFilterBmp = filterBmp
                                        startActivity(
                                            Intent(
                                                this@PhotoFilterActivity,
                                                PhotoFiltersSaveActivity::class.java
                                            )
                                                .putExtra("type", "filter")
                                        )
                                    }
                                })
                        }
                    }
                }.execute(filterBmp, true)
            }
        }

        loadAllFilters()
    }

    private fun loadAllFilters() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            val f1 = ThumbnailItem()
            val f2 = ThumbnailItem()
            val f3 = ThumbnailItem()
            val f4 = ThumbnailItem()
            val f5 = ThumbnailItem()
            val f6 = ThumbnailItem()

            f1.image = orgBitmap
            f2.image = orgBitmap
            f3.image = orgBitmap
            f4.image = orgBitmap
            f5.image = orgBitmap
            f6.image = orgBitmap
            ThumbnailsManager.clearThumbs()
            ThumbnailsManager.addThumb(f1) // Original Image


            f2.filter = SampleFilters.getStarLitFilter()
            ThumbnailsManager.addThumb(f2)

            f3.filter = SampleFilters.getBlueMessFilter()
            ThumbnailsManager.addThumb(f3)

            f4.filter = SampleFilters.getAweStruckVibeFilter()
            ThumbnailsManager.addThumb(f4)

            f5.filter = SampleFilters.getLimeStutterFilter()
            ThumbnailsManager.addThumb(f5)

            f6.filter = SampleFilters.getNightWhisperFilter()
            ThumbnailsManager.addThumb(f6)

            val thumbs: MutableList<ThumbnailItem> = ThumbnailsManager.processThumbs(this)
            val thumbnailsAdapter = ThumbnailsAdapter(thumbs) { filter ->
                val bmp = filter.processFilter(orgBitmap)
                binding.imgPhoto.setImageBitmap(bmp)
                filterBmp = bmp
            }

            binding.rvFiltersList.adapter = thumbnailsAdapter
        }

        handler.post(runnable)
    }

    override fun onDestroy() {
        PhotoFiltersUtils.photoFilterBmp = null
        AdsUtils.destroyBanner()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }
}