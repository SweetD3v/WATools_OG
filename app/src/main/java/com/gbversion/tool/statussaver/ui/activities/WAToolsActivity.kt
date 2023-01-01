package com.gbversion.tool.statussaver.ui.activities

import android.content.Intent
import android.os.Bundle
import com.gbversion.tool.statussaver.databinding.ActivityWatoolsBinding
import com.gbversion.tool.statussaver.whatsapp_tools.wa_web.WebviewActivity
import com.whats.stickers.WAStickersActivity

class WAToolsActivity : BaseActivity() {
    val binding by lazy { ActivityWatoolsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            imgBack.setOnClickListener { onBackPressed() }

            llWAWeb.setOnClickListener {
                startActivity(Intent(this@WAToolsActivity, WebviewActivity::class.java))
            }
            llWAStickers.setOnClickListener {
                startActivity(Intent(this@WAToolsActivity, WAStickersActivity::class.java))
            }
            llDirectChat.setOnClickListener {
                startActivity(Intent(this@WAToolsActivity, DirectChatActivity::class.java))
            }
            llEmptyMsg.setOnClickListener {
                startActivity(Intent(this@WAToolsActivity, EmptySendActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}