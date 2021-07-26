package com.codingschool.ideabase.ui.detail

interface DetailView {
    fun showToast(any: Any)
    fun setIdeaImage(uri: String)
    fun navigateBack()
    fun navigateToEditNewFragment(id: String, newIde: Boolean)
}