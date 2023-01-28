package com.gbversion.tool.statussaver.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gbversion.tool.statussaver.databinding.ItemStatusBinding
import com.gbversion.tool.statussaver.models.Media
import com.gbversion.tool.statussaver.ui.activities.FullViewWhatsappActivity
import com.squareup.picasso.Picasso

class WAMediaAdapter(
    var ctx: Context,
) :
    ListAdapter<Media, WAMediaAdapter.VH>(ValueComparator()) {
    class VH(var binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root)

    val listDiffer by lazy { AsyncListDiffer(this, ValueComparator()) }

    fun updateList(mediaList: MutableList<Media>) {
        listDiffer.submitList(mediaList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val media = listDiffer.currentList[holder.bindingAdapterPosition]
        if (media.isVideo) {
            Glide.with(ctx).load(media.uri)
                .into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.VISIBLE
        } else {
            Picasso.get().load(media.uri)
                .config(Bitmap.Config.RGB_565)
                .into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            ctx.startActivity(
                Intent(ctx, FullViewWhatsappActivity::class.java)
                    .putExtra("position", holder.bindingAdapterPosition)
                    .putExtra(
                        "type",
                        if (listDiffer.currentList[holder.bindingAdapterPosition].isVideo) "video"
                        else "photo"
                    )
            )
        }
    }

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }

    class ValueComparator : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem.path == newItem.path
        }
    }
}