package com.codingschool.ideabase.ui.neweditidea

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class NewEditIdeaViewModel(
    private val editIdea: String,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
): BaseObservable() {

    private var view: NewEditIdeaView? = null

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: NewEditIdeaView) {
        this.view = view
    }
    fun init() {
        // set edittexts with hint or prefill depending on editIdea (id) or editIdea("")
        if (editIdea.isNotEmpty()) {
            getIdeaAndPrefill()
        }
    }

    private fun getIdeaAndPrefill() {
        saveButtonText.set(R.string.save_idea_edit)
        uploadImageButtonText.set(R.string.change_image_idea_edit)
        view?.setActionBarTitle("Edit idea")

        ideaApi.getIdeaById(editIdea)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                // set idea name in title
                view?.setActionBarTitle(idea.title)
                // now set all the bindable details, including image
                view?.setIdeaImage(idea.imageUrl)
                ideaName = idea.title
                // TODO locale check to get correct language category
                ideaCategory = idea.category.name_en
                ideaDescritpion = idea.description
                notifyPropertyChanged(BR.ideaName)
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
    var ideaName: String = ""

    @get:Bindable
    var ideaCategory: String = ""

    @get:Bindable
    var ideaDescritpion: String = ""

    @get: Bindable
    val saveButtonText = ObservableInt(R.string.save_idea_new)

    @get: Bindable
    val uploadImageButtonText = ObservableInt(R.string.upload_image_idea_new)



    fun onSaveClick() {

    }

    fun onUploadImageClick() {

    }

    fun onIdeaNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetEmptyIdeaName()
    }


    fun onDescriptionTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        view?.resetEmptyDescription()
    }
}