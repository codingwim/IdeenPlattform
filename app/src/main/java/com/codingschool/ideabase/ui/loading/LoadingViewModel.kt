package com.codingschool.ideabase.ui.loading

import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
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
            checkCredentialsWithAPI()
        } else {
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
    var loadingMessage = ObservableInt(R.string.chacking_credentials_loading)

    fun onRefreshClick() {
            layoutRetry()
            checkCredentialsWithAPI()
    }


    private fun checkCredentialsWithAPI() {
        ideaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
                prefs.setCredentialID(user.id)
                prefs.setIsManager(user.isManager)
                user.profilePicture?.let { prefs.setProfileImage(it) }
                view?.navigateToTopRankedFragment()
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    when {
                        responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        ) -> {
                            view?.navigateToLogin()
                        }
                        responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        ) -> {
                            view?.navigateToLogin()
                        }
                        else -> {
                            layoutOffline()
                        }
                    }
                }
                Log.e("IdeaBase_log", "exception checking user credentials: $t")
            }).addTo(compositeDisposable)
    }

    private fun layoutOffline() {
        loadingMessage.set(R.string.oops_offline_try_again_loading)
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
        loadingMessage.set(R.string.checking_credentials_again_loading)
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