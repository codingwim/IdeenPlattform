package com.codingschool.ideabase.ui.detail

interface DetailView {
    fun showToast(any: Any)
    fun setIdeaImage(url: String)
    fun setTitleReleasedItalic()
    fun navigateBack()
    //fun navigateOffline(online: Boolean)
    fun showPopupRatingDialog(id: String, checkedItem: Int)
    fun navigateToCommentFragment(id: String)
    fun navigateToEditFragment(id: String)
    fun setRatingIcon(drawIcon: Int)
    fun showMenu()
    fun hideMenu()
    fun hideCommentTitle()
    fun setCommentTitle(resourceString: Int)
    fun addReleaseMenuItem(isAuthor: Boolean)
    fun setActionBarTitle(title: String)
    fun navigateToProfile(id: String)
    fun releaseDialog()
    fun deleteDialog()
}