package com.codingschool.ideabase.ui.list

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.Idea
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.model.remote.ImageHandler
import com.codingschool.ideabase.utils.NO_CATEGORY_FILTER
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.koin.java.KoinJavaComponent.inject

class ListViewModel(
    val adapter: IdeaListAdapter,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: ListView? = null

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        // set initial adapter list here

        ideaApi.getAllIdeas(NO_CATEGORY_FILTER)
        //ideaApi.getIdeaById("6d01fe46-a1c3-4c81-baa4-4d353e905db9")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                adapter.setData(list)
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "404 Idea not found")
                        view?.showToast("The Idea was not found")
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast("You are not autorized to log in")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting specific idea: $t")

            }).addTo(compositeDisposable)
    }

    fun attachView(view: ListView) {
        this.view = view
    }


}