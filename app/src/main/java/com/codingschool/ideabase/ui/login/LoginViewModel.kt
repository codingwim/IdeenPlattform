package com.codingschool.ideabase.ui.login

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.Credentials

class LoginViewModel(
    private val uNameFromArgs: String?,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: LoginView? = null

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        if (prefs.getAuthString().isNotEmpty()) {
            // on loading viewmodel, check if we have valid credentials in shared prefs, do chek login auto login and navigate to next screen
            // TODO
            // while "checking" in, rotate progressbar with info text!!
            // nice welcome screen like the beer screen, with progress indicator while also loading idealists!!!!
            checkCredentialsWithAPI()
            Log.d("observer_ex", "prefs not empty")
        } else Log.d("observer_ex", "prefs empty")
    }

    fun attachView(view: LoginView) {
        this.view = view

    }

    @get:Bindable
    var username = if ((uNameFromArgs != "null")) uNameFromArgs.toString() else ""

    @get:Bindable
    var password: String = ""

    fun onLoginClick() {

        if (username.isEmpty()) view?.setInputUsernameError("No empty username allowed")
        else if (!validEmail(username)) view?.setInputUsernameError("Your username should be an e-mail address")
        else if (password.isEmpty()) view?.setInputPasswordError("Please your password ")
        else {
            // fields are valid, letz build the encoded base Auth string, and try to "login" get own user data
            buildBasicAuthAndStoreInPrefs()

            // with getOwnUser we can check credentials, will automatically use auth token from preferences !
            checkCredentialsWithAPI()
        }
    }

    private fun checkCredentialsWithAPI() {
        ideaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
            //.subscribeOn(Schedulers.io())
            .subscribe({ user ->
                view?.showToast("Hi ${user.firstname}, welcome back!")
                // TODO we could put the users firstname, etc in sharedprefs if we need to
                view?.navigateToTopRankedFragment()
                //Log.d("observer_ex", "Current logged in user: ${user.firstname}")
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        view?.setInputUsernameError("")
                        view?.setInputPasswordError("")
                        view?.showToast("This username/password combination is not valid")
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) view?.showToast("You are not autorized to log in")
                    else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception adding new user: $t")
            }).addTo(compositeDisposable)
    }

    // TODO could be done as completable to link these actions ?
    private fun buildBasicAuthAndStoreInPrefs() {
        val text = (username + ":" + password).toByteArray()
        val test : String = Credentials.basic(username, password)
        val authString = "Basic " + Base64.encodeToString(text, Base64.NO_WRAP)
        //Log.d("observer_ex", "encoded: $authString")
        prefs.setAuthString(test)
    }

    fun onRegisterClick() {
        view?.navigateToRegisterFragment()
    }

    fun onUsernameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetUsernameError()
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0) view?.resetPasswordError()
    }

    private fun validEmail(email: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}