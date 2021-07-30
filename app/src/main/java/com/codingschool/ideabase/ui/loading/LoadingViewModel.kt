package com.codingschool.ideabase.ui.loading

import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class LoadingViewModel(
    private var online: Boolean,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: LoadingView? = null

    val compositeDisposable = CompositeDisposable()

    fun init() {
        // offline -> set offline messages and start periodic updates
        // online A returning logged in user
        //          -> check credentials: OK  -> top ranked
        //                                NOK -> login screen
        //                                timeout -> offline -> see above
        //        B no saved user data
        //         -> login screen
        if (!online) {
            layoutOffline()
            logoVisibility = View.INVISIBLE
            progressIndicatorVisibility = View.INVISIBLE
            errorVisibility = View.VISIBLE
            refreshButtonVisibility = View.VISIBLE

        } else if (prefs.getAuthString().isNotEmpty()) {
            // on loading viewmodel, check if we have valid credentials in shared prefs,
            // do check login auto login and navigate to Top Ranked
            // while "checking" in, rotate progressbar with info text!!
            loadingMessage = "Checking credentials..."
            notifyPropertyChanged(BR.loadingMessage)
            checkCredentialsWithAPI()
        } else {
            loadingMessage = "..."
            view?.navigateToLogin()
        }
    }

    fun attachView(view: LoadingView) {
        this.view = view
    }

    @get:Bindable
    var logoVisibility: Int = View.VISIBLE

    @get:Bindable
    var errorVisibility: Int = View.INVISIBLE

    @get:Bindable
    var refreshButtonVisibility: Int = View.INVISIBLE

    @get:Bindable
    var progressIndicatorVisibility: Int = View.VISIBLE

    @get:Bindable
    var loadingMessage: String = ""

    fun onRefreshClick() {
            layoutRetry()
            checkCredentialsWithAPI()
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
                        view?.navigateToLogin()
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        view?.navigateToLogin()
                    } else {
                        layoutOffline()
                        //view?.showToast(R.string.network_issue_check_network)
                    }
                }
                Log.e("observer_ex", "exception checking user credentials: $t")
            }).addTo(compositeDisposable)
    }

    private fun layoutOffline() {
        loadingMessage = "Oops, there seems to be a connectivity Issue. Please check your internet connection and try again..."
        logoVisibility = View.INVISIBLE
        progressIndicatorVisibility = View.INVISIBLE
        errorVisibility = View.VISIBLE
        refreshButtonVisibility = View.VISIBLE

        notifyPropertyChanged(BR.logoVisibility)
        notifyPropertyChanged(BR.progressIndicatorVisibility)
        notifyPropertyChanged(BR.loadingMessage)
        notifyPropertyChanged(BR.errorVisibility)
        notifyPropertyChanged(BR.refreshButtonVisibility)
    }

    private fun layoutRetry() {
        loadingMessage = "Trying to check credentials again..."
        logoVisibility = View.INVISIBLE
        progressIndicatorVisibility = View.VISIBLE
        errorVisibility = View.VISIBLE
        refreshButtonVisibility = View.INVISIBLE

        notifyPropertyChanged(BR.logoVisibility)
        notifyPropertyChanged(BR.progressIndicatorVisibility)
        notifyPropertyChanged(BR.loadingMessage)
        notifyPropertyChanged(BR.errorVisibility)
        notifyPropertyChanged(BR.refreshButtonVisibility)
    }

}