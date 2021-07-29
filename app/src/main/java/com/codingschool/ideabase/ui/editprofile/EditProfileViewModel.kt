package com.codingschool.ideabase.ui.editprofile

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.UpdateUser
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class EditProfileViewModel(
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) : BaseObservable() {


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

    var email: String =""

    @get:Bindable
    var firstname: String = ""

    @get:Bindable
    var lastname: String = ""

    @get:Bindable
    var password: String = ""

    @get:Bindable
    var password2: String = ""

    @get:Bindable
    var profilepicture: String = ""

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
                email,
                password,
                firstname,
                lastname,

            )
            ideaApi.updateUser(updatedUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("observer_ex", "user updated over API")
                    view?.showToast("You're credentials have been updated. Please login again")
                    view?.navigateToLoginRegistered(email)
                }, { t ->
                    val responseMessage = t.message
                    if (responseMessage != null) {
                        if (responseMessage.contains(
                                "HTTP 401",
                                ignoreCase = true
                            )
                        ) view?.showToast("You are not authorized to log in")
                        else if (responseMessage.contains(
                                "HTTP 400",
                                ignoreCase = true
                            )
                        ) view?.showToast("Some parameter is missing")
                        else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception updating user: $t")
                }).addTo(compositeDisposable)
        }

    }
    private fun setMyCredentialsFromAPI() {
        ideaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
            //.subscribeOn(Schedulers.io())
            .subscribe({ user ->
                view?.showToast("Hi ${user.firstname}, if you change your password, you will be redirected to login again!")
                email = user.email
                firstname = user.firstname
                lastname = user.lastname

               // profilepicture= user.profilePicture.toString()

                notifyPropertyChanged(BR.firstname)
                notifyPropertyChanged(BR.lastname)

                //notifyPropertyChanged(BR.profilepicture)


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
                Log.e("observer_ex", "exception getting user info: $t")
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