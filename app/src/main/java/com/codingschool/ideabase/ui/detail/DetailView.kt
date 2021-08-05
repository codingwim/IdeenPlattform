package com.codingschool.ideabase.ui.detail

interface DetailView {
    fun showToast(any: Any)
    fun setIdeaImage(url: String)
    fun showIdeaReleased()
    fun navigateBack()
    fun showPopupRatingDialog(id: String, checkedItem: Int)
    fun navigateToCommentFragment(id: String)
    fun navigateToEditFragment(id: String)
    fun setRatingIcon(drawIcon: Int)
    fun showMenu()
    fun hideMenu()
    fun setIsManagerSetIsAuthorOrNotAndResetMenu(isAuthor: Boolean)
    fun setActionBarTitle(title: String)
    fun navigateToProfile(id: String)
    fun releaseDialog()
    fun deleteDialog()
    fun handleErrorResponse(errorMessage: String?)
}