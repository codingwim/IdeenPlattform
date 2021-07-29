package com.codingschool.ideabase.utils

import android.content.Context
import android.content.SharedPreferences
import com.codingschool.ideabase.utils.Keys.BASE_AUTH_STRING
import com.codingschool.ideabase.utils.Keys.DRAFT_COMMENT
import com.codingschool.ideabase.utils.Keys.IS_MANAGER
import com.codingschool.ideabase.utils.Keys.LOCALE_STRING
import com.codingschool.ideabase.utils.Keys.PREF_NAME
import com.codingschool.ideabase.utils.Keys.USER_EMAIL
import com.codingschool.ideabase.utils.Keys.USER_FNAME
import com.codingschool.ideabase.utils.Keys.USER_ID
import com.codingschool.ideabase.utils.Keys.USER_IMAGE
import com.codingschool.ideabase.utils.Keys.USER_LNAME

class Preferences(

    context: Context
) {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences =
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getLocale() = preferences.getString(LOCALE_STRING, "en") ?: "en"
    fun getAuthString() = preferences.getString(BASE_AUTH_STRING, "") ?: ""
    fun getMyId() = preferences.getString(USER_ID, "") ?: ""
    fun isManager() = preferences.getBoolean(IS_MANAGER, false)
    fun getCommentDraft() = preferences.getString(DRAFT_COMMENT, "") ?: ""
    fun getProfileImage()  = preferences.getString(USER_IMAGE, "") ?: ""

    //fun getEmailString() = preferences.getString(USER_EMAIL, "")

    fun setLocale(localeString: String) {
        preferences.edit().putString(Keys.LOCALE_STRING, localeString).apply()
    }
    fun setAuthString(authString: String) {
        preferences.edit().putString(Keys.BASE_AUTH_STRING, authString).apply()
    }

    fun setIsManager(isManager: Boolean) {
        preferences.edit().putBoolean(Keys.IS_MANAGER, isManager).apply()
    }

    fun setCredentialID(id: String) {
        preferences.edit().putString(Keys.USER_ID, id).apply()
    }

    fun setCommentDraft(draft: String) {
        preferences.edit().putString(Keys.DRAFT_COMMENT, draft).apply()
    }
    fun setProfileImage(imageUrl: String) {
        preferences.edit().putString(USER_IMAGE, imageUrl).apply()
    }


    fun isLangEn() = getLocale().equals("en")
    /*fun setCredentialMail(mail: String) {
        preferences.edit().putString(Keys.USER_EMAIL, mail).apply()
    }

    fun setCredentialFName(fname: String) {
        preferences.edit().putString(Keys.USER_FNAME, fname).apply()
    }

    fun setCredentialLName(fname: String) {
        preferences.edit().putString(Keys.USER_LNAME, fname).apply()
    }*/


}

object Keys {
    const val PREF_NAME = "sharedpreferences"
    const val LOCALE_STRING = "local_string"
    const val BASE_AUTH_STRING = "basic_auth_string"
    const val IS_MANAGER = "is_manager"
    const val USER_ID = "user_id"
    const val USER_IMAGE = "profile_image_url"
    const val USER_FNAME = "user_fn"
    const val USER_LNAME = "user_ln"
    const val USER_EMAIL = "user_email"
    const val DRAFT_COMMENT = "saved_comment_draft"

}