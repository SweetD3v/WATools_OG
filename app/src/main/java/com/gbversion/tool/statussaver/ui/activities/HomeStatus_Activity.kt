package com.gbversion.tool.statussaver.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gbversion.tool.statussaver.databinding.ActivityHomeStatusBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState

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

            binding.run {
                imgBack.setOnClickListener {
                    onBackPressed()
                }

                imgDownloads.setOnClickListener {
                    startActivity(
                        Intent(
                            this@HomeStatus_Activity,
                            MyCreationToolsActivity::class.java
                        ).apply {
                            putExtra(MyCreationToolsActivity.CREATION_TYPE, "wa_status")
                        }
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}