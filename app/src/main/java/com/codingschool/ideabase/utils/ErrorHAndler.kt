package com.codingschool.ideabase.utils

import android.content.Context
import android.util.Log
import com.codingschool.ideabase.R

fun Context.errorHandler(errorMessage: String?): Boolean {
    var errorHandled = false
    errorMessage?.let {
        if (errorMessage.contains(
                "HTTP 400",
                ignoreCase = true
            )
        ) {
            this.toast(R.string.parameter_missing_message)
            errorHandled = true
        }
        else if (errorMessage.contains(
                "HTTP 401",
                ignoreCase = true
            )
        ) {
            this.toast(R.string.not_authorized)
            errorHandled = true
        } else if (errorMessage.contains(
                "HTTP 403",
                ignoreCase = true
            )
        ) {
            this.toast((R.string.idea_released_mot_editable_error))
            errorHandled = true
        } else if (errorMessage.contains(
                "HTTP 404",
                ignoreCase = true
            )
        ) {
            toast(R.string.idea_not_found_message)
            errorHandled = true
        }
    }
    return errorHandled
}
