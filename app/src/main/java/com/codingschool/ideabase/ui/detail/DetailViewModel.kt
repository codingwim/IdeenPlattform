package com.codingschool.ideabase.ui.detail

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.UpdateReleased
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class DetailViewModel(
    private val id: String,
    val adapter: CommentListAdapter,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) : BaseObservable() {

    private var view: DetailView? = null

    val compositeDisposable = CompositeDisposable()

    fun attachView(view: DetailView) {
        this.view = view
    }

    fun init() {
        //get idea with api, and set the bindables, set menu items, set comment, set comment author click
        view?.setActionBarTitle("Idea:")
        getIdeaAndShow()
        adapter.addCommentClickListener { id ->
            view?.navigateToProfile(id)
        }
    }

    private fun getIdeaAndShow() {
        ideaApi.getIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                // set idea name in title
                view?.setActionBarTitle(idea.title)
                // first set the menu options: add release if manager // no menu when released OR not owner // not "release" and owner sees Edit/delete
                if (prefs.isManager()) view?.addReleaseMenuItem()
                else if (idea.released or (prefs.getMyId() != idea.author.id)) view?.hideMenu()
                // now set all the bindable details, including image
                view?.setIdeaImage(idea.imageUrl)
                ideaTitle = idea.title
                ideaAuthor = idea.authorName
                // locale check to get correct language category
                ideaCategory = if (prefs.isLangEn()) idea.category.name_en else idea.category.name_de
                ideaDescription = idea.description
                if (idea.comments.isEmpty()) view?.hideCommentTitle()
                notifyPropertyChanged(BR.ideaTitle)
                notifyPropertyChanged(BR.ideaAuthor)
                notifyPropertyChanged(BR.ideaCategory)
                notifyPropertyChanged(BR.ideaDescritpion)
                // add comment list
                adapter.updateList(idea.comments)
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast(R.string.not_authorized)
                    } else if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "404 Idea not found")
                        view?.showToast(R.string.idea_not_found_message)
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting idea: $t")
            }).addTo(compositeDisposable)

    }

    fun deleteIdea() {
        ideaApi.deleteIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showToast(R.string.idea_deleted_message)
                view?.navigateBack()
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) view?.showToast(R.string.parameter_missing_message)
                    else if (responseMessage.contains(
                            "HTTP 400",
                            ignoreCase = true
                        )
                    ) view?.showToast(R.string.idea_not_found_message)
                    else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception deleting idea: $t")
            }).addTo(compositeDisposable)

    }

    fun editIdea() {
        view?.navigateToEditFragment(id)
    }

    fun releaseIdea() {
        val updateReleased = UpdateReleased(true)
        ideaApi.releaseIdea(id, updateReleased)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showToast(R.string.idea_released_message)
                // remove complete menu on release
                view?.hideMenu()

            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) view?.showToast(R.string.parameter_missing_message)
                    else if (responseMessage.contains(
                            "HTTP 400",
                            ignoreCase = true
                        )
                    ) view?.showToast(R.string.idea_not_found_message)
                    else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception releasing idea: $t")
            }).addTo(compositeDisposable)
    }

    @get:Bindable
    var ideaTitle: String = ""

    @get:Bindable
    var ideaAuthor: String = ""

    @get:Bindable
    var ideaCategory: String = ""

    @get:Bindable
    var ideaDescription: String = ""

    fun onAuthorClick() {
        view?.navigateToProfile(id)
    }

    fun onConfirmRelease() {
        releaseIdea()
    }

    fun onCancelRelease() {
        view?.showToast(R.string.release_cancelled_message)
    }

    fun onConfirmDelete() {
        deleteIdea()
    }

    fun onCancelDelete() {
        view?.showToast(R.string.delete_cancelled_message)
    }
}