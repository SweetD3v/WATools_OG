package com.gbversion.tool.statussaver.tools.photo_filters.deform

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhotoWarpBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.cartoonify.CartoonActivity
import com.gbversion.tool.statussaver.tools.photo_filters.PhotoFiltersSaveActivity
import com.gbversion.tool.statussaver.tools.photo_filters.PhotoFiltersUtils
import com.gbversion.tool.statussaver.utils.*
import org.wysaid.common.Common
import org.wysaid.nativePort.CGEDeformFilterWrapper
import org.wysaid.nativePort.CGEImageHandler
import org.wysaid.texUtils.TextureRenderer
import org.wysaid.view.ImageGLSurfaceView
import kotlin.math.min

class PhotoWarpActivity : AppCompatActivity() {
    val binding by lazy { ActivityPhotoWarpBinding.inflate(layoutInflater) }
    val photoUri by lazy {
        intent.getStringExtra(CartoonActivity.SELECTED_PHOTO).toString().toUri()
    }
    var orgBitmap: Bitmap? = null

    private var mDeformWrapper: CGEDeformFilterWrapper? = null
    private val mTouchRadius = 200.0f
    private val mTouchIntensaity = 0.1f

    enum class DeformMode {
        Restore, Forward, Bloat, Wrinkle
    }

    var mDeformMode = DeformMode.Forward

    var mTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        var mLastX = 0f
        var mLastY = 0f
        var mIsMoving = false
        var mHasMotion = false

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            Log.e("TAG", "mDeformWrapper: $mDeformWrapper")
            if (mDeformWrapper == null) {
                return false
            }
            val viewport: TextureRenderer.Viewport = binding.imgPhoto.renderViewport
            val w = viewport.width.toFloat()
            val h = viewport.height.toFloat()
            val x: Float = event.x - viewport.x
            val y: Float = event.y - viewport.y
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    mIsMoving = true
                    mLastX = x
                    mLastY = y
                    if (!mDeformWrapper!!.canUndo()) {
                        mDeformWrapper!!.pushDeformStep()
                    }
                    return true
                }
                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mIsMoving = false
                    if (mHasMotion) {
                        binding.imgPhoto.queueEvent(Runnable {
                            if (binding.imgPhoto != null) {
                                mDeformWrapper!!.pushDeformStep()
                                Log.i(Common.LOG_TAG, "Init undo status...")
                            }
                        })
                    }
                    return true
                }
                else -> return true
            }
            val lastX = mLastX
            val lastY = mLastY
            binding.imgPhoto.lazyFlush(true, Runnable {
                if (mDeformWrapper == null) return@Runnable
                when (mDeformMode) {
                    DeformMode.Restore -> mDeformWrapper!!.restoreWithPoint(
                        x,
                        y,
                        w,
                        h,
                        mTouchRadius,
                        mTouchIntensaity
                    )
                    DeformMode.Forward -> mDeformWrapper!!.forwardDeform(
                        lastX,
                        lastY,
                        x,
                        y,
                        w,
                        h,
                        mTouchRadius,
                        mTouchIntensaity
                    )
                    DeformMode.Bloat -> mDeformWrapper!!.bloatDeform(
                        x,
                        y,
                        w,
                        h,
                        mTouchRadius,
                        mTouchIntensaity
                    )
                    DeformMode.Wrinkle -> mDeformWrapper!!.wrinkleDeform(
                        x,
                        y,
                        w,
                        h,
                        mTouchRadius,
                        mTouchIntensaity
                    )
                }
                mHasMotion = true
            })
            mLastX = x
            mLastY = y
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setDarkStatusBar(this)

        if (NetworkState.isOnline())
            AdsUtils.loadBanner(
                this, RemoteConfigUtils.adIdBanner(),
                binding.bannerContainer
            )

        binding.imgPhoto.setSurfaceCreatedCallback {
            val bmp = getBitmapFromUri(this, photoUri)
            orgBitmap = bmp?.copy(bmp.config, true)

            binding.imgPhoto.setImageBitmap(orgBitmap)
            binding.imgPhoto.queueEvent {
                var w: Float = orgBitmap?.width?.toFloat() ?: 0f
                var h: Float = orgBitmap?.height?.toFloat() ?: 0f
                val scaling = min(1280.0f / w, 1280.0f / h)
                if (scaling < 1.0f) {
                    w *= scaling
                    h *= scaling
                }
                Log.e("TAG", "onCreateW: $w")
                mDeformWrapper = CGEDeformFilterWrapper.create(w.toInt(), h.toInt(), 10.0f)
                Log.e("TAG", "onCreateWWW: $mDeformWrapper")
                mDeformWrapper?.let { wrapper ->
                    wrapper.setUndoSteps(200) // set max undo steps to 200.
                    val handler: CGEImageHandler = binding.imgPhoto.imageHandler
                    handler.setFilterWithAddres(wrapper.nativeAddress)
                    handler.processFilters()
                }
            }
        }

        binding.run {
            imgPhoto.displayMode = ImageGLSurfaceView.DisplayMode.DISPLAY_ASPECT_FIT
            imgPhoto.setOnTouchListener(mTouchListener)

            btnSave.setOnClickListener {
                imgPhoto.getResultBitmap {
                    object : AsyncTaskRunner<Bitmap, String?>(this@PhotoWarpActivity) {
                        var pathStr: String = ""
                        override fun doInBackground(params: Bitmap?): String {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                saveBitmapImage(
                                    this@PhotoWarpActivity,
                                    params,
                                    "IMG_WARP_${System.currentTimeMillis()}",
                                    "PhotoWarp"
                                ) {
                                    pathStr = it
                                }
                            } else {
                                FileUtilsss.saveBitmapAsFileA10(
                                    this@PhotoWarpActivity,
                                    params,
                                    "IMG_WARP_${System.currentTimeMillis()}",
                                    "PhotoWarp"
                                ) {
                                    pathStr = it
                                }
                            }
                            return pathStr
                        }

                        override fun onPostExecute(result: String?) {
                            super.onPostExecute(result)
                            result?.let { path ->

                                MediaScannerConnection.scanFile(
                                    this@PhotoWarpActivity, arrayOf(
                                        path
                                    ), null
                                ) { path1: String?, uri1: Uri? -> }

                                Toast.makeText(
                                    this@PhotoWarpActivity,
                                    "Saved to: $path",
                                    Toast.LENGTH_SHORT
                                ).show()

                                AdsUtils.loadInterstitialAd(this@PhotoWarpActivity,
                                    RemoteConfigUtils.adIdInterstital(),
                                    object : AdsUtils.Companion.FullScreenCallback() {
                                        override fun continueExecution() {
                                            PhotoFiltersUtils.photoFilterBmp = it
                                            startActivity(
                                                Intent(
                                                    this@PhotoWarpActivity,
                                                    PhotoFiltersSaveActivity::class.java
                                                )
                                                    .putExtra("type", "warp")
                                            )
                                        }
                                    })
                            }
                        }
                    }.execute(it, true)
                }
            }
        }
    }

    override fun onDestroy() {
        PhotoFiltersUtils.photoFilterBmp = null
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}