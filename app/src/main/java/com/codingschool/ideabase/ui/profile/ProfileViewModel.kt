package com.codingschool.ideabase.ui.profile

import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingschool.ideabase.ui.list.ListView

class ProfileViewModel : BaseObservable() {

    private var view: ProfileView? = null

    fun attachView(view: ProfileView) {
        this.view = view
    }
}