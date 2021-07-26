package com.codingschool.ideabase.utils

import android.content.Context
import android.content.SharedPreferences
import com.codingschool.ideabase.utils.Keys.BASE_AUTH_STRING
import com.codingschool.ideabase.utils.Keys.MY_ID
import com.codingschool.ideabase.utils.Keys.PREF_NAME
import com.codingschool.ideabase.utils.Keys.USER_EMAIL
import com.codingschool.ideabase.utils.Keys.USER_FNAME
import com.codingschool.ideabase.utils.Keys.USER_ID
import com.codingschool.ideabase.utils.Keys.USER_IS_MANAGER
import com.codingschool.ideabase.utils.Keys.USER_LNAME

class Preferences(

context: Context
) {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    fun getEmailString() = preferences.getString(USER_EMAIL, "") ?: ""
    fun getAuthString() = preferences.getString(BASE_AUTH_STRING, "") ?: ""

    fun setAuthString(authString: String) {
        preferences.edit().putString(Keys.BASE_AUTH_STRING,authString).apply()
    }



    fun setCredentialMail(){
        preferences.edit().putString(Keys.BASE_AUTH_STRING, USER_EMAIL).apply()
    }
    fun setCredentialFName(){
        preferences.edit().putString(Keys.PREF_NAME, USER_FNAME).apply()
    }
    fun setCredentialLName(){
        preferences.edit().putString(Keys.BASE_AUTH_STRING, USER_LNAME).apply()
    }
    fun setCredentialID(){
        preferences.edit().putString(Keys.BASE_AUTH_STRING, USER_ID).apply()
    }
}

object Keys {
    const val PREF_NAME = "sharedpreferences"
    const val BASE_AUTH_STRING = "basic_auth_string"
    const val USER_ID = "user_id"
    const val USER_FNAME = "user_fn"
    const val USER_LNAME = "user_ln"
    const val USER_EMAIL = "user_email"

}