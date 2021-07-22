package com.codingschool.ideabase.utils

import android.content.Context
import android.content.SharedPreferences
import com.codingschool.ideabase.utils.Keys.BASE_AUTH_STRING
import com.codingschool.ideabase.utils.Keys.PREF_NAME

class Preferences(
    context: Context
) {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getAuthString() = preferences.getString(BASE_AUTH_STRING, "") ?: ""

    fun setAuthString(authString: String) {
        preferences.edit().putString(Keys.BASE_AUTH_STRING,authString).apply()
    }



}

object Keys {
    const val PREF_NAME = "sharedpreferences"
    const val BASE_AUTH_STRING = "basic_auth_string"
}