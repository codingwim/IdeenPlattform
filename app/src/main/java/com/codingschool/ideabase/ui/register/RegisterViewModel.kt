package com.codingschool.ideabase.ui.register

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class RegisterViewModel : BaseObservable() {
    private var view: RegisterView? = null

    fun attachView(view: RegisterView) {
        this.view = view
    }

    @get:Bindable
    var firstname: String = ""

    @get:Bindable
    var lastname: String = ""

    @get:Bindable
    var email: String = ""

    @get:Bindable
    var password: String = ""

    @get:Bindable
    var password2: String = ""


    fun onRegisterClick() {
    }

    fun onCancelClick() {

    }

    fun onFirstnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
    fun onLastnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
    fun onPassword2TextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
}