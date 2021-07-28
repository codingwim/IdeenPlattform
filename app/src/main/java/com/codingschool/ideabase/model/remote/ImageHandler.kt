package com.codingschool.ideabase.model.remote

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

    val picasso = Picasso.Builder(appContext)
        .downloader(OkHttp3Downloader(okHttpClient))
        .build()
    fun getProfilePic(url: String?, view: ImageView) {
        val uriOrDrawable = if (url != null) url else "R.drawable.ic_baseline_person_24"

        picasso
            .setIndicatorsEnabled(true)
        picasso
            .load(uriOrDrawable)
            .resize(240,240)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_person_24)
            .error(R.drawable.ic_baseline_person_outline_24)
            .into(view)
    }

    fun getIdeaImage(uri: String, view: ImageView) {
        val uriOrDrawable = if (uri.isNotEmpty()) uri else "R.drawable.image_not_set480_360"

        picasso
            .setIndicatorsEnabled(true)
        picasso
            .load(uriOrDrawable)
            .resize(480,360)
            .placeholder(R.drawable.image_not_set480_360)
            .error(R.drawable.image_loading480_360)
            .into(view)
    }

}