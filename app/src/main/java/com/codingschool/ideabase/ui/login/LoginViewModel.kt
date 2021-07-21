package com.codingschool.ideabase.ui.login

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.model.remote.IdeaApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val IdeaApi: IdeaApi): BaseObservable() {

    private var view: LoginView? = null

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: LoginView) {
        this.view = view
    }

    @get:Bindable
    var username: String = ""

    @get:Bindable
    var password: String = ""


    fun onLoginClick() {

        if (username.isEmpty()) view?.setInputEmptyError("No empty username allowed")
        Log.d("observer_ex", "trying to call api getOwnUser now")
        //Dummy test for login
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

    fun onRegisterClick() {
        view?.navigateToRegisterFragment()

    }

    fun onUsernameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetError()
    }
}