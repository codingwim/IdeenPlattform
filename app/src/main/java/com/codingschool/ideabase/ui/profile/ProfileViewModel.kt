package com.codingschool.ideabase.ui.profile

import android.util.Log
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

class ProfileViewModel(
    private val id: String,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) : BaseObservable() {

    private var view: ProfileView? = null

    val compositeDisposable = CompositeDisposable()

    fun attachView(view: ProfileView) {
        this.view = view
    }

    fun init() {
        getUserProfileAndShow()
    }

    @get:Bindable
    var email: String =""

    @get:Bindable
    var name: String = ""

    @get:Bindable
    var firstname: String = ""

    @get:Bindable
    var lastname: String = ""

    @get:Bindable
    var role = ObservableInt(R.string.user_not_idea_manager)

   private fun getUserProfileAndShow() {
        val getId = if (isMyProfile()) prefs.getMyId() else id
        ideaApi.getUserById(getId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
                view?.setProfilePicture(user.profilePicture)
                name = user.toString()
                email = if (isMyProfile()) user.email else ""
                firstname = user.firstname
                lastname = user.lastname
                if (user.isManager)  role.set(R.string.idea_manager)
                notifyPropertyChanged(BR.email)
                notifyPropertyChanged(BR.name)
                notifyPropertyChanged(BR.firstname)
                notifyPropertyChanged(BR.lastname)

                if (isMyProfile()) view?.showMenu()
            }, { t ->
                Log.e("IdeaBase_log", "exception getting user profile $t")

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

    private fun isMyProfile() = id.isEmpty() or id.equals(prefs.getMyId())

}