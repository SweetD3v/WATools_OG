package com.gbversion.tool.statussaver.tools.cleaner

import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityCpuCoolerBinding
import com.gbversion.tool.statussaver.tools.BaseActivity

class CPUCoolerActivity : BaseActivity() {
    val binding by lazy { ActivityCpuCoolerBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.appTitle.text = getString(R.string.cpu_cooler)

            toolbar.imgBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}