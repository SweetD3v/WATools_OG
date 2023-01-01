package com.gbversion.tool.statussaver.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.gbversion.tool.statussaver.adapter.AllMediaAdapter
import com.gbversion.tool.statussaver.databinding.FragmentWaimagesBinding
import com.gbversion.tool.statussaver.models.Media
import com.gbversion.tool.statussaver.utils.dpToPx
import com.gbversion.tool.statussaver.utils.getMedia
import com.gbversion.tool.statussaver.widgets.MarginItemDecoration


class AllImagesFragment : BaseFragment<FragmentWaimagesBinding>() {
    override fun getLayout(): FragmentWaimagesBinding {
        return FragmentWaimagesBinding.inflate(layoutInflater)
    }

    var imagesList = mutableListOf<Media>()

    companion object {
        open fun newInstance(): AllImagesFragment {
            return AllImagesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadImages()
    }

    private fun loadImages() {
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            getMedia(ctx) { list ->
                if (imagesList.size != list.size) {
                    for (media in list) {
                        Log.e("TAG", "mediaPath: ${media.path}")
                        if (!media.isVideo && !media.path.contains("Insta Grid", true)) {
                            imagesList.add(media)
                        }
                    }
                    val allMediaAdapter = AllMediaAdapter(ctx, imagesList, binding.rlMain)
                    rvWAImages.adapter = allMediaAdapter
                }
            }
        }
    }

    override fun onBackPressed() {
    }
}