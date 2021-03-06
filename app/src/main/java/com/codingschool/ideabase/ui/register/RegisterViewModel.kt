package com.codingschool.ideabase.ui.register

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.CreateUser
import com.codingschool.ideabase.model.remote.IdeaApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.regex.Pattern

class RegisterViewModel(private val ideaApi: IdeaApi) : BaseObservable() {
    private var view: RegisterView? = null

    val compositeDisposable = CompositeDisposable()

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
        var fieldsNotEmpty = false

        when {
            firstname.isEmpty() -> view?.setInputFirstnameError(R.string.enter_first_name_error)
            lastname.isEmpty() -> view?.setInputLastnameError(R.string.error_enter_last_name)
            email.isEmpty() -> view?.setInputEmailError(R.string.enter_valid_email)
            password.isEmpty() -> view?.setInputPasswordError(R.string.please_choose_pwd)
            password2.isEmpty() -> view?.setInputPasswordRepeatError(R.string.pwd_same_error)
            else -> fieldsNotEmpty = true
        }

        // if fields not empty, check validity
        var validCredentialsToRegister = false
        if (fieldsNotEmpty) {
            if (!validEmail(email)) view?.setInputEmailError(R.string.enter_valid_email)
            else
            // check pwds the same
                if (!(password.equals(password2))) {
                    view?.setInputPasswordRepeatError(R.string.pwd_are_ot_same_error)
                } else
                // check pwd valid
                    if (!passwordPattern.matcher(password).matches()) {
                        password = ""
                        password2 = ""
                        notifyPropertyChanged(BR.password)
                        notifyPropertyChanged(BR.password2)
                        view?.setFocusPasswordInput()
                        view?.setInputPasswordError(R.string.password_rule)
                    } else {
                        validCredentialsToRegister = true
                    }
        }

        if (validCredentialsToRegister) {
            val newUser = CreateUser(
                email,
                firstname,
                lastname,
                password
            )
            ideaApi.registerUser(newUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.showToast(R.string.registered_succes)
                    view?.navigateToLoginRegistered(email)
                }, { t ->
                    view?.handleErrorResponse(t.message)
                    Log.e("IdeaBase_log", "exception adding new user: $t")
                }).addTo(compositeDisposable)
        }
    }

    private fun validEmail(email: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun onCancelClick() {
        view?.showToast(R.string.registration_cancelled)
        view?.navigateCancelRegistration()
    }

    fun onFirstnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetFirstnameError()
    }

    fun onLastnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetLastnameError()
    }

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetEmailError()
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetPasswordError()
    }

    fun onPassword2TextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetPasswordRepeatError()
    }

    private val passwordPattern = Pattern.compile(
        "^" + "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-zA-Z])" +  //at least 1 letter->any letter
                "(?=.*[@#$%^&+=!?])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{8,}" +  //at least 8 characters
                "$"
    )
}