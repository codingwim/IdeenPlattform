package com.codingschool.ideabase.ui.register

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.R
import kotlinx.coroutines.withContext

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
        
        // on succesfull registration, pass username back to login page
        view?.navigateToLoginRegistered("wim")
    }

    fun onCancelClick() {
        view?.showToast(R.string.registration_cancelled)
        view?.navigateCancelRegistration()
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