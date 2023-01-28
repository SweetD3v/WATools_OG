package com.gbversion.tool.statussaver.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.gbversion.tool.statussaver.databinding.ActivitySplashBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.utils.gone
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

class SplashScreenActivity : BaseActivity(), LifecycleObserver {
    val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val SPLASH_TIME_OUT: Long = 6000
    var isShowingAd = false
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var loadTime = 0L

    var handler: Handler? = Handler(Looper.getMainLooper())
    var runnable = Runnable { showAdIfAvailable() }

    var cachePercentage: Int = 0
    var handlerConnecting: Handler? = Handler(Looper.getMainLooper())
    val runnableConnecting = object : Runnable {
        override fun run() {
            cachePercentage += 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                binding.progressLoading.setProgress(cachePercentage, true)
            else binding.progressLoading.progress = cachePercentage
            handlerConnecting?.postDelayed(this, 50)

            if (cachePercentage == 100) {
                handlerConnecting?.removeCallbacks(this)
                cachePercentage = 0
            }
        }
    }

    fun continueExecution() {
        handler?.removeCallbacks(runnable)
        handlerConnecting?.removeCallbacks(runnableConnecting)
        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        finish()
    }

    private fun showErrorDialog() {
        handler?.removeCallbacks(runnable)
        Toast.makeText(this, "Sorry, You can't enter this app.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        fetchAd()
        handlerConnecting?.post(runnableConnecting)

        handler?.postDelayed({
            showAdIfAvailable()
        }, SPLASH_TIME_OUT)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        handler?.removeCallbacks(runnable)
        handler = Handler(Looper.getMainLooper())
        handlerConnecting?.removeCallbacks(runnableConnecting)
        handlerConnecting = Handler(Looper.getMainLooper())
        super.onPause()
    }

    override fun onDestroy() {
        handler?.removeCallbacks(runnable)
        handler = null
        handlerConnecting?.removeCallbacks(runnableConnecting)
        handlerConnecting = null
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
        val request: AdRequest = getAdRequest()
        AppOpenAd.load(
            this, RemoteConfigUtils.adIdAppOpen(), request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback!!
        )
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        continueExecution()
                    }

                    override fun onAdShowedFullScreenContent() {
                        binding.llLoading.gone()
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