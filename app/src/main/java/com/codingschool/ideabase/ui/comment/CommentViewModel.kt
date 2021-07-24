package com.codingschool.ideabase.ui.comment

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.CreateComment
import com.codingschool.ideabase.model.remote.IdeaApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class CommentViewModel (
    private val id: String,
    private val ideaApi: IdeaApi
) :
    BaseObservable() {

    private var view: CommentView? = null

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        //view?.setTitle(title)
        view?.setTitle("")
        //Log.d("observer_ex", "init id $id % title: $title")
    }

    fun attachView(view: CommentView) {
        this.view = view
    }

    @get:Bindable
    var comment: String = ""

    fun onCommentTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0 && count < 3) view?.setTitle("")
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
                        ) view?.showToast("Some parameter was missing. Adding comment failed.")
                        else if (responseMessage.contains(
                                "HTTP 400",
                                ignoreCase = true
                            )
                        ) view?.showToast("Idea was not found. Adding comment failed.")
                        else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception adding comment: $t")
                }).addTo(compositeDisposable)

        }
    }

    fun onCloseClick() {
        view?.showToast(R.string.add_comment_cancelled)
        view?.navigateBack()
    }

}