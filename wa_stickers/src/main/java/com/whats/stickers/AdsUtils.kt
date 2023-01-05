package com.whats.stickers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class AdsUtils {
    companion object{
        fun loadNative(context: Context, adId: String, frameLayout: FrameLayout) {
            val adLoader =
                AdLoader.Builder(context, adId)
                    .forNativeAd { nativeAd: NativeAd ->
                        val adView =
                            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                                .inflate(
                                    R.layout.admob_native_medium_new,
                                    null
                                ) as NativeAdView
                        adView.mediaView = adView.findViewById(R.id.media_view)
                        adView.headlineView = adView.findViewById(R.id.primary)
                        adView.bodyView = adView.findViewById(R.id.secondary)
                        adView.callToActionView = adView.findViewById(R.id.call_to_action)
                        adView.advertiserView = adView.findViewById(R.id.tertiary)
                        adView.iconView = adView.findViewById(R.id.icon)
                        populateUnifiedNativeAdView(adView, nativeAd)
                        frameLayout.visibility = View.VISIBLE
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            frameLayout.visibility = View.GONE
                        }
                    })
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: $error")
                        }
                    })
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun populateUnifiedNativeAdView(
            unifiedNativeAdView: NativeAdView,
            unifiedNativeAd: NativeAd
        ) {
            (unifiedNativeAdView.headlineView as TextView).text = unifiedNativeAd.headline
            (unifiedNativeAdView.bodyView as TextView).text = unifiedNativeAd.body
            (unifiedNativeAdView.callToActionView as TextView).text = unifiedNativeAd.callToAction
            val icon = unifiedNativeAd.icon
            if (icon == null) {
                unifiedNativeAdView.iconView?.visibility = View.INVISIBLE
            } else {
                if (unifiedNativeAdView.iconView != null) {
                    (unifiedNativeAdView.iconView as ImageView).setImageDrawable(icon.drawable)
                    unifiedNativeAdView.iconView?.visibility = View.VISIBLE
                }
            }
            if (unifiedNativeAd.advertiser == null) {
                unifiedNativeAdView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (unifiedNativeAdView.advertiserView as TextView).text = unifiedNativeAd.advertiser
                unifiedNativeAdView.advertiserView?.visibility = View.VISIBLE
            }
            unifiedNativeAdView.setNativeAd(unifiedNativeAd)
        }
    }
}