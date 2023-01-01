package com.gbversion.tool.statussaver.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityPrivacyPolicyBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class PrivacyPolicyActivity : AppCompatActivity(){
    val binding by lazy { ActivityPrivacyPolicyBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            toolbar.appTitle.text = getString(R.string.privacy_policy)

            txtPolicy.text = readPolicy()
        }
    }

    private fun readPolicy(): String {
        val inputStream = resources.openRawResource(
            resources.getIdentifier(
                "privacy_policy",
                "raw", packageName
            )
        )
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line = reader.readLine()
        val sb = StringBuilder()
        while (line != null) {
            line = reader.readLine()
            sb.append(line)
            sb.append("\n")
        }
        return sb.toString()
    }
}