package com.codingschool.ideabase.utils

import android.content.Context
import android.widget.ImageView
import com.codingschool.ideabase.R
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient

class ImageHandler(
    context: Context,
    okHttpClient: OkHttpClient
) {
    private val appContext = context.applicationContext

    private val picasso: Picasso = Picasso.Builder(appContext)
        .downloader(OkHttp3Downloader(okHttpClient))
        .build()

    fun getProfilePic(url: String?, view: ImageView) {
        val uriOrDrawable = url ?: "R.drawable.ic_baseline_person_24"
        picasso
            .load(uriOrDrawable)
            .resize(240,240)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_person_24)
            .error(R.drawable.ic_baseline_person_outline_24)
            .into(view)
    }

    fun getIdeaImage(uri: String, view: ImageView) {
        val uriOrDrawable = if (uri.isNotEmpty()) uri else "R.drawable.image_placeholder_480_360"
        picasso
            .load(uriOrDrawable)
            .resize(480,360)
            .placeholder(R.drawable.image_placeholder_480_360)
            .error(R.drawable.image_not_found)
            .into(view)
    }

}