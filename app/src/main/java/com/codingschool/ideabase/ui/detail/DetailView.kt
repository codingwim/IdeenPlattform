package com.codingschool.ideabase.ui.detail

interface DetailView {
    fun showToast(any: Any)
    fun setIdeaImage(uri: String)
    fun navigateBack()
    fun navigateToEditFragment(id: String)
    fun hideMenu()
    fun hideCommentTitle()
    fun addReleaseMenuItem()
    fun setTtitle(title: String)
}