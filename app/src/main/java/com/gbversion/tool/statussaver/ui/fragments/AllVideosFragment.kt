package com.gbversion.tool.statussaver.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.gbversion.tool.statussaver.adapter.AllMediaAdapter
import com.gbversion.tool.adapters.WAMediaAdapter
import com.gbversion.tool.statussaver.databinding.FragmentWaimagesBinding
import com.gbversion.tool.statussaver.models.Media
import com.gbversion.tool.statussaver.utils.dpToPx
import com.gbversion.tool.statussaver.utils.getMedia
import com.gbversion.tool.statussaver.widgets.MarginItemDecoration


class AllVideosFragment : BaseFragment<FragmentWaimagesBinding>() {
    override fun getLayout(): FragmentWaimagesBinding {
        return FragmentWaimagesBinding.inflate(layoutInflater)
    }

    var imagesList = mutableListOf<Media>()

    companion object {
        open fun newInstance(): AllVideosFragment {
            return AllVideosFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadVideos()
    }

    private fun loadVideos() {
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            getMedia(ctx) { list ->
                if (imagesList.size != list.size) {
                    getMedia(ctx) { list ->
                        if (imagesList.size != list.size) {
                            for (media in list) {
                                if (media.isVideo) {
                                    imagesList.add(media)
                                }
                            }
                            val allMediaAdapter = AllMediaAdapter(ctx, imagesList, binding.rlMain)
                            rvWAImages.adapter = allMediaAdapter
                        }
                    }
                    val waMediaAdapter = WAMediaAdapter(ctx, imagesList)
                    rvWAImages.adapter = waMediaAdapter
                }
            }
        }
    }

    override fun onBackPressed() {
    }
}