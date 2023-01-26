package com.gbversion.tool.statussaver.remote_config

class RemoteConfigUtils {
    companion object {
        private var admobData: FireAdModel? = null


        fun adIdInterstital(): String {
            return "/22686075355/com.gbversion.tool.statussaver.interstitial"
//            return "ca-app-pub-3940256099942544/8691691433"
//            return "ca-app-pub-3940256099942544/1033173712"
        }

        fun adIdBanner(): String {
            return "/22686075355/com.gbversion.tool.statussaver.banner"
//            return "ca-app-pub-3940256099942544/6300978111"
        }

        fun adIdNative(): String {
            return "/22686075355/com.gbversion.tool.statussaver.native"
//            return "ca-app-pub-3940256099942544/2247696110"
        }

        fun adIdAppOpen(): String {
            return "/22686075355/com.gbversion.tool.statussaver.openapp"
//            return "ca-app-pub-3940256099942544/3419835294"
        }
    }
}