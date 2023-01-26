package com.gbversion.tool.statussaver.tools.cartoonify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivitySketchifyHomeBinding
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import gun0912.tedimagepicker.builder.TedImagePicker

class SketchifyHomeActivity : BaseActivity() {

    val binding by lazy { ActivitySketchifyHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@SketchifyHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

//                AdsUtils.loadNative(
//                    this@SketchifyHomeActivity,
//                    getString(R.string.admob_native_id),
//                    adFrame
//                )
            }

            imgBack.setOnClickListener {
                onBackPressed()
            }

            llSketchify.setOnClickListener {
                TedImagePicker.with(this@SketchifyHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        val intent = Intent(
                            this@SketchifyHomeActivity,
                            SketchifyActivity::class.java
                        )
                        intent.putExtra(CartoonActivity.SELECTED_PHOTO, uri.toString())
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@SketchifyHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "sketchify")
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