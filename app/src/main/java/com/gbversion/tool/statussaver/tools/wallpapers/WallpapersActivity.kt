package com.gbversion.tool.statussaver.tools.wallpapers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityWallpapersBinding
import com.gbversion.tool.statussaver.databinding.ItemWallpapersBinding
import com.gbversion.tool.statussaver.models.WallModelPixabay
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.widgets.MarginItemDecoration
import java.io.IOException

class WallpapersActivity : BaseActivity() {
    val binding by lazy { ActivityWallpapersBinding.inflate(layoutInflater) }
    lateinit var wallpapersList: MutableList<WallModelPixabay.PhotoDetails>
    lateinit var wallpapersListOff: MutableList<String>
    var lastVisiblesItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading: Boolean = true
    var mIsLastPage: Boolean = false
    var layoutManager = GridLayoutManager(this@WallpapersActivity, 2)
    var category: String? = "nature"
    var catSelected = 0
    var reset = true

    val walpType by lazy { intent.getStringExtra("walpType") }

    companion object {
        var pageIndex = 1
        var perPage: Int = 20
        const val WALLPAPER_ORIGINAL_URL: String = "wallUrl"
    }

    lateinit var wallpapersAdapter: WallpapersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadWallpapers()

        if (NetworkState.isOnline()) {

//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

            AdsUtils.loadNativeProgress(
                this@WallpapersActivity,
                RemoteConfigUtils.adIdNative(),
                binding.adFrame,
                binding.adProgress
            )

            binding.imgDownloads.setOnClickListener {
                startActivity(
                    Intent(
                        this@WallpapersActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        if (walpType == "wallpapers") {
                            putExtra(MyCreationToolsActivity.CREATION_TYPE, "wallpapers")
                        } else {
                            putExtra(MyCreationToolsActivity.CREATION_TYPE, "status")
                        }
                    }
                )
            }
        }
    }

    private fun loadWallpapers() {
        binding.run {
            imgBack.setOnClickListener {
                onBackPressed()
            }

            rvWallpapers.isNestedScrollingEnabled = false

            rvWallpapers.layoutManager = layoutManager
            rvWallpapers.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelSize(
                        R.dimen.rv_space
                    ), 2, RecyclerView.VERTICAL
                )
            )

            wallpapersList = mutableListOf()
            wallpapersAdapter = WallpapersAdapter(this@WallpapersActivity).apply {
                walpType = this@WallpapersActivity.walpType.toString()
            }
            binding.rvWallpapers.adapter = wallpapersAdapter

            val arr: Array<String>
            if (walpType == "wallpapers") {
                arr = resources.getStringArray(R.array.wallp_arr)
                appTitle.text = getString(R.string.wallpapers)
            }
            else {
                arr = resources.getStringArray(R.array.status_arr)
                appTitle.text = getString(R.string.status_maker)
            }

            val wallOn = mutableListOf<WallModelPixabay.PhotoDetails>()
            for (photo in arr) {
                val photoDetails = WallModelPixabay.PhotoDetails()
                photoDetails.largeImageURL = photo
                wallOn.add(photoDetails)
            }
            wallOn.shuffle()

            wallpapersListOff = mutableListOf()
            listAssetFiles("wallpapers")

            for (wall in wallpapersListOff) {
                val photoDetails = WallModelPixabay.PhotoDetails()
                photoDetails.largeImageURL = "file:///android_asset/wallpapers/$wall"
                wallpapersList.add(photoDetails)
            }

            wallpapersList.addAll(wallOn)
            wallpapersAdapter.setList(wallpapersList)
        }
    }

    private fun listAssetFiles(path: String): Boolean {
        val list: Array<String>?
        try {
            list = assets.list(path)
            if (list?.isNotEmpty() == true) {
                // This is a folder
                for (file in list) {
                    if (!listAssetFiles("$path/$file")) return false else {
                        // This is a file
                        wallpapersListOff.add(file)
                    }
                }
            }
        } catch (e: IOException) {
            return false
        }
        return true
    }

    class WallpapersAdapter(
        var ctx: Context
    ) :
        RecyclerView.Adapter<WallpapersAdapter.VH>() {
        lateinit var wallpapers: MutableList<WallModelPixabay.PhotoDetails>
        var walpType = "wallpapers"

        fun updateList(wallpapers: MutableList<WallModelPixabay.PhotoDetails>) {
            this.wallpapers = wallpapers
        }

        fun setList(list: MutableList<WallModelPixabay.PhotoDetails>) {
            wallpapers = list
            notifyDataSetChanged()
        }

        fun addAll(newList: MutableList<WallModelPixabay.PhotoDetails>) {
            val lastIndex: Int = wallpapers.size - 1
            wallpapers.addAll(newList)
            notifyItemRangeInserted(lastIndex, newList.size)
        }

        class VH(var binding: ItemWallpapersBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemWallpapersBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val wallpaper = wallpapers[holder.adapterPosition]
            Log.e("TAG", "onBindViewHolder: ${wallpaper.largeImageURL}")
            Glide.with(ctx).load(wallpaper.largeImageURL)
                .centerCrop()
                .into(holder.binding.ivThumbnail)

            holder.itemView.setOnClickListener {
                ctx.startActivity(
                    Intent(ctx, WallpapersDetailsActivity::class.java)
                        .putExtra("position", holder.adapterPosition)
                        .putExtra(
                            WALLPAPER_ORIGINAL_URL,
                            wallpapers[holder.adapterPosition].largeImageURL
                        )
                        .putExtra("walpType", walpType)
                )
            }
        }

        override fun getItemCount(): Int {
            return wallpapers.size
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