package com.codingschool.ideabase.ui.profile

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import androidx.databinding.library.baseAdapters.BR

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

    //private var profilePictureUri: Uri = "".toUri()
    private var profilePictureUrl: String = ""

    @get:Bindable
    var email: String =""

    @get:Bindable
    var name: String = ""

    @get:Bindable
    var firstname: String = ""

    @get:Bindable
    var lastname: String = ""

    @get:Bindable
    var role: String = ""

   private fun getUserProfileAndShow() {
        val getId = if (isMyProfile()) prefs.getMyId() else id
        ideaApi.getUserById(getId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
                view?.setProfilePicture(user.profilePicture)
                name = user.firstname + " " + user.lastname
                email = if (isMyProfile()) user.email else ""
                firstname = user.firstname
                lastname = user.lastname
                role = if (user.isManager) "Idea manager" else "User"

                notifyPropertyChanged(BR.email)
                notifyPropertyChanged(BR.name)

                notifyPropertyChanged(BR.firstname)
                notifyPropertyChanged(BR.lastname)
                notifyPropertyChanged(BR.role)

                if (isMyProfile()) view?.showMenu()


            }, { t ->
                view?.handleErrorResponse(t.message)
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

    private fun isMyProfile() = id.isEmpty() or id.equals(prefs.getMyId())

}