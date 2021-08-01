package com.codingschool.ideabase.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.codingschool.ideabase.utils.Keys.APP_STARTED
import com.codingschool.ideabase.utils.Keys.BASE_AUTH_STRING
import com.codingschool.ideabase.utils.Keys.DRAFT_CATEGORY
import com.codingschool.ideabase.utils.Keys.DRAFT_COMMENT
import com.codingschool.ideabase.utils.Keys.DRAFT_DESCRIPTION
import com.codingschool.ideabase.utils.Keys.DRAFT_IDEA_SAVED
import com.codingschool.ideabase.utils.Keys.DRAFT_IMAGE
import com.codingschool.ideabase.utils.Keys.DRAFT_NAME
import com.codingschool.ideabase.utils.Keys.IS_MANAGER
import com.codingschool.ideabase.utils.Keys.LAST_ADAPTER_UPDATE
import com.codingschool.ideabase.utils.Keys.LOCALE_STRING
import com.codingschool.ideabase.utils.Keys.PREF_NAME
import com.codingschool.ideabase.utils.Keys.TOP_RANKED_IDS
import com.codingschool.ideabase.utils.Keys.USER_EMAIL
import com.codingschool.ideabase.utils.Keys.USER_FNAME
import com.codingschool.ideabase.utils.Keys.USER_ID
import com.codingschool.ideabase.utils.Keys.USER_IMAGE
import com.codingschool.ideabase.utils.Keys.USER_LNAME
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import java.time.LocalDate
import java.time.LocalDateTime.now
import java.time.ZoneOffset
import java.util.*

class Preferences(

    context: Context
) {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences =
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getLocale() = preferences.getString(LOCALE_STRING, "en") ?: "en"
    fun appJustStarted() = preferences.getBoolean(APP_STARTED, true)
    fun getLastAdapterUpdate() = preferences.getString(LAST_ADAPTER_UPDATE, "") ?: ""
    fun getAuthString() = preferences.getString(BASE_AUTH_STRING, "") ?: ""
    fun getMyId() = preferences.getString(USER_ID, "") ?: ""
    fun isManager() = preferences.getBoolean(IS_MANAGER, false)
    fun getCommentDraft() = preferences.getString(DRAFT_COMMENT, "") ?: ""
    fun hasSavedIdeaDraft() = preferences.getBoolean(DRAFT_IDEA_SAVED, false)
    fun getProfileImage()  = preferences.getString(USER_IMAGE, "") ?: ""
    fun getImageDraft() = preferences.getString(DRAFT_IMAGE, "") ?: ""
    fun getIdeaNameDraft() = preferences.getString(DRAFT_NAME, "") ?: ""
    fun getIdeaDescriptionDraft() = preferences.getString(DRAFT_DESCRIPTION, "") ?: ""
    fun getIdeaCategoryDraft() = preferences.getString(DRAFT_CATEGORY, "") ?: ""
    fun getTopRankedIds(): List<String> {
        val idsAsString = preferences.getString(TOP_RANKED_IDS, "") ?: ""
        val result: List<String> = idsAsString.split(",").map { it.trim() }
        return result
    }



    //fun getEmailString() = preferences.getString(USER_EMAIL, "")

    fun setLocale(localeString: String) {
        preferences.edit().putString(LOCALE_STRING, localeString).apply()
    }
    fun setAuthString(authString: String) {
        preferences.edit().putString(BASE_AUTH_STRING, authString).apply()
    }

    fun setIsManager(isManager: Boolean) {
        preferences.edit().putBoolean(IS_MANAGER, isManager).apply()
    }

    fun setCredentialID(id: String) {
        preferences.edit().putString(USER_ID, id).apply()
    }

    fun setCommentDraft(draft: String) {
        preferences.edit().putString(DRAFT_COMMENT, draft).apply()
    }

    fun setIdeaDraftSaved(saved: Boolean) {
        preferences.edit().putBoolean(DRAFT_IDEA_SAVED, saved).apply()
    }
    fun setProfileImage(imageUrl: String) {
        preferences.edit().putString(USER_IMAGE, imageUrl).apply()
    }

    fun setImageDraft(draft: String) {
        preferences.edit().putString(DRAFT_IMAGE, draft).apply()
    }

    fun setIdeaNameDraft(draft: String) {
        preferences.edit().putString(DRAFT_NAME, draft).apply()
    }

    fun setIdeaDescriptionDraft(draft: String) {
        preferences.edit().putString(DRAFT_DESCRIPTION, draft).apply()
    }

    fun setIdeaCategoryDraft(draft: String) {
        preferences.edit().putString(Keys.DRAFT_CATEGORY, draft).apply()
    }

    fun isLangEn() = getLocale().equals("en")
    fun setAppNotJustStarted() {
        preferences.edit().putBoolean(APP_STARTED, false).apply()
    }

    fun setAppJustStarted() {
        preferences.edit().putBoolean(APP_STARTED, true).apply()
    }

    // todo check timezone problems if phone in other timezome
    fun setLastAdapterUpdateNow() {
        val currentDateTime = ZonedDateTime.now().withZoneSameInstant(org.threeten.bp.ZoneOffset.UTC).toString()
        preferences.edit().putString(LAST_ADAPTER_UPDATE, currentDateTime).apply()
    }

    fun setTopRankedIds(list: List<String>) {
        preferences.edit().putString(Keys.TOP_RANKED_IDS, list.joinToString()).apply()
    }
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
    const val APP_STARTED = "app_just_started"
    const val LAST_ADAPTER_UPDATE = "timestamp_last_adapter_update"
    const val LOCALE_STRING = "local_string"
    const val BASE_AUTH_STRING = "basic_auth_string"
    const val IS_MANAGER = "is_manager"
    const val USER_ID = "user_id"
    const val USER_IMAGE = "profile_image_url"
    const val USER_FNAME = "user_fn"
    const val USER_LNAME = "user_ln"
    const val USER_EMAIL = "user_email"
    const val DRAFT_IDEA_SAVED = "idea_draft_saved"
    const val DRAFT_COMMENT = "saved_comment_draft"
    const val DRAFT_IMAGE = "saved_idea_url"
    const val DRAFT_NAME = "saved_idea_name"
    const val DRAFT_DESCRIPTION = "saved_idea_description"
    const val DRAFT_CATEGORY = "saved_idea"
    const val TOP_RANKED_IDS ="ordered_top_ranked_ids"


}