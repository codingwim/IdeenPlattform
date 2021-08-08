package com.codingschool.ideabase.ui.comment

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.CreateComment
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class CommentViewModel(
    private val id: String,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: CommentView? = null

    val compositeDisposable = CompositeDisposable()

    fun attachView(view: CommentView) {
        this.view = view
    }

    fun init() {
        view?.setProfileImage(prefs.getProfileImage())
    }

    @get:Bindable
    var comment: String = prefs.getCommentDraft()

    @get:Bindable
    val commentTitle = ObservableInt(R.string.prof_info_will_be_visible_commenting)

    fun onCommentTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
         when {
            s.length in 1 until MAX_COMMENT_LENGTH-1 -> resetCommentEmptyError()
            s.length == MAX_COMMENT_LENGTH -> setMaxLenghtError()
            before>0 -> resetCommentEmptyError()
        }
    }

    private fun setCommentEmptyError() {
        commentTitle.set(R.string.comment_empty_error_message)
        view?.setError()
    }

    private fun setMaxLenghtError() {
        commentTitle.set(R.string.comment_max_lenght_reached)
        view?.setError()
    }

    private fun resetCommentEmptyError() {
        commentTitle.set(R.string.prof_info_will_be_visible_commenting)
        view?.clearError()
    }

    fun onSubmitClick() {
        if (comment.isEmpty()) setCommentEmptyError()
        else {
            val createComment = CreateComment(comment)
            ideaApi.commentIdea(id, createComment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.showToast(R.string.thank_you_comment)
                    prefs.setCommentDraft("")
                    view?.navigateBack()
                }, { t ->
                    view?.handleErrorResponse(t.message)
                    Log.e("IdeaBase_log", "exception adding comment: $t")
                }).addTo(compositeDisposable)
        }
    }

    fun onCloseClick() {
        if (comment.isNotEmpty()) view?.cancelDialog() else view?.navigateBack()
    }

    fun onCancelWithoutDraft() {
        prefs.setCommentDraft("")
        view?.showToast(R.string.add_comment_cancelled)
        view?.navigateBack()
    }

    fun onCancelWithDraft() {
        prefs.setCommentDraft(comment)
        view?.showToast(R.string.comment_saved_as_draft)
        view?.navigateBack()
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 500
    }

}