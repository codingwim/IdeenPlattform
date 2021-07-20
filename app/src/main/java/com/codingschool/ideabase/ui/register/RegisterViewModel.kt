package com.codingschool.ideabase.ui.register

import androidx.databinding.BaseObservable

class RegisterViewModel : BaseObservable() {
    private var view: RegisterView? = null

    fun attachView(view: RegisterView) {
        this.view = view
    }


}