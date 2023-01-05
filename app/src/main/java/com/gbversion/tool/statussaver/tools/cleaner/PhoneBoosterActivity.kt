package com.gbversion.tool.statussaver.tools.cleaner

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPhoneBoosterBinding
import com.gbversion.tool.statussaver.tools.BaseActivity
import com.gbversion.tool.statussaver.utils.AppUtils.Companion.CLEANER_TYPE
import com.gbversion.tool.statussaver.utils.gone
import com.gbversion.tool.statussaver.utils.visible

class PhoneBoosterActivity : BaseActivity() {
    val binding by lazy { ActivityPhoneBoosterBinding.inflate(layoutInflater) }

    var handler: Handler? = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            imgBack.setOnClickListener { onBackPressed() }

            animMain.rotation = 0f

            selectToolType(CLEANER_TYPE)

            when (CLEANER_TYPE) {
                0 -> {
                    appTitle.text = getString(R.string.cleaner)
                    animMain.setAnimation(R.raw.cleaner_home1)
                }
                1 -> {
                    appTitle.text = getString(R.string.battery_saver)
                    animMain.setAnimation(R.raw.battery_saver)
                }
                2 -> {
                    appTitle.text = getString(R.string.network_optimization)
                    animMain.setAnimation(R.raw.net_opt)
                }
                3 -> {
                    appTitle.text = getString(R.string.phone_booster)
                    animMain.setAnimation(R.raw.phone_booster)
                    animMain.rotation = -45f
                }
                4 -> {
                    appTitle.text = getString(R.string.cpu_cooler)
                    animMain.setAnimation(R.raw.cpu_cooler)
                }
            }

            clCleaner.setOnClickListener {
                CLEANER_TYPE = 0
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            clBatterySaver.setOnClickListener {
                CLEANER_TYPE = 1
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            clNetOptimization.setOnClickListener {
                CLEANER_TYPE = 2
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            clPhoneBooster.setOnClickListener {
                CLEANER_TYPE = 3
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }
            clCpuCooler.setOnClickListener {
                CLEANER_TYPE = 4
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            handler?.postDelayed({
                motionLayout.transitionToEnd()
            }, 5000)
        }
    }

    private fun selectToolType(cleanerType: Int) {
        binding.run {
            when (cleanerType) {
                0 -> {
                    clBatterySaver.visible()
                    clNetOptimization.visible()
                    clPhoneBooster.visible()
                    clCleaner.gone()
                    clBatterySaver.visible()
                }
                1 -> {
                    clBatterySaver.gone()
                    clNetOptimization.visible()
                    clPhoneBooster.visible()
                    clCleaner.visible()
                    clCpuCooler.visible()
                }
                2 -> {
                    clBatterySaver.visible()
                    clNetOptimization.gone()
                    clPhoneBooster.visible()
                    clCleaner.visible()
                    clBatterySaver.visible()
                }
                3 -> {
                    clBatterySaver.visible()
                    clNetOptimization.visible()
                    clPhoneBooster.gone()
                    clCleaner.visible()
                    clBatterySaver.visible()
                }
                4 -> {
                    clBatterySaver.visible()
                    clNetOptimization.visible()
                    clPhoneBooster.visible()
                    clCleaner.visible()
                    clCpuCooler.gone()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
