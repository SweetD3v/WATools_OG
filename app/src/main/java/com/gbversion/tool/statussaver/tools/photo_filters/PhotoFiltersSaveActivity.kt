package com.gbversion.tool.statussaver.tools.photo_filters

import android.content.Intent
import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhotoFiltersSaveBinding
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState

class PhotoFiltersSaveActivity : BaseActivity() {
    val binding by lazy { ActivityPhotoFiltersSaveBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkState.isOnline()) {
//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

            AdsUtils.loadNative(
                this@PhotoFiltersSaveActivity,
                getString(R.string.admob_native_id),
                binding.adFrame
            )
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.preview.setImageBitmap(PhotoFiltersUtils.photoFilterBmp)

        binding.btnMyCreation.setOnClickListener {
            if (intent.getStringExtra("type") == "filter") {
                startActivity(
                    Intent(
                        this@PhotoFiltersSaveActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_filter")
                    }
                )
            } else {
                startActivity(
                    Intent(
                        this@PhotoFiltersSaveActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_warp")
                    }
                )
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}