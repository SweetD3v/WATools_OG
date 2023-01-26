package com.gbversion.tool.statussaver.tools.photoeditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPicEditorHomeBinding
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.ui.activities.BaseActivity
import com.gbversion.tool.statussaver.ui.fragments.HomeFragment
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import gun0912.tedimagepicker.builder.TedImagePicker

class PicEditorHomeActivity : BaseActivity() {
    val binding by lazy { ActivityPicEditorHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PicEditorHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNativeProgress(
                    this@PicEditorHomeActivity,
                    getString(R.string.admob_native_id),
                    adFrame,
                    adProgress
                )
            }

            imgBack.setOnClickListener {
                onBackPressed()
            }

            llEdit.setOnClickListener {
                TedImagePicker.with(this@PicEditorHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        Log.e("TAG", "onCreatePic: $uri")
                        val intent = Intent(
                            this@PicEditorHomeActivity,
                            PicEditActivity::class.java
                        )
                        intent.putExtra(HomeFragment.KEY_SELECTED_PHOTOS, uri.toString())
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@PicEditorHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_editor")
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