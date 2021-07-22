package com.codingschool.ideabase.model.remote

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient


class ImageHandler(
    context: Context,
    okHttpClient: OkHttpClient
) {
    private val appContext = context.applicationContext
    val picasso = Picasso.Builder(appContext)
        .downloader(OkHttp3Downloader(okHttpClient))
        .build()

    fun getImage(uri: String, view: ImageView) {l
        picasso
            .setIndicatorsEnabled(true)
        picasso
            .load(uri)
            .fit()
            .into(view)
    }


}