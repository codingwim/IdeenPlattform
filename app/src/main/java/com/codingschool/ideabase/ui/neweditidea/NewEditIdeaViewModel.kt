package com.codingschool.ideabase.ui.neweditidea

import androidx.databinding.BaseObservable

class NewEditIdeaViewModel: BaseObservable() {

    private var view: NewEditIdeaView? = null

    fun attachView(view: NewEditIdeaView) {
        this.view = view
    }
}