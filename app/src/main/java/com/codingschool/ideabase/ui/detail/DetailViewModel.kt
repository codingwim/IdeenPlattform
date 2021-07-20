package com.codingschool.ideabase.ui.detail

import androidx.databinding.BaseObservable

class DetailViewModel : BaseObservable() {

    private var view: DetailView? = null

    fun attachView(view: DetailView) {
        this.view = view
    }
}