package com.gbversion.tool.statussaver.tools.funny

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityFunnyVideosBinding
import com.gbversion.tool.statussaver.databinding.ItemAttitudeStatusBinding
import com.gbversion.tool.statussaver.models.PopularVids
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.downloader.BasicImageDownloader
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.utils.RootDirectoryFacts
import com.gbversion.tool.statussaver.utils.setDarkStatusBarColor

class FunnyVideosActivity : AppCompatActivity() {
    val binding by lazy { ActivityFunnyVideosBinding.inflate(layoutInflater) }

    var downloadUrl: String? = null
    val customUrl by lazy { if (intent.hasExtra("customUrl")) intent.getStringExtra("customUrl") else "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusBarColor(this, R.color.black)
        setContentView(binding.root)

        binding.run {
            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@FunnyVideosActivity, RemoteConfigUtils.adIdBanner(),
                    bannerContainer
                )

            initMyPopularVideos()

            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

//    private fun loadVideos() {
//        binding.run {
//            webView.isSaveEnabled = true
//            webView.setNetworkAvailable(true)
//            webView.settings.apply {
//                javaScriptEnabled = true
//                loadWithOverviewMode = true
//                useWideViewPort = true
//                builtInZoomControls = false
//                loadsImagesAutomatically = true
//                domStorageEnabled = true
//
//                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
//                domStorageEnabled = true
//                mediaPlaybackRequiresUserGesture = false
//                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//
//                setSupportZoom(true)
//                builtInZoomControls = true
//                displayZoomControls = false
//
//                webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
//            }
//            webView.webViewClient = WVClient()
//            webView.webChromeClient = ChromeClient()
//            webView.loadUrl("https://myvideo.fun")
//
//            fabDownload.setOnClickListener {
//                startDownload(downloadUrl)
//            }
//        }
//    }

    private fun initMyPopularVideos() {
        binding.run {
            viewPagerVid.orientation = ViewPager2.ORIENTATION_VERTICAL
            val popularAdapter = PopularVideoAdapter(this@FunnyVideosActivity)
            val videoArr = this@FunnyVideosActivity.resources.getStringArray(R.array.fun_videos)
            var popularList = mutableListOf<PopularVids>()
            for (i in videoArr.indices) {
                popularList.add(PopularVids("Title${i}", videoArr[i], videoArr[i]))
            }
            popularList.shuffle()
            if (customUrl != "")
                popularList.add(
                    0,
                    PopularVids(
                        "Title0",
                        customUrl.toString(),
                        customUrl.toString()
                    )
                )
            popularList = popularList.distinctBy { it.videoUrl }.toMutableList()
//            popularList.add(0, PopularVids(titleArr[i], thumbArr[i], videoArr[i]))
            popularAdapter.popularList = popularList
            viewPagerVid.adapter = popularAdapter

            viewPagerVid.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    Log.e("TAG", "onPageSelected: $position")
                }
            })
        }
    }

    class PopularVideoAdapter(var ctx: Context) :
        RecyclerView.Adapter<PopularVideoAdapter.VH>() {

        var popularList = mutableListOf<PopularVids>()
        var popularItemClickListener: PopularItemClickListener? = null

        inner class VH(var binding: ItemAttitudeStatusBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemAttitudeStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.binding.run {
                val popularVid = popularList[holder.bindingAdapterPosition]

                Glide.with(ctx).load(popularVid.videoUrl)
                    .into(imgWallpaper)

                fabDownload.setOnClickListener {
                    AdsUtils.loadInterstitialAd(
                        ctx as Activity,
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                popularVid.videoUrl.let { url ->
                                    BasicImageDownloader(ctx)
                                        .saveImageToExternal(
                                            url,
                                            RootDirectoryFacts
                                        )
                                }
                            }
                        })
                }

//                videoView.setVideoURI(Uri.parse(popularVid.videoUrl))
                imgPlay.setOnClickListener {
                    videoView.setOnPreparedListener {
                        imgPlay.visibility = View.INVISIBLE

                        // Restore saved position, if available.
                        if (videoView.currentPosition > 0) {
                            videoView.seekTo(videoView.currentPosition)
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            videoView.seekTo(1)
                        }

                        // Start playing!
                        videoView.start()
                    }
                }

                root.setOnClickListener {
                    popularItemClickListener?.onItemClick(popularVid.videoUrl)
                }
            }
        }

        override fun getItemCount(): Int {
            return popularList.size
        }

        interface PopularItemClickListener {
            fun onItemClick(url: String)
        }
    }

    private fun startDownload(downloadUrl: String?) {
        downloadUrl?.let { url ->
            Log.e("TAG", "startDownload: $url")
            BasicImageDownloader(this@FunnyVideosActivity)
                .saveVideoToExternalFunny(
                    url
                ) {
                    Toast.makeText(
                        this@FunnyVideosActivity,
                        "Image Downloaded.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

//    inner class ChromeClient : WebChromeClient() {
//        override fun onProgressChanged(view: WebView?, newProgress: Int) {
//            super.onProgressChanged(view, newProgress)
//
//            if (view?.url.toString() != "https://myvideo.fun/") {
//                downloadUrl = view?.url.toString()
//            }
//            Log.e("TAG", "onProgressChanged: ${view?.url}")
//        }
//    }
//
//    inner class WVClient : WebViewClient() {
//        override fun shouldOverrideUrlLoading(
//            view: WebView?,
//            request: WebResourceRequest?
//        ): Boolean {
//            Log.e("TAG", "shouldOverrideUrlLoading: ${request?.url}")
//            return true
//        }
//
//        override fun onReceivedSslError(
//            view: WebView?,
//            handler: SslErrorHandler?,
//            error: SslError?
//        ) {
//            super.onReceivedSslError(view, handler, error)
//            Log.e("TAG", "onReceivedSslError: ${error.toString()}")
//        }
//
//        override fun onReceivedError(
//            view: WebView?,
//            request: WebResourceRequest?,
//            error: WebResourceError?
//        ) {
//            super.onReceivedError(view, request, error)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Log.e("TAG", "onReceivedError: ${error?.description}")
//            }
//        }
//    }

    override fun onBackPressed() {
        if (intent.hasExtra("customUrl")) {
            AdsUtils.loadInterstitialAd(this@FunnyVideosActivity,
                RemoteConfigUtils.adIdInterstital(),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        finish()
                    }
                })
        } else finish()
    }
}