package com.codingschool.ideabase.ui.profile

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.ui.list.ListView
import com.codingschool.ideabase.utils.MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class ProfileViewModel(
    private val id: String,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) : BaseObservable() {

    private var view: ProfileView? = null

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: ProfileView) {
        this.view = view
    }

    fun init() {
        // check
            getUserProfileAndShow(id)
    }

    private fun getUserProfileAndShow(id: String) {
        val getId = if (id.isEmpty()) prefs.getMyId() else id
        ideaApi.getUserById(getId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->

                // add user fileds to vm fields
                //if (id.isEmpty()) visibilty email ok ELSE visibility GONE
                Log.d("observer_ex", "received profile")

            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast("You are not autorized")
                    } else if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        ) ){
                            Log.d("observer_ex", "404 User not found")
                            view?.showToast("User not found")
                        }
                        else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting user profile $t")

            }).addTo(compositeDisposable)

    }

    fun editProfile() {
        view?.navigateToEditProfileFragment()
    }

    fun addIdea() {
        view?.navigateToNewIdeaFragment("")
    }

    fun logout() {
        prefs.setAuthString("")
        view?.navigateToLoginFragment("")
    }
}