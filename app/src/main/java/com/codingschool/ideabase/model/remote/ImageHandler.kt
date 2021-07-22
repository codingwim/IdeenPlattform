package com.codingschool.ideabase.model.remote

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
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

    fun getProfilePic(uri: String, view: ImageView) {
        picasso
            .setIndicatorsEnabled(true)
        picasso
            .load(uri)
            .placeholder(R.drawable.ic_baseline_person_24)
            .error(R.drawable.ic_baseline_emoji_objects_24)
            .into(view)
    }

    fun getIdeaImage(uri: String, view: ImageView) {
        picasso
            .setIndicatorsEnabled(true)
        picasso
            .load(uri)
            .placeholder(R.drawable.placeholder_idea_image)
            .error(R.drawable.placeholder_idea_image_not_found)
            .into(view)
    }


}