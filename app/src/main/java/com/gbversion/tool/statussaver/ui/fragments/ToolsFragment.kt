package com.gbversion.tool.statussaver.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.collage_maker.ui.activities.CollageMakerHomeActivity
import com.gbversion.tool.statussaver.databinding.ItemPopularVideosBinding
import com.gbversion.tool.statussaver.databinding.MainLayMainBinding
import com.gbversion.tool.statussaver.models.PopularVids
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.speedtest.SpeedTestActivity
import com.gbversion.tool.statussaver.tools.age_calc.AgeCalculatorActivity
import com.gbversion.tool.statussaver.tools.cleaner.CleanerHomeActivity
import com.gbversion.tool.statussaver.tools.funny.FunnyVideosActivity
import com.gbversion.tool.statussaver.tools.insta_grid.InstaGridActivity
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.tools.photoeditor.PicEditorHomeActivity
import com.gbversion.tool.statussaver.tools.video_downloader.FBDownloaderHomeActivity
import com.gbversion.tool.statussaver.tools.video_downloader.InstaDPDownloaderActivity
import com.gbversion.tool.statussaver.tools.video_downloader.InstaDownloaderHomeActivity
import com.gbversion.tool.statussaver.tools.wallpapers.WallpapersActivity
import com.gbversion.tool.statussaver.ui.activities.DirectChatActivity
import com.gbversion.tool.statussaver.ui.activities.EmptySendActivity
import com.gbversion.tool.statussaver.ui.activities.HomeStatus_Activity
import com.gbversion.tool.statussaver.ui.activities.PrivacyPolicyActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.MyProgressDialog
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.wa_stickers.stickers.WAStickersActivity
import com.gbversion.tool.statussaver.whatsapp_tools.wa_web.WebviewActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class ToolsFragment : BaseFragment<MainLayMainBinding>() {
    override fun getLayout(): MainLayMainBinding {
        return MainLayMainBinding.inflate(layoutInflater)
    }

    companion object {
        open fun newInstance(): ToolsFragment {
            return ToolsFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (NetworkState.isOnline()) {
            AdsUtils.loadNativeSmall(ctx, RemoteConfigUtils.adIdNative(), binding.adFrame)
        }

        val colorScheme = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(
                ContextCompat.getColor(
                    ctx,
                    R.color.white
                )
            )
            .build()
        val customtabs = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorScheme)
            .build()

        binding.run {

//            llGame1.setOnClickListener {
//                AdsUtils.loadInterstitialAd(
//                    requireActivity(),
//                    RemoteConfigUtils.adIdInterstital(),
//                    object : AdsUtils.Companion.FullScreenCallback() {
//                        override fun continueExecution() {
//                            customtabs.launchUrl(ctx, getString(R.string.link_game1).toUri())
//                        }
//                    })
//            }
//            llGame2.setOnClickListener {
//                AdsUtils.loadInterstitialAd(
//                    requireActivity(),
//                    RemoteConfigUtils.adIdInterstital(),
//                    object : AdsUtils.Companion.FullScreenCallback() {
//                        override fun continueExecution() {
//                            customtabs.launchUrl(ctx, getString(R.string.link_game2).toUri())
//                        }
//                    })
//            }
//            llGame3.setOnClickListener {
//                AdsUtils.loadInterstitialAd(
//                    requireActivity(),
//                    RemoteConfigUtils.adIdInterstital(),
//                    object : AdsUtils.Companion.FullScreenCallback() {
//                        override fun continueExecution() {
//                            customtabs.launchUrl(ctx, getString(R.string.link_game3).toUri())
//                        }
//                    })
//            }

            llInstagram.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, InstaDownloaderHomeActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, InstaDownloaderHomeActivity::class.java))
                }
            }

            llFacebook.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, FBDownloaderHomeActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, FBDownloaderHomeActivity::class.java))
                }
            }

//            llWhatsappSide.setOnClickListener {
//                AdsUtils.clicksCountTools++
//                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
//                    AdsUtils.clicksCountTools = 0
//                    AdsUtils.loadInterstitialAd(
//                        requireActivity(),
//                        RemoteConfigUtils.adIdInterstital(),
//                        object : AdsUtils.Companion.FullScreenCallback() {
//                            override fun continueExecution() {
//                                startActivity(Intent(ctx, WAStatusActivity::class.java))
//                            }
//                        })
//                } else {
//                    startActivity(Intent(ctx, WAStatusActivity::class.java))
//                }
//            }

            llWallpaper.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(
                                    Intent(ctx, WallpapersActivity::class.java)
                                        .putExtra("walpType", "wallpapers")
                                )
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(
                        Intent(ctx, WallpapersActivity::class.java)
                            .putExtra("walpType", "wallpapers")
                    )
                }
            }

            llInstaDP.setOnClickListener {
                startActivity(
                    Intent(ctx, InstaDPDownloaderActivity::class.java)
                )
            }

            llPhotoEditor.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(
                                    Intent(
                                        ctx,
                                        PicEditorHomeActivity::class.java
                                    )
                                )
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(
                        Intent(
                            ctx,
                            PicEditorHomeActivity::class.java
                        )
                    )
                }
            }

            llFunny.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, FunnyVideosActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, FunnyVideosActivity::class.java))
                }
            }

            llAgeCalc.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, AgeCalculatorActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, AgeCalculatorActivity::class.java))
                }
            }

            llInstaGrid.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, InstaGridActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, InstaGridActivity::class.java))
                }
            }

            llCleanerRef.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, CleanerHomeActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, CleanerHomeActivity::class.java))
                }
            }

            imgCleaner.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, CleanerHomeActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, CleanerHomeActivity::class.java))
                }
            }

            llCollageMaker.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                launchCollage()
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    launchCollage()
                }
            }

            llMyCreation.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(
                                    Intent(
                                        ctx,
                                        MyCreationToolsActivity::class.java
                                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "all")
                                )
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(
                        Intent(
                            ctx,
                            MyCreationToolsActivity::class.java
                        ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "all")
                    )
                }
            }

            llDownloads.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(
                                    Intent(
                                        ctx,
                                        MyCreationToolsActivity::class.java
                                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "all")
                                )
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(
                        Intent(
                            ctx,
                            MyCreationToolsActivity::class.java
                        ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "all")
                    )
                }
            }

            rlWhatsappWeb.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, WebviewActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, WebviewActivity::class.java))
                }
            }
            llWpdownload.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, HomeStatus_Activity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, HomeStatus_Activity::class.java))
                }
            }
            rlWhatsappSticker.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    loadInterstitialAdWA(requireActivity(), RemoteConfigUtils.adIdInterstital())
                } else {
                    AdsUtils.clicksAlternate = true
                    adClosedListener?.onAdClosed()
                }
            }

            rlWhatsappChat.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, DirectChatActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, DirectChatActivity::class.java))
                }
            }
            rlEmptyMsgs.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, EmptySendActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, EmptySendActivity::class.java))
                }
            }

            llSpeedTest.setOnClickListener {
                if (NetworkState.isOnline() && AdsUtils.clicksAlternate) {
                    AdsUtils.clicksAlternate = false
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, SpeedTestActivity::class.java))
                            }
                        })
                } else {
                    AdsUtils.clicksAlternate = true
                    startActivity(Intent(ctx, SpeedTestActivity::class.java))
                }
            }

            imgDrawer.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

            navDrawer.llRate.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                openPlayStore()
            }

            navDrawer.llShare.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                openShareIntent()
            }

            navDrawer.llPrivacyPolicy.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(ctx, PrivacyPolicyActivity::class.java))
            }

        }
    }

    fun continueToWA() {
        startActivity(Intent(ctx, WAStickersActivity::class.java))
        adClosedListener = WAStickersActivity()
    }

    var interstitialAdWA: InterstitialAd? = null
    var adClosedListener: AdClosedListener? = null

    fun loadInterstitialAdWA(
        activity: Activity,
        adId: String
    ) {
        if (!NetworkState.isOnline()) {
            continueToWA()
            return
        }
        var handler: Handler? = Handler(Looper.getMainLooper())
        var runnable: Runnable? = Runnable {
            MyProgressDialog.dismissDialog()
            continueToWA()
            interstitialAdWA?.show(activity)
        }
        try {
            MyProgressDialog.showDialog(activity, "Please wait...", false)
        } catch (e: Exception) {
            MyProgressDialog.dismissDialog()
        }
        runnable?.let { handler?.postDelayed(it, 5000) }

        InterstitialAd.load(
            activity,
            adId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAdWA = ad
                    interstitialAdWA?.fullScreenContentCallback = object :
                        FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                        }

                        override fun onAdDismissedFullScreenContent() {
                            runnable?.let { handler?.removeCallbacks(it) }
                            handler = null
                            runnable = null
                            adClosedListener?.onAdClosed()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            runnable?.let { handler?.removeCallbacks(it) }
                            handler = null
                            runnable = null
                            adClosedListener?.onAdClosed()
                        }
                    }

                    MyProgressDialog.dismissDialog()
                    continueToWA()
                    interstitialAdWA?.show(activity)
                    runnable?.let { handler?.removeCallbacks(it) }
                    handler = null
                    runnable = null
                }

                override fun onAdFailedToLoad(ad: LoadAdError) {
                    runnable?.let { handler?.removeCallbacks(it) }
                    handler = null
                    runnable = null
                    MyProgressDialog.dismissDialog()
                    adClosedListener?.onAdClosed()
                }
            })
    }

    interface AdClosedListener {
        fun onAdClosed()
    }

    private fun initMyPopularVideos() {
        binding.run {
            rvPopularVideos.layoutManager = LinearLayoutManager(ctx).apply {
                orientation = RecyclerView.HORIZONTAL
            }
            val popularAdapter = PopularVideoAdapter(ctx)
            rvPopularVideos.adapter = popularAdapter
            rvPopularVideos.isNestedScrollingEnabled = false
            popularAdapter.popularItemClickListener =
                object : PopularVideoAdapter.PopularItemClickListener {
                    override fun onItemClick(url: String) {
                        openUrl(url)
                    }
                }
            val titleArr = ctx.resources.getStringArray(R.array.myfun_titles_array)
            val thumbArr = ctx.resources.getStringArray(R.array.status_arr)
            val videoArr = ctx.resources.getStringArray(R.array.fun_videos)
            val popularList = mutableListOf<PopularVids>()
            for (i in videoArr.indices) {
                popularList.add(PopularVids(titleArr[i], videoArr[i], videoArr[i]))
            }
            popularList.shuffle()
            popularAdapter.popularList = popularList

            popularAdapter.notifyDataSetChanged()
        }
    }

    fun openUrl(url: String) {
        AdsUtils.loadInterstitialAd(requireActivity(),
            RemoteConfigUtils.adIdInterstital(),
            object : AdsUtils.Companion.FullScreenCallback() {
                override fun continueExecution() {
                    startActivity(
                        Intent(ctx, FunnyVideosActivity::class.java)
                            .putExtra("customUrl", url)
                    )
                }
            })
    }

    class PopularVideoAdapter(var ctx: Context) :
        RecyclerView.Adapter<PopularVideoAdapter.VH>() {

        var popularList = mutableListOf<PopularVids>()
        var popularItemClickListener: PopularItemClickListener? = null

        inner class VH(var binding: ItemPopularVideosBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemPopularVideosBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.binding.run {
                val popularVid = popularList[holder.adapterPosition]

                Glide.with(ctx).load(popularVid.thumbUrl)
                    .centerCrop()
                    .into(imgMyFun)

                txtFunny.text = popularVid.title

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

    private fun openShareIntent() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text, ctx.packageName))
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)))
    }

    private fun openPlayStore() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    getString(
                        R.string.playstore_url,
                        ctx.packageName
                    )
                )
            )
        )
    }

    private fun launchCollage() {
        startActivity(
            Intent(
                context,
                CollageMakerHomeActivity::class.java
            ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "collage_maker")
        )
    }

    override fun onBackPressed() {

    }
}