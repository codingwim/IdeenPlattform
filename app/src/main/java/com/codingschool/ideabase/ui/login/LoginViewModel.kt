package com.codingschool.ideabase.ui.login

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.Credentials

class LoginViewModel(
    uNameFromArgs: String?,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: LoginView? = null

    val compositeDisposable = CompositeDisposable()

    fun init() {
        if (prefs.getAuthString().isNotEmpty()) {
            checkCredentialsWithAPI()
        }
    }

    fun attachView(view: LoginView) {
        this.view = view
    }

    @get:Bindable
    var username = if ((uNameFromArgs != "null")) uNameFromArgs.toString() else ""

    @get:Bindable
    var password: String = ""

    fun onLoginClick() {

        if (username.isEmpty()) view?.setInputUsernameError(R.string.error_empty_username)
        else if (!validEmail(username)) view?.setInputUsernameError(R.string.error_not_email_adress)
        else if (password.isEmpty()) view?.setInputPasswordError(R.string.error_empty_password)
        else {
            // fields are valid, build the encoded base Auth token string, put in prefs
            buildBasicAuthAndStoreInPrefs()
            // with getOwnUser we can check credentials, will automatically use base Auth token from preferences !
            checkCredentialsWithAPI()
        }
    }

    private fun checkCredentialsWithAPI() {
        ideaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
                prefs.setCredentialID(user.id)
                prefs.setIsManager(user.isManager)
                if (user.profilePicture != null) {
                    prefs.setProfileImage(user.profilePicture)
                    view?.navigateToTopRankedFragment()
                } else view?.showSetProfilePictureDialog()

            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    when {
                        responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        ) -> {
                            view?.setInputUsernameError("")
                            view?.setInputPasswordError("")
                            view?.showToast(R.string.worng_login_credentials_message)
                        }
                        responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        ) -> {
                            prefs.setCommentDraft("")
                            view?.showToast(R.string.error_pwd_user_not_valid)
                        }
                        else -> view?.showToast(R.string.network_issue_check_network)
                    }
                }
                Log.e("IdeaBase_log", "exception adding new user: $t")
            }).addTo(compositeDisposable)
    }

    private fun buildBasicAuthAndStoreInPrefs() {
        val authString: String = Credentials.basic(username, password)
        prefs.setAuthString(authString)
    }

    fun onRegisterClick() {
        view?.navigateToRegisterFragment()
    }

    fun onSetPictureNow() {
        // navigate to editProfile AND open image selector
        view?.navigateToEditProfileFragmentAndLoadPictureSelector()

    }
    fun onUsernameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before==0)) view?.resetUsernameError()
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before==0)) view?.resetPasswordError()
    }

    private fun validEmail(email: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}