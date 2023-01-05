package com.gbversion.tool.statussaver.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbversion.tool.adapters.WAMediaAdapter
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.FragmentWaimagesBinding
import com.gbversion.tool.statussaver.interfaces.WATypeChangeListener
import com.gbversion.tool.statussaver.models.Media
import com.gbversion.tool.statussaver.utils.addOuterGridSpacing
import com.gbversion.tool.statussaver.utils.getMediaWACoroutine
import com.gbversion.tool.statussaver.utils.getMediaWAWBCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WAVideosFragment : BaseFragment<FragmentWaimagesBinding>(), WATypeChangeListener {
    override fun getLayout(): FragmentWaimagesBinding {
        return FragmentWaimagesBinding.inflate(layoutInflater)
    }

    var decorationAdded: Boolean? = false
    var waTypeChangeListener: WATypeChangeListener? = null
    var waType = 0

    var job = Job()
    var ioScope = CoroutineScope(Dispatchers.IO + job)
    var uiScope = CoroutineScope(Dispatchers.Main + job)

    private val PERMISSIONS = mutableListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val permissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var granted = true
            if (result != null) {
                for (b in result.values) {
                    if (!b) {
                        granted = false
                        break
                    }
                }
            } else granted = false

//            if (granted) {
//                if (waType == 0)
//                    loadImages()
//                else loadImagesWB()
//            }
        }

    val statusFileResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent = result.data!!
                Log.d("HEY: ", data.data.toString())
                ctx.contentResolver.takePersistableUriPermission(
                    data.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Toast.makeText(ctx, "Success", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onTypeChanged(type: Int) {
        waType = type
        Log.e("TAG", "onTypeChanged: $waType")
        if (waType == 0)
            loadVideos()
        else loadVideosWB()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
//            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val albumGridSpacing = resources.getDimension(R.dimen.rv_margin)
            if (decorationAdded == false) {
                decorationAdded = true
                rvWAImages.addOuterGridSpacing((albumGridSpacing).toInt())
                rvWAImages.addItemDecoration(GridMarginDecoration(albumGridSpacing.toInt()))
            }
        }
    }

    internal class GridMarginDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space / 2
            outRect.top = space / 2
            outRect.right = space / 2
            outRect.bottom = space / 2
        }
    }


    override fun onResume() {
        super.onResume()

        Log.e(
            "TAG",
            "onResumeStatus: ${isAllPermissionsGranted()} - ${
                shouldShowRequestPermissionRationale(PERMISSIONS[0])
            }"
        )

        if (!isAllPermissionsGranted() || ctx.contentResolver.persistedUriPermissions.size <= 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && ctx.contentResolver.persistedUriPermissions.size <= 0
            ) {
                openDocTreeStatus()
            } else {
                if (!isAllPermissionsGranted()) {
                    var askPermission = false
                    for (i in PERMISSIONS.indices) {
                        if (shouldShowRequestPermissionRationale(PERMISSIONS[i])
                            || !hasPermission(PERMISSIONS[i])
                        ) {
                            askPermission = true
                            break
                        }
                    }
                    Log.e("TAG", "askPermission: ${askPermission}")
                    if (askPermission) {
                        permissionsLauncher.launch(PERMISSIONS.toTypedArray())
                    }
                } else {
                    if (waType == 0)
                        loadVideos()
                    else loadVideosWB()
                }
            }
        } else {
            if (waType == 0)
                loadVideos()
            else loadVideosWB()
        }
    }

    fun hasPermission(permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            ctx,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isAllPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(ctx, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun arePermissionDenied(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return ctx.contentResolver.persistedUriPermissions.size <= 0
        }
        for (str in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    ctx,
                    str
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun openDocTreeStatus() {
        Log.e("TAG", "requestPermissionQ: ")
        val createOpenDocumentTreeIntent =
            (ctx.getSystemService(Activity.STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
        val replace: String =
            (createOpenDocumentTreeIntent.getParcelableExtra<Parcelable>(DocumentsContract.EXTRA_INITIAL_URI) as Uri?).toString()
                .replace("/root/", "/document/")
        val parse: Uri =
            Uri.parse("$replace%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")
        Log.d("URI", parse.toString())
        createOpenDocumentTreeIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse)
        statusFileResultLauncher.launch(createOpenDocumentTreeIntent)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun openDocTreeStatusBusiness() {
        Log.e("TAG", "requestPermissionQB: ")
        val createOpenDocumentTreeIntent =
            (ctx.getSystemService(Activity.STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
        val replace: String =
            (createOpenDocumentTreeIntent.getParcelableExtra<Parcelable>(DocumentsContract.EXTRA_INITIAL_URI) as Uri?).toString()
                .replace("/root/", "/document/")
        val parse: Uri =
            Uri.parse("$replace%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp%20Business%2FMedia%2F.Statuses")
        Log.d("URI", parse.toString())
        createOpenDocumentTreeIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse)
        statusFileResultLauncher.launch(createOpenDocumentTreeIntent)
    }

    override fun onPermissionGranted() {
//        loadImages()
    }

    private fun loadVideos() {
        if (isAdded) {
            binding.apply {
                val imageListNew = mutableListOf<Media>()

                job = Job()
                ioScope = CoroutineScope(Dispatchers.IO + job)
                uiScope = CoroutineScope(Dispatchers.Main + job)

                ioScope.launch {
                    getMediaWACoroutine(ctx) { list ->
                        for (media in list) {
                            if (!media.path.contains(".nomedia", true)
                                && ctx.contentResolver.getType(media.uri).toString()
                                    .contains("video", true)
                            ) {
                                imageListNew.add(media)
                            }
//                            Log.e("TAG", "loadImagesWA: ${media.path}")
                        }
                        uiScope.launch {
                            videosList = imageListNew
                            uiScope.launch {
                                val waMediaAdapter = WAMediaAdapter(ctx, videosList)
                                binding.rvWAImages.adapter = waMediaAdapter
                                waMediaAdapter.notifyItemRangeChanged(0, videosList.size)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadVideosWB() {
        if (isAdded) {
            binding.apply {
                val imageListNew = mutableListOf<Media>()

                job = Job()
                ioScope = CoroutineScope(Dispatchers.IO + job)
                uiScope = CoroutineScope(Dispatchers.Main + job)

                ioScope.launch {
                    getMediaWAWBCoroutine(ctx) { list ->
                        for (media in list) {
                            if (!media.path.contains(".nomedia", true)
                            ) {
                                imageListNew.add(media)
                            }
//                            Log.e("TAG", "loadImagesWA: ${media.path}")
                        }
                        uiScope.launch {
                            videosList = imageListNew
                            uiScope.launch {
                                val waMediaAdapter = WAMediaAdapter(ctx, videosList)
                                binding.rvWAImages.adapter = waMediaAdapter
                                waMediaAdapter.notifyItemRangeChanged(0, videosList.size)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
    }

    companion object {
        var videosList: MutableList<Media> = mutableListOf()
        fun newInstance(): WAVideosFragment {
            val fragment = WAVideosFragment()
            fragment.waTypeChangeListener = fragment
            return fragment
        }

        fun newInstance(waTypeChangeListener: WATypeChangeListener): WAVideosFragment {
            val fragment = WAVideosFragment()
            fragment.waTypeChangeListener = waTypeChangeListener
            return fragment
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
