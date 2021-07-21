package com.codingschool.ideabase.ui.login

import android.util.Base64
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.model.remote.IdeaApi
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class LoginViewModel(private val uNameFromArgs: String?, private val IdeaApi: IdeaApi) :
    BaseObservable() {

    private var view: LoginView? = null

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: LoginView) {
        this.view = view
    }

    @get:Bindable
    var username = if ((uNameFromArgs != "null")) uNameFromArgs.toString() else ""

    @get:Bindable
    var password: String = ""

    // on loading viewmodel, check if we have valid credentials in shared prefs, do chek login auto login and navigate to next screen
    // TODO
    // while loggin in, rotate progressbar!!

    fun onLoginClick() {

        if (username.isEmpty()) view?.setInputUsernameError("No empty username allowed")
        else if (!validEmail(username)) view?.setInputUsernameError("Your username should be an e-mail address")
        else if (password.isEmpty()) view?.setInputPasswordError("Please your password ")
        else {
            buildBasicAuthAndStoreInPrefs()

            // login with getOwnUser !!
            IdeaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
                //.subscribeOn(Schedulers.io())
                .subscribe({ user ->
                    view?.showToast("Current logged in user: ${user.firstname}")
                    Log.d("observer_ex", "Current logged in user: ${user.firstname}")
                }, { t ->
                    view?.showToast("there seems to be a connectivity problem")
                    Log.e("observer_ex", "getting own user: $t")
                }).addTo(compositeDisposable)
        }
    }

    // TODO could be done as completable to link these actions ?
    private fun buildBasicAuthAndStoreInPrefs() {
        val text = (username + ":" + password).toByteArray()
        //Log.d("observer_ex", "bytearray: $text")
        val auth = "Basic " + Base64.encodeToString(text, Base64.URL_SAFE)
        Log.d("observer_ex", "encoded: $auth")

        // set to shared prefs before trying login with getOwnUser
        //TODO
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