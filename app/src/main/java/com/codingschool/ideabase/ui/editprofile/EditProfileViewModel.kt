package com.codingschool.ideabase.ui.editprofile

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.UpdateUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class EditProfileViewModel : BaseObservable() {


    private var view: EditProfileView? = null
    private val compositeDisposable = CompositeDisposable()


    fun init() {
        if (prefs.getAuthString().isNotEmpty()) {

            setMyCredentialsFromAPI()
            Log.d("observer_ex", "prefs not empty")
        } else Log.d("observer_ex", "prefs empty")
    }
    fun attachView(view: EditProfileView) {
        this.view = view
    }
    @get:Bindable
    var firstname: String = ""

    @get:Bindable
    var lastname: String = ""

    @get:Bindable
    var password: String = ""

    @get:Bindable
    var password2: String = ""

    fun onSaveClick() {
        // check empty fields
        var fieldsNotEmpty = false

        if (firstname.isEmpty()) view?.setInputFirstnameError("Please enter your first name")
        else if (lastname.isEmpty()) view?.setInputLastnameError("Please enter your first name")
        else if (password.isEmpty()) view?.setInputPasswordError("Please enter a password of minimum 8 characters, including at least 1 number")
        else if (password2.isEmpty()) view?.setInputPasswordRepeatError("Please enter the same password again")
        else fieldsNotEmpty = true

        // if fields not empty, check validity
        var validCredentailsToRegister = false
        if (fieldsNotEmpty) {
            // check valid e-mail

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
            val updatedUser = UpdateUser(
                firstname,
                lastname,
                password
            )
            ideaApi.updateUser(updatedUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

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
                    Log.e("observer_ex", "exception updating new user: $t")
                }).addTo(compositeDisposable)
        }

    }
    private fun setMyCredentialsFromAPI() {
        ideaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
            //.subscribeOn(Schedulers.io())
            .subscribe({ user ->
                view?.showToast("Hi ${user.firstname}, welcome back!")


            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {

                        view?.setInputPasswordError("")
                        view?.showToast("Authorization is not valid")
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

    private fun validPassword(password: String): Boolean {
        return password.length > 4
    }


    fun onFirstnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetFirstnameError()
    }

    fun onLastnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetLastnameError()
    }



    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0) view?.resetPasswordError()
    }

    fun onPassword2TextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetPasswordRepeatError()
    }
}