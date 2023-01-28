package com.gbversion.tool.statussaver.tools.compress

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhotocmpHomeBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.ui.activities.BaseActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import gun0912.tedimagepicker.builder.TedImagePicker.Companion.with

class PhotoCmpHomeActivity : BaseActivity() {
    companion object {
        const val SELECTED_PHOTO = "SELECTED_PHOTO"
    }

    val binding by lazy { ActivityPhotocmpHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PhotoCmpHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNativeProgress(
                    this@PhotoCmpHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame,
                    adProgress
                )
            }

            imgBack.setOnClickListener { onBackPressed() }

            llPhotoCompress.setOnClickListener {
                with(this@PhotoCmpHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        val intent = Intent(
                            this@PhotoCmpHomeActivity,
                            CompressPhotoActivity::class.java
                        )
                        intent.putExtra(SELECTED_PHOTO, uri.toString())
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@PhotoCmpHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_cmp")
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