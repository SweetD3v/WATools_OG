package com.gbversion.tool.statussaver.tools.insta_grid

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityInstaGridBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.utils.getBitmapFromUri
import com.yalantis.ucrop.UCrop
import gun0912.tedimagepicker.builder.TedImagePicker
import java.io.File

class InstaGridActivity : BaseActivity() {
    val CROPPED_IMAGE_NAME = "CroppedImage"

    val binding by lazy { ActivityInstaGridBinding.inflate(layoutInflater) }
    val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val destinationFileName = "${CROPPED_IMAGE_NAME}.jpg"

        val uCrop = UCrop.of(uri!!, Uri.fromFile(File(cacheDir, destinationFileName)))
        val options = UCrop.Options()
        uCrop.withOptions(options)
        uCrop.start(this, cropLauncher)
    }

    var bmp: Bitmap? = null

    val cropLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.data != null) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    bmp = getBitmapFromUri(this, resultUri)

                    bmp?.let {
                        GridUtils.bitmaps = GridUtils.splitBitmap(this, it)

                        startActivity(Intent(this, GridSaveActivity::class.java))
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@InstaGridActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )
                AdsUtils.loadNativeSmallProgress(
                    this@InstaGridActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame,
                    adProgress
                )
            }

            imgBack.setOnClickListener {
                onBackPressed()
            }

            imgSelectImage.setOnClickListener {
                TedImagePicker.with(this@InstaGridActivity)
                    .showCameraTile(false)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        val destinationFileName = "${CROPPED_IMAGE_NAME}.jpg"

                        val uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationFileName)))
                        val options = UCrop.Options()
                        options.withAspectRatio(1f, 1f)
                        uCrop.withOptions(options)
                        uCrop.start(this@InstaGridActivity, cropLauncher)
                    }
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