package com.codingschool.ideabase.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast


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
        res,
        Toast.LENGTH_SHORT
    ).show()
}

fun Context.toast(any: Any) {
    return when (any) {
        is Int ->
            Toast.makeText(
                this,
                any,
                Toast.LENGTH_SHORT
            ).show()

        is String -> Toast.makeText(
            this,
            any,
            Toast.LENGTH_SHORT
        ).show()
        else -> Unit
    }
}

fun Context.getResString(any: Any): String {
    return when (any) {
        is Int -> this.getString(any)
        is String -> any
        else -> ""
    }
}


fun Context.showKeyboard(editText: EditText) {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInputFromWindow(
        editText.applicationWindowToken,
        InputMethodManager.SHOW_IMPLICIT, 0
    )
    editText.requestFocus()
    editText.setSelection(editText.text.length)
}

fun Context.hideKeyboard(view: View) {
    val imm: InputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


