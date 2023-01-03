package com.gbversion.tool.statussaver.tools.cleaner

import android.os.Bundle
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhoneBoosterBinding
import com.gbversion.tool.statussaver.tools.BaseActivity

class PhoneBoosterActivity : BaseActivity() {
    val binding by lazy { ActivityPhoneBoosterBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.appTitle.text = getString(R.string.phone_booster)

            toolbar.imgBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}