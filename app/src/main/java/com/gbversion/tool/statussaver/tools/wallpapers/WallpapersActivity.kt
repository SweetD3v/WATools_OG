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
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.widgets.MarginItemDecoration

class WallpapersActivity : BaseActivity() {
    val binding by lazy { ActivityWallpapersBinding.inflate(layoutInflater) }
    lateinit var wallpapersList: MutableList<WallModelPixabay.PhotoDetails>
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

        if (NetworkState.isOnline()) {

//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

            AdsUtils.loadNative(
                this@WallpapersActivity,
                getString(R.string.admob_native_id),
                binding.adFrame
            )

            loadWallpapers()

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

            for (photo in arr) {
                val photoDetails = WallModelPixabay.PhotoDetails()
                photoDetails.largeImageURL = photo
                wallpapersList.add(photoDetails)
            }

            wallpapersList.shuffle()
            wallpapersAdapter.setList(wallpapersList)

        }
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
            val wallpaper = wallpapers[holder.bindingAdapterPosition]
            Log.e("TAG", "onBindViewHolder: ${wallpaper.largeImageURL}")
            Glide.with(ctx).load(wallpaper.largeImageURL)
                .centerCrop()
                .into(holder.binding.ivThumbnail)

            holder.itemView.setOnClickListener {
                ctx.startActivity(
                    Intent(ctx, WallpapersDetailsActivity::class.java)
                        .putExtra("position", holder.bindingAdapterPosition)
                        .putExtra(
                            WALLPAPER_ORIGINAL_URL,
                            wallpapers[holder.bindingAdapterPosition].largeImageURL
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