package com.gbversion.tool.statussaver.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gbversion.tool.statussaver.databinding.ActivityHomeStatusBinding
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils

class HomeStatus_Activity : AppCompatActivity() {
    val binding by lazy { ActivityHomeStatusBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.run {

            if (NetworkState.isOnline()) {
                AdsUtils.loadBanner(
                    this@HomeStatus_Activity,
                    RemoteConfigUtils.adIdBanner(),
                    bannerContainer
                )
            }

            binding.imgBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}