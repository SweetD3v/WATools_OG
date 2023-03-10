package com.gbversion.tool.statussaver.utils

import androidx.recyclerview.widget.DiffUtil
import com.gbversion.tool.statussaver.models.Media

class MediaDiffCallback(
    var oldMediaList: MutableList<Media>,
    var newMediaList: MutableList<Media>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldMediaList.size
    }

    override fun getNewListSize(): Int {
        return newMediaList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMediaList[oldItemPosition].path == newMediaList[newItemPosition].path
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMedia = oldMediaList[oldItemPosition]
        val newMedia = newMediaList[newItemPosition]

        return oldMedia.path == newMedia.path
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}