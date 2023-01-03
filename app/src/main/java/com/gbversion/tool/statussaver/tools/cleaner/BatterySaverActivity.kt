package com.gbversion.tool.statussaver.tools.cleaner

import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityBatterySaverBinding
import com.gbversion.tool.statussaver.tools.BaseActivity

class BatterySaverActivity : BaseActivity() {
    val binding by lazy { ActivityBatterySaverBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.appTitle.text = getString(R.string.battery_saver)

            toolbar.imgBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}