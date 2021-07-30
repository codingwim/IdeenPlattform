package com.codingschool.ideabase.ui.detail

interface DetailView {
    fun showToast(any: Any)
    fun setIdeaImage(url: String)
    fun navigateBack()
    //fun navigateOffline(online: Boolean)
    fun navigateToEditFragment(id: String)
    fun showMenu()
    fun hideMenu()
    fun hideCommentTitle()
    fun addReleaseMenuItem()
    fun setActionBarTitle(title: String)
    fun navigateToProfile(id: String)
    fun releaseDialog()
    fun deleteDialog()
}