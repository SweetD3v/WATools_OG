package com.gbversion.tool.statussaver.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.collage_maker.ui.activities.CollageMakerHomeActivity
import com.gbversion.tool.statussaver.databinding.MainLayMainBinding
import com.gbversion.tool.statussaver.speedtest.SpeedTestActivity
import com.gbversion.tool.statussaver.tools.age_calc.AgeCalculatorActivity
import com.gbversion.tool.statussaver.tools.cleaner.CleanerActivity
import com.gbversion.tool.statussaver.tools.compress.PhotoCmpHomeActivity
import com.gbversion.tool.statussaver.tools.funny.FunnyVideosActivity
import com.gbversion.tool.statussaver.tools.insta_grid.InstaGridActivity
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.tools.photo_filters.PhotoFilterHomeActivity
import com.gbversion.tool.statussaver.tools.photo_filters.deform.PhotoWarpHomeActivity
import com.gbversion.tool.statussaver.tools.photoeditor.PicEditorHomeActivity
import com.gbversion.tool.statussaver.tools.video_downloader.FBDownloaderHomeActivity
import com.gbversion.tool.statussaver.tools.video_downloader.InstaDownloaderHomeActivity
import com.gbversion.tool.statussaver.tools.video_player.DemoUtil
import com.gbversion.tool.statussaver.tools.video_player.VideoPlayerActivity
import com.gbversion.tool.statussaver.tools.wallpapers.WallpapersActivity
import com.gbversion.tool.statussaver.ui.activities.DirectChatActivity
import com.gbversion.tool.statussaver.ui.activities.EmptySendActivity
import com.gbversion.tool.statussaver.ui.activities.HomeStatus_Activity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.DeviceUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.utils.getRealPathFromUri
import com.gbversion.tool.statussaver.whatsapp_tools.wa_web.WebviewActivity
import com.whats.stickers.WAStickersActivity
import gun0912.tedimagepicker.builder.TedImagePicker

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

        binding.run {
            llInstagram.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, InstaDownloaderHomeActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, InstaDownloaderHomeActivity::class.java))
                }
            }

            llFacebook.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, FBDownloaderHomeActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, FBDownloaderHomeActivity::class.java))
                }
            }

//            llWhatsappSide.setOnClickListener {
//                AdsUtils.clicksCountTools++
//                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
//                    AdsUtils.clicksCountTools = 0
//                    AdsUtils.loadInterstitialAd(
//                        requireActivity(),
//                        ctx.getString(R.string.interstitial_id),
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
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(
                                    Intent(ctx, WallpapersActivity::class.java)
                                        .putExtra("walpType", "wallpapers")
                                )
                            }
                        })
                } else {
                    startActivity(
                        Intent(ctx, WallpapersActivity::class.java)
                            .putExtra("walpType", "wallpapers")
                    )
                }
            }

            llStatusMaker.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(
                                    Intent(ctx, WallpapersActivity::class.java)
                                        .putExtra("walpType", "status")
                                )
                            }
                        })
                } else {
                    startActivity(
                        Intent(ctx, WallpapersActivity::class.java)
                            .putExtra("walpType", "status")
                    )
                }
            }

            llPhotoEditor.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
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
                    startActivity(
                        Intent(
                            ctx,
                            PicEditorHomeActivity::class.java
                        )
                    )
                }
            }

            llFunny.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, FunnyVideosActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, FunnyVideosActivity::class.java))
                }
            }

            llAgeCalc.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, AgeCalculatorActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, AgeCalculatorActivity::class.java))
                }
            }

            llInstaGrid.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, InstaGridActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, InstaGridActivity::class.java))
                }
            }

            llPhotoCompress.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, PhotoCmpHomeActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, PhotoCmpHomeActivity::class.java))
                }
            }

            llCleanerRef.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, CleanerActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, CleanerActivity::class.java))
                }
            }

            imgCleaner.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, CleanerActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, CleanerActivity::class.java))
                }
            }

            llCollageMaker.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                launchCollage()
                            }
                        })
                } else {
                    launchCollage()
                }
            }

//            llCartoonify.setOnClickListener {
//                AdsUtils.clicksCountTools++
//                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
//                    AdsUtils.clicksCountTools = 0
//                    AdsUtils.loadInterstitialAd(
//                        requireActivity(),
//                        ctx.getString(R.string.interstitial_id),
//                        object : AdsUtils.Companion.FullScreenCallback() {
//                            override fun continueExecution() {
//                                startActivity(Intent(ctx, CartoonifyHomeActivity::class.java))
//                            }
//                        })
//                } else {
//                    startActivity(Intent(ctx, CartoonifyHomeActivity::class.java))
//                }
//            }
//
//            llSketchify.setOnClickListener {
//                AdsUtils.clicksCountTools++
//                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
//                    AdsUtils.clicksCountTools = 0
//                    AdsUtils.loadInterstitialAd(
//                        requireActivity(),
//                        ctx.getString(R.string.interstitial_id),
//                        object : AdsUtils.Companion.FullScreenCallback() {
//                            override fun continueExecution() {
//                                startActivity(Intent(ctx, SketchifyHomeActivity::class.java))
//                            }
//                        })
//                } else {
//                    startActivity(Intent(ctx, SketchifyHomeActivity::class.java))
//                }
//            }

            llPhotoFilter.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, PhotoFilterHomeActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, PhotoFilterHomeActivity::class.java))
                }
            }

            llPhotoWarp.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                startActivity(Intent(ctx, PhotoWarpHomeActivity::class.java))
                            }
                        })
                } else {
                    startActivity(Intent(ctx, PhotoWarpHomeActivity::class.java))
                }
            }

            llDownloads.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
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
                    startActivity(
                        Intent(
                            ctx,
                            MyCreationToolsActivity::class.java
                        ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "all")
                    )
                }
            }

            rlWhatsappWeb.setOnClickListener {
                startActivity(Intent(ctx, WebviewActivity::class.java))
            }
            llWpdownload.setOnClickListener {
                startActivity(Intent(ctx, HomeStatus_Activity::class.java))
            }
            rlWhatsappSticker.setOnClickListener {
                startActivity(Intent(ctx, WAStickersActivity::class.java))
            }

            llSticker.setOnClickListener {
                startActivity(Intent(ctx, WAStickersActivity::class.java))
            }

            rlWhatsappChat.setOnClickListener {
                startActivity(Intent(ctx, DirectChatActivity::class.java))
            }
            rlEmptyMsgs.setOnClickListener {
                startActivity(Intent(ctx, EmptySendActivity::class.java))
            }
            llIntspeedtracker.setOnClickListener {
                startActivity(Intent(ctx, SpeedTestActivity::class.java))
            }

            llVideoPlayer.setOnClickListener {
                AdsUtils.clicksCountTools++
                if (NetworkState.isOnline() && AdsUtils.clicksCountTools == 2) {
                    AdsUtils.clicksCountTools = 0
                    AdsUtils.loadInterstitialAd(
                        requireActivity(),
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                TedImagePicker.with(ctx)
                                    .dropDownAlbum()
                                    .video()
                                    .showVideoDuration(true)
                                    .imageCountTextFormat("%s videos")
                                    .start { uri: Uri? ->
                                        val videoPath: String =
                                            getRealPathFromUri(ctx, uri).toString()
                                        val intent =
                                            Intent(context, VideoPlayerActivity::class.java)
                                        intent.putExtra("selectedvideo", videoPath)
                                        intent.putExtra(
                                            DemoUtil.VID_ORIENTATION,
                                            DeviceUtils.rotateScreen(context, uri)
                                        )
                                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        startActivity(intent)
                                    }
                            }
                        })
                } else {
                    TedImagePicker.with(ctx)
                        .dropDownAlbum()
                        .video()
                        .showVideoDuration(true)
                        .imageCountTextFormat("%s videos")
                        .start { uri: Uri? ->
                            val videoPath: String = getRealPathFromUri(ctx, uri).toString()
                            val intent =
                                Intent(context, VideoPlayerActivity::class.java)
                            intent.putExtra("selectedvideo", videoPath)
                            intent.putExtra(
                                DemoUtil.VID_ORIENTATION,
                                DeviceUtils.rotateScreen(context, uri)
                            )
                            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            startActivity(intent)
                        }
                }
            }
        }
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
        requireActivity().onBackPressed()
    }
}