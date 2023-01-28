package com.gbversion.tool.statussaver.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityHomeStatusBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity
import com.gbversion.tool.statussaver.ui.fragments.HomeStatusFragment
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.gbversion.tool.statussaver.utils.gone
import com.gbversion.tool.statussaver.utils.visible
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class HomeStatus_Activity : AppCompatActivity() {
    val binding by lazy { ActivityHomeStatusBinding.inflate(layoutInflater) }

    var permissionsList = mutableListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                onPermissionGranted()
            } else {
                showPermissionDialog()
            }
        }

    protected fun allPermissionsGranted() = permissionsList.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.run {

            if (NetworkState.isOnline()) {
                AdsUtils.loadNativeSmallProgress(
                    this@HomeStatus_Activity,
                    RemoteConfigUtils.adIdNative(),
                    binding.adFrame,
                    binding.adProgress
                )
            }

            binding.run {
                imgBack.setOnClickListener {
                    onBackPressed()
                }

                btnPermission.setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        askForPermission()
                    }
                }

                imgDownloads.setOnClickListener {
                    startActivity(
                        Intent(
                            this@HomeStatus_Activity,
                            MyCreationToolsActivity::class.java
                        ).apply {
                            putExtra(MyCreationToolsActivity.CREATION_TYPE, "wa_status")
                        }
                    )
                }
            }
        }
    }

    var permissionDialog: AlertDialog? = null

    fun showPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required")
            .setCancelable(false)
            .setMessage("Some permissions are needed to be allowed to use this app without any problems.")
            .setPositiveButton("Settings") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package", packageName,
                    null
                )
                intent.data = uri
                startActivity(intent)
            }
        if (permissionDialog == null)
            permissionDialog = builder.create()
        if (permissionDialog?.isShowing == false) {
            permissionDialog?.show()
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (allPermissionsGranted()) {
                binding.btnPermission.gone()
                onPermissionGranted()
            } else {
                binding.btnPermission.visible()
            }
        } else {
            onPermissionGranted()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun askForPermission() {
        Log.e(
            "TAG",
            "askForPermission: ${shouldShowRequestPermissionRationale(permissionsList[0])}",
        )
        if (!allPermissionsGranted()) {
            if (shouldShowRequestPermissionRationale(permissionsList[0])) {
                permissionRequest.launch(permissionsList.toTypedArray())
            } else {
                showPermissionDialog()
            }
        }
    }

    fun onPermissionGranted() {
        binding.btnPermission.gone()
        Log.e("TAG", "onPermissionGranted: ${supportFragmentManager.fragments.size}")
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeStatusFragment.newInstance())
                .commit()
        }
    }

    var interstitialAd: InterstitialAd? = null
    var failedToLoad = false

    fun loadInterstitialAd(
        activity: Activity,
        adId: String
    ) {
        if (!NetworkState.isOnline()) {
            return
        }

        InterstitialAd.load(
            activity,
            adId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    failedToLoad = false
                    interstitialAd = ad
//                    interstitialAd?.fullScreenContentCallback = object :
//                        FullScreenContentCallback() {
//                        override fun onAdShowedFullScreenContent() {
//
//                        }
//
//                        override fun onAdDismissedFullScreenContent() {
//                            startActivity(intent)
//                        }
//
//                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                            startActivity(intent)
//                        }
//                    }
                    Log.e("TAG", "onAdLoaded: ${interstitialAd}")
                }

                override fun onAdFailedToLoad(ad: LoadAdError) {
                    failedToLoad = true
                    Log.e("TAG", "adException: ${ad.responseInfo}")
                }
            })
    }


    fun showFullScreenAd() {
        if (failedToLoad) {
            continueExec()
            return
        }
        Log.e("TAG", "showFullScreenAd: ${interstitialAd}")
        interstitialAd?.let {
            it.fullScreenContentCallback = object :
                FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {

                }

                override fun onAdDismissedFullScreenContent() {

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                }
            }
            continueExec()
            it.show(this@HomeStatus_Activity)
        } ?: let {
            failedToLoad = true
            if (NetworkState.isOnline()) {
                loadInterstitialAd(this@HomeStatus_Activity, RemoteConfigUtils.adIdInterstital())
                val pd = AdsUtils.ProgressDialogMine()
                pd.showDialog(this@HomeStatus_Activity, "Please wait...", false)
                Handler(Looper.getMainLooper()).postDelayed({
                    pd.dismissDialog()
                    showFullScreenAd()
                }, 3000)
            } else continueExec()
        }
    }

    fun continueExec() {
        finish()
    }

    override fun onBackPressed() {
        finish()
    }
}