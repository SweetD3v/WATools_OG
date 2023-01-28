package com.gbversion.tool.statussaver.ui.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.utils.setLightStatusBarColor

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val configuration: Configuration = resources.configuration
        configuration.fontScale = 1f //0.85 small size, 1 normal size, 1,15 big etc

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        configuration.densityDpi = resources.displayMetrics.xdpi.toInt()
        baseContext.resources.updateConfiguration(configuration, metrics)
        setLightStatusBarColor(this, window, R.color.white)
    }
}