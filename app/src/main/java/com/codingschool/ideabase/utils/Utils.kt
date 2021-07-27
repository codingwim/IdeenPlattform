package com.codingschool.ideabase.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.codingschool.ideabase.MyApplication
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException


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

fun Context.getResString(any: Any): String {
    return when (any) {
        is Int -> this.getString(any)
        is String -> any
        else -> ""
    }
}
class InputStreamRequestBody(
    private val contentType: MediaType,
    private val contentResolver: ContentResolver,
    private val uri: Uri
) : RequestBody() {
    override fun contentType() = contentType

    override fun contentLength(): Long = -1

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val input = contentResolver.openInputStream(uri)

        input?.use { sink.writeAll(it.source()) }
            ?: throw IOException("Could not open $uri")
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

