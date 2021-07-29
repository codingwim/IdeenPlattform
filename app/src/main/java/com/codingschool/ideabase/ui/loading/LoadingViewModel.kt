package com.codingschool.ideabase.ui.loading

import android.util.Log
import androidx.databinding.BaseObservable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.ui.login.LoginView
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class LoadingViewModel(
    online: Boolean,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: LoadingView? = null

    val compositeDisposable = CompositeDisposable()

    fun init() {
        if (prefs.getAuthString().isNotEmpty()) {
            // on loading viewmodel, check if we have valid credentials in shared prefs, do chek login auto login and navigate to next screen
            // while "checking" in, rotate progressbar with info text!!

            checkCredentialsWithAPI()
        } else {
            view?.navigatToLogin()
            Log.d("observer_ex", "prefs empty")
        }
    }

    fun attachView(view: LoginView) {
        this.view = view
    }
    private fun checkCredentialsWithAPI() {
        ideaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
            //.subscribeOn(Schedulers.io())
            .subscribe({ user ->
                prefs.setCredentialID(user.id)
                prefs.setIsManager(user.isManager)
                user.profilePicture?.let { prefs.setProfileImage(it) }
                // TODO welcome back!!
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
                        view?.showToast(R.string.worng_login_credentials_message)
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        view?.showToast(R.string.error_pwd_user_not_valid)
                    }
                    else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception adding new user: $t")
            }).addTo(compositeDisposable)
    }

}