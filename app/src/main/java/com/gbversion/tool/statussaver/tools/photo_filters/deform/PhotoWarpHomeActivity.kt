package com.gbversion.tool.statussaver.tools.photo_filters.deform

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhotoWarpHomeBinding
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.cartoonify.CartoonActivity.Companion.SELECTED_PHOTO
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import gun0912.tedimagepicker.builder.TedImagePicker

class PhotoWarpHomeActivity : BaseActivity() {

    val binding by lazy { ActivityPhotoWarpHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PhotoWarpHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

//                AdsUtils.loadNative(
//                    this@PhotoWarpHomeActivity,
//                    RemoteConfigUtils.adIdNative(),
//                    adFrame
//                )
            }

            imgBack.setOnClickListener { onBackPressed() }

            llPhotoFilters.setOnClickListener {
                TedImagePicker.with(this@PhotoWarpHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri? ->
                        startActivity(
                            Intent(this@PhotoWarpHomeActivity, PhotoWarpActivity::class.java)
                                .putExtra(SELECTED_PHOTO, uri.toString())
                        )
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@PhotoWarpHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_warp")
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }
}