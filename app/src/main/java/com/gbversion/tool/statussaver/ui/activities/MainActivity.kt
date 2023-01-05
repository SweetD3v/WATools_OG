package com.gbversion.tool.statussaver.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.gbversion.tool.statussaver.databinding.ActivityMainBinding
import com.gbversion.tool.statussaver.phone_booster.app_utils.batteryPerms
import com.gbversion.tool.statussaver.phone_booster.app_utils.getAllAppsPermissions
import com.internet.speed_meter.SpeedMeterService

class MainActivity : BaseActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val speedMeterIntent =
                    Intent(this@MainActivity, SpeedMeterService::class.java)
                startService(speedMeterIntent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            } else {
//                val speedMeterIntent =
//                    Intent(this@MainActivity, SpeedMeterService::class.java)
//                startService(speedMeterIntent)
//            }
//        } else {
        val speedMeterIntent =
            Intent(this@MainActivity, SpeedMeterService::class.java)
        startService(speedMeterIntent)
//        }

        var permissions = getAllAppsPermissions(this)
        permissions = ArrayList(permissions.filter {
            it.permissions.contains(batteryPerms[0])
                    || it.permissions.contains(batteryPerms[1])
                    || it.permissions.contains(batteryPerms[2])
                    || it.permissions.contains(batteryPerms[3])
        })
        for (permission in permissions) {
            Log.e(
                "TAGApp",
                "App Name : ${permission.appName} -> Is Sensitive: ${permission.isSensitive}"
            )
        }
    }

//    var doubleBackToExitPressedOnce = false
//    override fun onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            finish()
//            return
//        }
//
//        this.doubleBackToExitPressedOnce = true
//        Snackbar.make(binding.root, "Press BACK again to exit", Snackbar.LENGTH_SHORT).show()
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            doubleBackToExitPressedOnce = false
//        }, 2000)
//    }

    override fun onBackPressed() {
        startActivity(Intent(this, ExitActivity::class.java))
        finish()
    }
}