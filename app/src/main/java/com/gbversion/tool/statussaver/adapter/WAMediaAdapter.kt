package com.gbversion.tool.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gbversion.tool.statussaver.databinding.ItemStatusBinding
import com.gbversion.tool.statussaver.models.Media
import com.gbversion.tool.statussaver.ui.activities.FullViewWhatsappActivity
import com.squareup.picasso.Picasso

class WAMediaAdapter(
    var ctx: Context,
    var mediaList: MutableList<Media>
) :
    RecyclerView.Adapter<WAMediaAdapter.VH>() {
    class VH(var binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val media = mediaList[holder.adapterPosition]
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
                    .putExtra("position", holder.adapterPosition)
                    .putExtra(
                        "type",
                        if (mediaList[holder.adapterPosition].isVideo) "video"
                        else "photo"
                    )
            )
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }
}