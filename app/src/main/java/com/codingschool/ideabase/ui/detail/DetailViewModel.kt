package com.codingschool.ideabase.ui.detail

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class DetailViewModel(
    private val id: String,
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
        ideaApi.getIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                view?.setIdeaImage(idea.imageUrl)
                ideaTitle=idea.title
                ideaAuthor = idea.authorName
                // TODO locale check to get correct language category
                ideaCategory = idea.category.name_en
                ideaDescritpion = idea.description
                notifyPropertyChanged(BR.ideaTitle)
                notifyPropertyChanged(BR.ideaAuthor)
                notifyPropertyChanged(BR.ideaCategory)
                notifyPropertyChanged(BR.ideaDescritpion)

            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast("You are not autorized to log in")
                    } else if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "404 Idea not found")
                        view?.showToast("Idea not found")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting idea: $t")
            }).addTo(compositeDisposable)
    }

    @get:Bindable
    var ideaTitle: String = ""

    @get:Bindable
    var ideaAuthor: String =""

    @get:Bindable
    var ideaCategory: String = ""

    @get:Bindable
    var ideaDescritpion: String =""

}