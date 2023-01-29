package com.gbversion.tool.statussaver.tools.cleaner

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityCacheCleanerBinding
import com.gbversion.tool.statussaver.phone_booster.models.AppModel
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.utils.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class CleanerActivity : BaseActivity() {
    val binding by lazy { ActivityCacheCleanerBinding.inflate(layoutInflater) }

    var handler: Handler? = Handler(Looper.getMainLooper())
    var handlerReverse: Handler? = Handler(Looper.getMainLooper())
    var cachePercentageReverse: Int = 100
    var cachePercentage: Int = 0
    var showedAd: Boolean = false
    var junkAppsList: MutableList<AppModel> = mutableListOf()

    var interstitialAd: InterstitialAd? = null

    fun loadInterstitialAd(
        activity: Activity,
        adId: String
    ) {
        if (!NetworkState.isOnline()) {
            return
        }

        InterstitialAd.load(
            activity,
            adId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.e("TAG", "onAdLoaded: ${interstitialAd}")
                }

                override fun onAdFailedToLoad(ad: LoadAdError) {
                    Log.e("TAG", "adException: ${ad.responseInfo}")
                }
            })
    }

    fun showFullScreenAd() {
        Log.e("TAG", "showFullScreenAd: ${interstitialAd}")
        interstitialAd?.let {
            it.fullScreenContentCallback = object :
                FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    showedAd = true
                }

                override fun onAdDismissedFullScreenContent() {
                    showedAd = true
                    finish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    showedAd = false
                    finish()
                }
            }
            it.show(this)
        } ?: let {
            if (NetworkState.isOnline()) {
                loadInterstitialAd(this, RemoteConfigUtils.adIdInterstital())
                val pd = AdsUtils.ProgressDialogMine()
                pd.showDialog(this, "Please wait...", false)
                Handler(Looper.getMainLooper()).postDelayed({
                    pd.dismissDialog()
                    showFullScreenAd()
                }, 3000)
            } else finish()
        }
    }

    val runnableReverse = object : Runnable {
        override fun run() {
            cachePercentageReverse -= 2
            binding.txtTotalPercentage.text = "$cachePercentageReverse %"
            binding.btnCleanJunk.text = "Cleaning..."
            handler?.postDelayed(this, 50)

            if (cachePercentageReverse == 0) {
                handlerReverse?.removeCallbacks(this)
                handlerReverse = null

                binding.run {
                    txtTotalCacheSize.animate().scaleX(0f).scaleY(0f).apply {
                        duration = 500
                        setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                txtTotalCacheSize.text = "Cache cleaned"
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationRepeat(p0: Animator?) {
                            }
                        })
                    }
                    txtTotalPercentage.animate().scaleX(0f).scaleY(0f).apply {
                        duration = 500
                        setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                txtTotalPercentage.text = "0 B"
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationRepeat(p0: Animator?) {
                            }
                        })
                    }

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            txtTotalPercentage.animate().scaleX(1f).scaleY(1f).apply {
                                duration = 500
                            }
                            txtTotalCacheSize.animate().scaleX(1f).scaleY(1f).apply {
                                duration = 500
                            }
                            btnCleanJunk.text = "Done"
                            btnCleanJunk.isEnabled = true
                        }, 500)
                }
            }

            handlerReverse?.postDelayed(this, 50)
        }
    }

    val runnable = object : Runnable {
        override fun run() {
            cachePercentage += 1
            binding.txtTotalPercentage.text = "$cachePercentage %"
            binding.btnCleanJunk.text = "Optimizing..."
            handler?.postDelayed(this, 50)

            if (cachePercentage == 100) {
                handler?.removeCallbacks(this)
                handler = null
                cachePercentage = 0

                binding.run {
                    object : AsyncTaskRunner<Void?, String>(this@CleanerActivity) {
                        override fun doInBackground(params: Void?): String {
                            return cacheDir.getProperSize(true).formatSize()
                        }

                        override fun onPostExecute(result: String?) {
                            super.onPostExecute(result)
                            result?.let { size ->

                                txtTotalPercentage.animate().scaleX(0f).scaleY(0f).apply {
                                    duration = 500
                                    setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(p0: Animator?) {
                                        }

                                        override fun onAnimationEnd(p0: Animator?) {
                                            txtTotalPercentage.text = size
                                        }

                                        override fun onAnimationCancel(p0: Animator?) {
                                        }

                                        override fun onAnimationRepeat(p0: Animator?) {
                                        }
                                    })
                                }
                                txtTotalCacheSize.animate().scaleX(0f).scaleY(0f).apply {
                                    duration = 500
                                    setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(p0: Animator?) {
                                        }

                                        override fun onAnimationEnd(p0: Animator?) {
                                            txtTotalCacheSize.text = "Cache size"
                                            btnCleanJunk.text = "Clean"
                                            btnCleanJunk.isEnabled = true
                                        }

                                        override fun onAnimationCancel(p0: Animator?) {
                                        }

                                        override fun onAnimationRepeat(p0: Animator?) {
                                        }
                                    })
                                }

                                Handler(Looper.getMainLooper())
                                    .postDelayed({
                                        txtTotalPercentage.animate().scaleX(1f).scaleY(1f).apply {
                                            duration = 500
                                        }
                                        txtTotalCacheSize.animate().scaleX(1f).scaleY(1f).apply {
                                            duration = 500
                                        }

                                    }, 500)
                            }
                        }
                    }.execute(null, false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        handler?.post(runnable)

        loadInterstitialAd(this, RemoteConfigUtils.adIdInterstital())

        binding.run {

            if (NetworkState.isOnline()) {
                AdsUtils.loadNativeSmall(
                    this@CleanerActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
//                AdsUtils.loadBanner(
//                    this@CleanerActivity, getString(R.string.interstitial_id),
//                    bannerContainer
//                )
            }

            appTitle.text = getString(R.string.cleaner)
//            toolbar.root.background = ContextCompat.getDrawable(
//                this@CleanerActivity,
//                R.drawable.top_bar_gradient_green
//            )

            imgBack.setOnClickListener {
                onBackPressed()
            }

            btnCleanJunk.setOnClickListener {
                if (btnCleanJunk.isEnabled) {
                    if (btnCleanJunk.text.equals("Done")) {
                        showFullScreenAd()
                    } else {
                        object : AsyncTaskRunner<Void?, String>(this@CleanerActivity) {
                            override fun doInBackground(params: Void?): String {
                                cacheDir.deleteRecursively()
                                return "Cleaning..."
                            }

                            override fun onPostExecute(result: String?) {
                                super.onPostExecute(result)
                                result?.let { size ->
                                    handlerReverse?.post(runnableReverse)
                                    binding.txtTotalCacheSize.text = size
                                    btnCleanJunk.isEnabled = false
                                }
                            }
                        }.execute(null, false)
                    }
                }
            }
        }
    }

    interface AppItemClickListener {
        fun onAppClicked(appModel: AppModel, position: Int)
    }

    override fun onPause() {
        handler?.removeCallbacks(runnable)
        handler = null
        super.onPause()
    }

    override fun onBackPressed() {
        if (!showedAd) {
            showFullScreenAd()
        } else finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}