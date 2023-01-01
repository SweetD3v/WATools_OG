package com.gbversion.tool.statussaver.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.utils.setLightStatusBarColor

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBarColor(this, window, R.color.white)
    }
}