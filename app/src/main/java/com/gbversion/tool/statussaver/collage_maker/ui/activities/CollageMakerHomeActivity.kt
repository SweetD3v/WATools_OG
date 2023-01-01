package com.gbversion.tool.statussaver.collage_maker.ui.activities

import android.content.Intent
import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityCollageMakerHomeBinding
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.ui.activities.BaseActivity
import com.gbversion.tool.statussaver.ui.fragments.HomeFragment
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import gun0912.tedimagepicker.builder.TedImagePicker

class CollageMakerHomeActivity : BaseActivity() {

    val binding by lazy { ActivityCollageMakerHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            imgBack.setOnClickListener {
                onBackPressed()
            }

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@CollageMakerHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )
                AdsUtils.loadNative(
                    this@CollageMakerHomeActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            appTitle.text = getString(R.string.collage_maker)

            llCollageMaker.setOnClickListener {
                TedImagePicker.with(this@CollageMakerHomeActivity)
                    .dropDownAlbum()
                    .min(2, "Select at least 2 images")
                    .max(9, "You can't select more than 9 images")
                    .imageCountTextFormat("%s images")
                    .startMultiImage { list ->
                        val paths = ArrayList<String>()
                        for (i in list.indices) {
                            paths.add(list[i].toString())
                        }

                        val intent =
                            Intent(this@CollageMakerHomeActivity, CollageViewActivity::class.java)
                        intent.putStringArrayListExtra(
                            HomeFragment.KEY_DATA_RESULT,
                            paths
                        )
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@CollageMakerHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "collage_maker")
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