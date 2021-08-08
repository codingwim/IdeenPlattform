package com.codingschool.ideabase.utils

import android.content.Context
import com.codingschool.ideabase.R

fun Context.errorHandler(errorMessage: String?): Boolean {
    var noInternet = false
    if (errorMessage != null) {
        val toastMessage = with(errorMessage) {
            when {
                contains(
                    "HTTP 400",
                    ignoreCase = true
                ) -> R.string.parameter_missing_message
                contains(
                    "HTTP 401",
                    ignoreCase = true
                ) -> R.string.not_authorized
                contains(
                    "HTTP 403",
                    ignoreCase = true
                ) -> R.string.idea_released_mot_editable_error
                contains(
                    "HTTP 404",
                    ignoreCase = true
                ) -> R.string.idea_not_found_message
                contains(
                    "HTTP 409",
                    ignoreCase = true
                ) -> R.string.email_already_inuse_input_error
                contains(
                    "2 exceptions occurred",
                    ignoreCase = true
                ) -> R.string.idea_released_mot_editable_error
                else -> {
                    noInternet = true
                    ""
                }
            }
        }
        if (toastMessage != "") toast(toastMessage)
    } else noInternet = true
    return noInternet
}

