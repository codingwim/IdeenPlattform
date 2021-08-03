package com.codingschool.ideabase.ui.comment

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.CreateComment
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class CommentViewModel (
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

    fun onCommentTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetCommentEmptyError()
    }

    fun onSubmitClick() {
        if (comment.isEmpty()) view?.setCommentEmptyError(R.string.comment_empty_error_message)
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

}