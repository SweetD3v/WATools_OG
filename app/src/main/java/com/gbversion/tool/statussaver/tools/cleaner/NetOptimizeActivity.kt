package com.gbversion.tool.statussaver.tools.cleaner

import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityNetOptimizationBinding
import com.gbversion.tool.statussaver.tools.BaseActivity

class NetOptimizeActivity : BaseActivity() {
    val binding by lazy { ActivityNetOptimizationBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.appTitle.text = getString(R.string.network_optimization)

            toolbar.imgBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}