package com.gbversion.tool.statussaver.tools.apis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface APIService {

    @GET("api/facebook/video")
    fun getMediaUrlFacebook(
        @Header("X-RapidAPI-Key") rapid_key: String,
        @Header("X-RapidAPI-Host") rapid_host: String,
        @Query("video_link") videoLink: String
    ): Call<FBModel>

    @GET("index")
    fun getMediaUrlInstagram(
        @Header("X-RapidAPI-Key") rapid_key: String,
        @Header("X-RapidAPI-Host") rapid_host: String,
        @Query("url") mediaLink: String
    ): Call<InstaModel>
}