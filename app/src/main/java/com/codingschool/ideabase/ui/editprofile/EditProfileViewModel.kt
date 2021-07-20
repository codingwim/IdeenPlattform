package com.codingschool.ideabase.ui.editprofile

import androidx.databinding.BaseObservable

class EditProfileViewModel : BaseObservable() {

    private var view: EditProfileView? = null

    fun attachView(view: EditProfileView) {
        this.view = view
    }
}