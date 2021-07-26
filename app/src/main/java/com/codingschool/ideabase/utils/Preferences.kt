package com.codingschool.ideabase.utils

import android.content.Context
import android.content.SharedPreferences
import com.codingschool.ideabase.utils.Keys.BASE_AUTH_STRING
import com.codingschool.ideabase.utils.Keys.MY_ID
import com.codingschool.ideabase.utils.Keys.PREF_NAME
import com.codingschool.ideabase.utils.Keys.USER_IS_MANAGER

class Preferences(
    context: Context
) {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getAuthString() = preferences.getString(BASE_AUTH_STRING, "") ?: ""

    fun setAuthString(authString: String) {
        preferences.edit().putString(Keys.BASE_AUTH_STRING,authString).apply()
    }

    fun getMyId() = preferences.getString(MY_ID, "") ?: ""

    fun setMyId(id: String) {
        preferences.edit().putString(MY_ID,id).apply()
    }

    fun isManager() = preferences.getBoolean(USER_IS_MANAGER, false)

    fun setManager(isManager: Boolean) {
        preferences.edit().putBoolean(USER_IS_MANAGER,isManager).apply()
    }



}

object Keys {
    const val PREF_NAME = "sharedpreferences"
    const val BASE_AUTH_STRING = "basic_auth_string"
    const val MY_ID = "logged_in_user_id"
    const val USER_IS_MANAGER = "is_user_manager"
}