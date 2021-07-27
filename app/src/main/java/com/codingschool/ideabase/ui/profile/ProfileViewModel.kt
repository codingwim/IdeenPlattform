package com.codingschool.ideabase.ui.profile

import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingschool.ideabase.ui.list.ListView
import com.codingschool.ideabase.utils.Preferences

class ProfileViewModel : BaseObservable(
    // prefs zufügen
) {

    private var view: ProfileView? = null

    fun attachView(view: ProfileView) {
        this.view = view
    }

    fun editProfile() {
        view?.navigateToEditProfileFragment()
    }

    fun addIdea() {
        view?.navigateToNewIdeaFragment("")
    }

    fun logout() {
        //prefs.setAuthString("")
        view?.navigateToLoginFragment("")
    }
}