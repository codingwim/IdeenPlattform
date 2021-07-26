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

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: DetailView) {
        this.view = view
    }

    fun init() {
        //get idea with api, and set the bindables, set menu items, set comment ist
        view?.setTtitle("Idea:")
        getIdeaAndShow()
        adapter.addCommentClickListener { id ->
            Log.d("observer_ex", "Comment with id $id clicked")

        }
    }

    private fun getIdeaAndShow() {
        ideaApi.getIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                // set idea name in title
                view?.setTtitle(idea.title)
                // first set the menu options: no menu when release // no "release" when not manager
                if (idea.released) view?.hideMenu()
                else if (prefs.isManager()) view?.addReleaseMenuItem()
                // now set all the bindable details, including image
                view?.setIdeaImage(idea.imageUrl)
                ideaTitle = idea.title
                ideaAuthor = idea.authorName
                // TODO locale check to get correct language category
                ideaCategory = idea.category.name_en
                ideaDescritpion = idea.description
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

    fun deleteIdea() {
        ideaApi.deleteIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // todo add snacker with UNDO option ORRRRR warning message before deleting
                view?.showToast("Idea has been deleted")
                view?.navigateBack()
            }, { t ->
                val responseMessage = t.message
                // TODO check response options
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) view?.showToast("Some parameter was missing. Deleting idea failed.")
                    else if (responseMessage.contains(
                            "HTTP 400",
                            ignoreCase = true
                        )
                    ) view?.showToast("Idea was not found. Deleting idea failed.")
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
                // todo add snacker with UNDO option ORRRRR warning message before releasing
                view?.showToast("Idea has been released")
                // remove complete menu on release ?
                view?.hideMenu()
            }, { t ->
                val responseMessage = t.message
                // TODO check response options
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) view?.showToast("Some parameter was missing. Release idea failed.")
                    else if (responseMessage.contains(
                            "HTTP 400",
                            ignoreCase = true
                        )
                    ) view?.showToast("Idea was not found. Release idea failed failed.")
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
    var ideaDescritpion: String = ""

}