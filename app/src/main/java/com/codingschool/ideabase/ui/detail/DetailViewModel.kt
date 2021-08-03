package com.codingschool.ideabase.ui.detail

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.PostIdeaRating
import com.codingschool.ideabase.model.data.UpdateReleased
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException

class DetailViewModel(
    private val id: String,
    val adapter: CommentListAdapter,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) : BaseObservable() {

    private var view: DetailView? = null

    val compositeDisposable = CompositeDisposable()
    private var authorId: String = ""

    fun attachView(view: DetailView) {
        this.view = view
    }

    fun init() {
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
                if (!idea.released) {
                    if (prefs.isManager()) {
                        view?.addReleaseMenuItem(prefs.getMyId() == idea.author.id)
                        view?.showMenu()
                    }
                    if (prefs.getMyId() == idea.author.id) view?.showMenu()
                } else view?.showIdeaReleased()
                authorId = idea.author.id
                setRatingImage(idea.avgRating)
                view?.setIdeaImage(idea.imageUrl)
                ideaTitle = idea.title
                ideaAuthor = idea.authorName
                // locale check to get correct language category
                ideaCategory =
                    if (prefs.isLangEn()) idea.category.name_en else idea.category.name_de
                ideaDescription = idea.description
                if (idea.comments.isEmpty()) commentTitle.set(R.string.be_the_first_to_comment) else commentTitle.set(R.string.comments_title_detail_idea)
                notifyPropertyChanged(BR.ideaTitle)
                notifyPropertyChanged(BR.ideaAuthor)
                notifyPropertyChanged(BR.ideaCategory)
                notifyPropertyChanged(BR.ideaDescription)
                // add comment list
                adapter.updateList(idea.comments.sortedByDescending { it.created })
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("observer_ex", "exception getting idea: $t")
            }).addTo(compositeDisposable)

    }

    private fun setRatingImage(avgRating: Double) {
        val drawIcon = when (avgRating) {
            in 0.1..1.5 -> R.drawable.rated_1
            in 1.5..2.5 -> R.drawable.rated_2
            in 2.5..3.5 -> R.drawable.rated_3
            in 3.5..4.5 -> R.drawable.rated_4
            in 4.5..5.0 -> R.drawable.rated_5
            else -> R.drawable.rated_none
        }
        view?.setRatingIcon(drawIcon)
    }

    private fun deleteIdea() {
        ideaApi.deleteIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.navigateBack()
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("observer_ex", "exception deleting idea: $t")
            }).addTo(compositeDisposable)
    }

    fun onRateClick() {
        getMyRatingForThisIdeaAndStartDialog(id)
    }

    fun editIdea() {
        view?.navigateToEditFragment(id)
    }

    fun onCommentClick() {
        view?.navigateToCommentFragment(id)
    }

    private fun releaseIdea() {
        val updateReleased = UpdateReleased(true)
        ideaApi.releaseIdea(id, updateReleased)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showToast(R.string.idea_released_message)
                // remove complete menu on release
                view?.hideMenu()
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("observer_ex", "exception releasing idea: $t")
            }).addTo(compositeDisposable)
    }

    private fun getMyRatingForThisIdeaAndStartDialog(id: String) {
        var ratingGiven: Int?
        ideaApi.getIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                ratingGiven = idea.ratings.firstOrNull {
                    it.user?.id == prefs.getMyId()
                }.let {
                    it?.rating
                }
                val ratingItem = ratingGiven ?: -1
                view?.showPopupRatingDialog(id, ratingItem - 1)
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("observer_ex", "exception getting idea: $t")
            }).addTo(compositeDisposable)
    }

    @get: Bindable
    val commentTitle = ObservableInt(R.string.comments_title_detail_idea)

    @get:Bindable
    var ideaTitle: String = ""

    @get:Bindable
    var ideaAuthor: String = ""

    @get:Bindable
    var ideaCategory: String = ""

    @get:Bindable
    var ideaDescription: String = ""

    fun onAuthorClick() {
        view?.navigateToProfile(authorId)
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

    fun setRating(id: String, oldCheckedItem: Int, newCheckedItem: Int) {
        if (oldCheckedItem != newCheckedItem) {
            val newRating = newCheckedItem + 1
            val postIdeaRating = PostIdeaRating(
                newRating
            )
            ideaApi.rateIdea(id, postIdeaRating)
                .onErrorComplete {
                    it is HttpException && ((it.code() == 401) or (it.code() == 400))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setRatingImage(newRating.toDouble())
                }, { t ->
                    Log.e("observer_ex", "exception adding/updating rating user: $t")
                }).addTo(compositeDisposable)
        }
    }
}