package com.codingschool.ideabase.ui.login

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class LoginViewModel: BaseObservable() {

    private var view: LoginView? = null

    fun attachView(view: LoginView) {
        this.view = view
    }

    @get:Bindable
    var username: String = ""

   /* @get: Bindable
    var error: String? = null*/

    @get:Bindable
    var password: String = ""


    fun onLoginClick() {

        if (username.isEmpty()) view?.setInputEmptyError("No empty username allowed")
    }

    fun onRegisterClick() {

    }

    fun onUsernameTextChanged() {
        view?.resetError()
    }
}