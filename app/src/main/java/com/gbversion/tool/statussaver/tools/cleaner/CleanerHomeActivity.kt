package com.gbversion.tool.statussaver.tools.cleaner

import android.content.Intent
import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityCleanerHomeBinding
import com.gbversion.tool.statussaver.tools.BaseActivity

class CleanerHomeActivity : BaseActivity() {
    val binding by lazy { ActivityCleanerHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.appTitle.text = getString(R.string.cleaner)
            clCpuCooler.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CPUCoolerActivity::class.java))
            }
            clPhoneBooster.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clNetOptimization.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, NetOptimizeActivity::class.java))
            }
            clBatterySaver.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, BatterySaverActivity::class.java))
            }
        }
    }
}