package com.gbversion.tool.statussaver.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.FragmentHomeStatusBinding
import com.gbversion.tool.statussaver.interfaces.WATypeChangeListener
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState

class HomeStatusFragment : BaseFragment<FragmentHomeStatusBinding>(), WATypeChangeListener {
    override fun getLayout(): FragmentHomeStatusBinding {
        return FragmentHomeStatusBinding.inflate(layoutInflater)
    }

    companion object {
        open fun newInstance(): HomeStatusFragment {
            return HomeStatusFragment()
        }
    }

    private val tabTitles = arrayOf("Photos", "Videos")

    lateinit var imgFragment: WAImagesFragment
//    lateinit var vidFragment: WAVideosFragment
//    lateinit var savedFragment: WADownloadsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            if (NetworkState.isOnline()) {
                AdsUtils.loadNative(
                    ctx,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }
        }

        setupViewPager()
    }

    private fun setupViewPager() {
        binding.apply {
            imgFragment = WAImagesFragment.newInstance()
//            vidFragment = WAVideosFragment.newInstance()
//            savedFragment = WADownloadsFragment.newInstance()

            try {
                viewPagerStatus.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                viewPagerStatus.adapter = FragmentsAdapter(requireActivity())

//                TabLayoutMediator(tabLayout, viewPagerStatus) { tab, position ->
//                    tab.text = tabTitles[position]
//                }.attach()
            } catch (e: Exception) {
            }
        }
    }

    inner class FragmentsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 1
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    imgFragment
                }
                else -> imgFragment
            }
        }
    }

    override fun onBackPressed() {
        requireActivity().onBackPressed()
    }

    override fun onTypeChanged(type: Int) {
        if (type == 0)
            imgFragment.onTypeChanged(type)
        else {
            var openPicker = true
            for (uriPermission in ctx.contentResolver.persistedUriPermissions) {
                Log.e("TAG", "onTypeChanged: ${uriPermission.uri.toString()}")
                if (uriPermission.uri.toString().contains(".w4b")) {
                    openPicker = false
                    break
                }
            }

            if (!openPicker)
                imgFragment.onTypeChanged(type)
        }
    }
}