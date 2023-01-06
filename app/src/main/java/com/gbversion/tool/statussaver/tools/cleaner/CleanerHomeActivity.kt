package com.gbversion.tool.statussaver.tools.cleaner

import android.content.Intent
import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityCleanerHomeBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.AppUtils
import com.gbversion.tool.statussaver.utils.NetworkState

class CleanerHomeActivity : BaseActivity() {
    val binding by lazy { ActivityCleanerHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()
                && RemoteConfigUtils.canEnter
            ) {
                AdsUtils.loadBanner(
                    this@CleanerHomeActivity, RemoteConfigUtils.adIdBanner(),
                    bannerContainer
                )
            }

            toolbar.appTitle.text = getString(R.string.cleaner)
            toolbar.imgBack.setOnClickListener { onBackPressed() }
            animCleaner.setOnClickListener {
                AppUtils.CLEANER_TYPE = 0
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clBatterySaver.setOnClickListener {
                AppUtils.CLEANER_TYPE = 1
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clNetOptimization.setOnClickListener {
                AppUtils.CLEANER_TYPE = 2
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clPhoneBooster.setOnClickListener {
                AppUtils.CLEANER_TYPE = 3
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clCpuCooler.setOnClickListener {
                AppUtils.CLEANER_TYPE = 4
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

