package com.gbversion.tool.statussaver.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivitySplashBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

class SplashScreenActivity : BaseActivity(), LifecycleObserver {
    val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val SPLASH_TIME_OUT: Long = 3000
    var isShowingAd = false
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var loadTime = 0L

    var handler: Handler? = Handler(Looper.getMainLooper())
    var runnable = Runnable { showAdIfAvailable() }

    var handlerCanEnter: Handler? = Handler(Looper.getMainLooper())
    var runnableCanEnter: Runnable = object : Runnable {
        override fun run() {
            if (NetworkState.isOnline()) {
                if (RemoteConfigUtils.canEnter) {
                    Log.e("TAG", "runEnter: " + RemoteConfigUtils.canEnter)
                    handler?.removeCallbacks(runnable)
                    handlerCanEnter?.removeCallbacks(this)
                    fetchAd()
                    handler?.postDelayed(runnable, SPLASH_TIME_OUT)
                } else {
                    showErrorDialog()
                }
            } else continueExecution()
        }
    }

    fun continueExecution() {
        handler?.removeCallbacks(runnable)
        handlerCanEnter?.removeCallbacks(runnableCanEnter)
        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        finish()
    }

    private fun showErrorDialog() {
        handler?.removeCallbacks(runnable)
        handlerCanEnter?.removeCallbacks(runnableCanEnter)
        Toast.makeText(this, "Sorry, You can't enter this app.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onResume() {
        super.onResume()
        handlerCanEnter?.postDelayed(runnableCanEnter, 2000)
    }

    override fun onPause() {
        handler?.removeCallbacks(runnable)
        handler = Handler(Looper.getMainLooper())
        handlerCanEnter?.removeCallbacks(runnableCanEnter)
        super.onPause()
    }

    override fun onDestroy() {
        handler?.removeCallbacks(runnable)
        handler = null
        super.onDestroy()
    }

    fun fetchAd() {
        if (isAdAvailable()) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                this@SplashScreenActivity.appOpenAd = appOpenAd
                loadTime = System.currentTimeMillis()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }
        }
        if (RemoteConfigUtils.isAdmobEnabled() && RemoteConfigUtils.canEnter) {
            val request: AdRequest = getAdRequest()
            AppOpenAd.load(
                this, getString(R.string.app_open_id), request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
            )
        }
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        continueExecution()
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }

                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        isShowingAd = false
                        continueExecution()
                    }
                }
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd?.show(this)
        } else {
            continueExecution()
        }
    }

    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = System.currentTimeMillis() - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    override fun onBackPressed() {
    }
}