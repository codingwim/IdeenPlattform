package com.codingschool.ideabase.ui.comment

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.channels.ticker

class CommentViewModel (
    private val id: String,
    private val title: String,
    private val ideaApi: IdeaApi
) :
    BaseObservable() {

    private var view: CommentView? = null

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        view?.setTitle(title)
    }

    fun attachView(view: CommentView) {
        this.view = view
    }

    @get:Bindable
    var comment: String = ""

    fun onCommentTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0 && count < 3) view?.setTitle(title)
    }

    fun onSubmitClick() {

    }

    fun onCloseClick() {

    }

}