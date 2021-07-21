package com.codingschool.ideabase.utils

import android.content.Context
import android.widget.Toast
import com.codingschool.ideabase.MyApplication

fun Context.toast(text: String) {
    Toast.makeText(
        this,
        text,
        Toast.LENGTH_SHORT
    ).show()
}


fun Context.toast(res: Int) {
    Toast.makeText(
        this,
        this.getString(res),
        Toast.LENGTH_SHORT
    ).show()
}

fun Context.toast(any: Any) {
    return when (any) {
        is Int -> this.toast(any)
        is String -> this.toast(any)
        else -> Unit
    }
}

fun Context.getResString(res: Int): String {
    return this.getString(res)
}
