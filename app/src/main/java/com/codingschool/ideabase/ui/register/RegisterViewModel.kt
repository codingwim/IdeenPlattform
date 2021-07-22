package com.codingschool.ideabase.ui.register

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.R
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.model.data.CreateUser
import com.codingschool.ideabase.model.data.UpdateUser
import com.codingschool.ideabase.model.remote.IdeaApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.withContext

class RegisterViewModel(private val ideaApi: IdeaApi) : BaseObservable() {
    private var view: RegisterView? = null

    private val compositeDisposable = CompositeDisposable()

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
        // check empty fields
        var fieldsNotEmpty = false

        if (firstname.isEmpty()) view?.setInputFirstnameError("Please enter your first name")
        else if (lastname.isEmpty()) view?.setInputLastnameError("Please enter your first name")
        else if (email.isEmpty()) view?.setInputEmailError("Please enter a valid e-mail address")
        else if (password.isEmpty()) view?.setInputPasswordError("Please enter a password of minimum 8 characters, including at least 1 number")
        else if (password2.isEmpty()) view?.setInputPasswordRepeatError("Please enter the same password again")
        else fieldsNotEmpty = true

        // if fields not empty, check validity
        var validCredentailsToRegister = false
        if (fieldsNotEmpty) {
            // check valid e-mail
            if (!validEmail(email)) view?.setInputEmailError("Please enter a valid e-mail address")
            else
            // check pwds the same
                if (!(password.equals(password2))) {
                    view?.setInputPasswordRepeatError("Passwords are not the same")
                } else
                // check pwd valid
                    if (!validPassword(password)) {
                        password = ""
                        password2 = ""
                        notifyPropertyChanged(BR.password)
                        notifyPropertyChanged(BR.password2)
                        view?.setFocusPasswordInput()
                        view?.setInputPasswordError("Please enter a password of minimum 8 characters, including at least 1 number")
                    } else {
                        validCredentailsToRegister = true
                    }
        }

        // if credentials valid, register User via API call
        if (validCredentailsToRegister) {
            val newUser = CreateUser(
                email,
                firstname,
                lastname,
                password
            )
            ideaApi.registerUser(newUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("observer_ex", "new user added over API")
                    view?.showToast("You are registered! You can now login with your e-mail and password")
                    view?.navigateToLoginRegistered(email)
                }, { t ->
                    val responseMessage = t.message
                    if (responseMessage != null) {
                        if (responseMessage.contains(
                                "HTTP 409",
                                ignoreCase = true
                            )
                        ) view?.setInputEmailError(R.string.email_already_inuse_input_error)
                        else if (responseMessage.contains(
                                "HTTP 400",
                                ignoreCase = true
                            )
                        ) view?.setInputEmailError("Some parameter is missing")
                        else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception adding new user: $t")
                }).addTo(compositeDisposable)
        }
    }

    private fun validEmail(email: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun validPassword(password: String): Boolean {
        return password.length > 4
    }

    fun onCancelClick() {
        view?.showToast(R.string.registration_cancelled)
        view?.navigateCancelRegistration()
    }

    fun onFirstnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetFirstnameError()
    }

    fun onLastnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetLastnameError()
    }

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetEmailError()
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0) view?.resetPasswordError()
    }

    fun onPassword2TextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetPasswordRepeatError()
    }
}