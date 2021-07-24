package com.codingschool.ideabase.ui.detail

import androidx.databinding.BaseObservable
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.disposables.CompositeDisposable

class DetailViewModel(
    id: String,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) : BaseObservable() {

    private var view: DetailView? = null

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: DetailView) {
        this.view = view
    }

    fun init() {
        //get idea with api, and set the bindables
    }
}