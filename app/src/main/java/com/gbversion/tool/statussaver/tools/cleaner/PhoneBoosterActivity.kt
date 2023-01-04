package com.gbversion.tool.statussaver.tools.cleaner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhoneBoosterBinding
import com.gbversion.tool.statussaver.tools.BaseActivity

class PhoneBoosterActivity : BaseActivity() {
    val binding by lazy { ActivityPhoneBoosterBinding.inflate(layoutInflater) }

    var handler: Handler? = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            appTitle.text = getString(R.string.phone_booster)

            imgBack.setOnClickListener { onBackPressed() }

            handler?.postDelayed({
                motionLayout.transitionToEnd()
            }, 5000)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}