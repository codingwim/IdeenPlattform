package com.codingschool.ideabase.ui.neweditidea

import androidx.databinding.BaseObservable
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences

class NewEditIdeaViewModel(
    newIdea: Boolean,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
): BaseObservable() {

    private var view: NewEditIdeaView? = null

    fun attachView(view: NewEditIdeaView) {
        this.view = view
    }
    fun init() {
        // set edittexts with hint or prefill depending on newIdea (true) or editIdea(false)
    }
}