package com.gbversion.tool.statussaver.tools.insta_grid

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityGridSaveBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.insta_grid.GridUtils.Companion.saveImageTemp
import com.gbversion.tool.statussaver.ui.activities.BaseActivity
import com.gbversion.tool.statussaver.utils.*

class GridSaveActivity : BaseActivity() {
    val binding by lazy { ActivityGridSaveBinding.inflate(layoutInflater) }
    var bmps: Array<Bitmap?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkState.isOnline()) {
//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )
            binding.run {
                AdsUtils.loadNativeSmallProgress(
                    this@GridSaveActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame,
                    adProgress
                )
            }
        }

        populateGrids()
    }

    private fun populateGrids() {
        GridUtils.bitmaps?.let { bmps ->
            this.bmps = bmps
            binding.imgBmp1.setImageBitmap(bmps[0])
            binding.imgBmp2.setImageBitmap(bmps[1])
            binding.imgBmp3.setImageBitmap(bmps[2])
            binding.imgBmp4.setImageBitmap(bmps[3])
            binding.imgBmp5.setImageBitmap(bmps[4])
            binding.imgBmp6.setImageBitmap(bmps[5])
            binding.imgBmp7.setImageBitmap(bmps[6])
            binding.imgBmp8.setImageBitmap(bmps[7])
            binding.imgBmp9.setImageBitmap(bmps[8])
        }

        binding.run {
            imgBack.setOnClickListener {
                onBackPressed()
            }

            imgSave.setOnClickListener {
                AdsUtils.loadInterstitialAd(this@GridSaveActivity,
                    RemoteConfigUtils.adIdInterstital(),
                    object : AdsUtils.Companion.FullScreenCallback() {
                        override fun continueExecution() {
                            object : AsyncTaskRunner<Void?, Void?>(this@GridSaveActivity) {
                                override fun doInBackground(params: Void?): Void? {
                                    GridUtils.bitmaps?.let { bmps ->
                                        for (bmp in bmps) {
                                            GridUtils.saveBitmapGrid(
                                                this@GridSaveActivity,
                                                bmp,
                                                "Cropped_${System.currentTimeMillis()}_${
                                                    bmps.indexOf(
                                                        bmp
                                                    )
                                                }",
                                                "Insta Grid"
                                            )
                                        }
                                    }

                                    return null
                                }

                                override fun onPostExecute(result: Void?) {
                                    super.onPostExecute(result)

                                    toastShort(this@GridSaveActivity, "Grids created!")
                                    finish()
                                }
                            }.execute(null, true)
                        }
                    })
            }

            imgShare.setOnClickListener {
                txt1.visibility = View.VISIBLE
                txt2.visibility = View.VISIBLE
                txt3.visibility = View.VISIBLE
                txt4.visibility = View.VISIBLE
                txt5.visibility = View.VISIBLE
                txt6.visibility = View.VISIBLE
                txt7.visibility = View.VISIBLE
                txt8.visibility = View.VISIBLE
                txt9.visibility = View.VISIBLE

                txtShareInsta.text = "Tap on the image no. to share it to the Instagram"
            }

            imgBmp1.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[0]?.let {
                        if (txt1.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }

                    }
                }
            }
            imgBmp2.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[1]?.let {
                        if (txt2.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
            imgBmp3.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[2]?.let {
                        if (txt3.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
            imgBmp4.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[3]?.let {
                        if (txt4.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
            imgBmp5.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[4]?.let {
                        if (txt5.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
            imgBmp6.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[5]?.let {
                        if (txt6.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
            imgBmp7.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[6]?.let {
                        if (txt7.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
            imgBmp8.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[7]?.let {
                        if (txt8.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
            imgBmp9.setOnClickListener {
                bmps?.let { bmpArr ->
                    bmpArr[8]?.let {
                        if (txt9.visibility == View.VISIBLE)
                            saveImageTemp(this@GridSaveActivity, it) { list ->
                                shareToInsta(this@GridSaveActivity, list)
                            }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}