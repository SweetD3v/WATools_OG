package com.gbversion.tool.statussaver.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivitySendEmptyBinding
import java.net.URLEncoder

class EmptySendActivity : BaseActivity() {

    val binding by lazy { ActivitySendEmptyBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            btnSend.setOnClickListener {
                if (edtNumber.text.toString().trim().isNotEmpty()
                    && edtLines.text.toString().trim().isNotEmpty()
                ) {
                    var toCode = ccpPhone.selectedCountryCode() // contains spaces.

                    toCode = toCode.replace("+", "").replace(" ", "")

                    val phoneNo = toCode + edtNumber.text.toString().replace(" ", "")
                    val lines = edtLines.text.toString().toInt()

                    val sb = StringBuilder("")
                    for (i in 0 until lines) {
                        sb.append(getString(R.string.blank_massage))
                        if (i != (lines - 1)) {
                            sb.append("\n")
                        }
                    }

                    shareBlankWhatsApp(phoneNo, sb.toString())
                } else {
                    Toast.makeText(
                        this@EmptySendActivity,
                        "Both fields are mandatory.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun shareBlankWhatsApp(phoneNo: String, message: String) {
        try {
            val sendIntent = Intent(Intent.ACTION_MAIN)
//            sendIntent.component = ComponentName(
//                "com.whatsapp",
//                "com.whatsapp.Conversation"
//            )
//            sendIntent.putExtra("jid", "$phoneNo@s.whatsapp.net")
            val url =
                "https://api.whatsapp.com/send?phone=$phoneNo&text=" + URLEncoder.encode(
                    message,
                    "UTF-8"
                )
//            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendIntent.action = Intent.ACTION_VIEW
            sendIntent.setPackage("com.whatsapp")
            sendIntent.data = Uri.parse(url)
//            sendIntent.type = "text/plain"
            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(sendIntent)
            } else {
//                sendIntent.component = ComponentName(
//                    "com.whatsapp.w4b",
//                    "com.whatsapp.Conversation"
//                )
                sendIntent.setPackage("com.whatsapp.w4b")
                if (sendIntent.resolveActivity(packageManager) != null) {
                    startActivity(sendIntent)
                } else {
                    Toast.makeText(
                        this@EmptySendActivity,
                        "Whatsapp not installed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
        }
    }
}