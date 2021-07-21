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

   /* fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
        val key = pair.first
        val value = pair.second
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
    }*/
}

object Keys {
    const val PREF_NAME = "sharedpreferences"
    const val BASE_AUTH_STRING = "basic_auth_string"
}