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

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: CommentView) {
        this.view = view
    }

    fun init() {
        view?.setProfileImage(prefs.getProfileImage())
    }

    @get:Bindable
    var comment: String = prefs.getCommentDraft()

    fun onCommentTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count < 3) view?.resetCommentEmptyError()
    }

    fun onSubmitClick() {
        if (comment.isEmpty()) view?.setCommentEmptyError(R.string.comment_empty_error_message)
        else {
            val createComment = CreateComment(comment)
            ideaApi.commentIdea(id, createComment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.showToast(R.string.thank_you_comment)
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
                    Log.e("observer_ex", "exception adding comment: $t")
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