package com.codingschool.ideabase.ui.profile

interface ProfileView {
    fun showToast(any: Any)
    fun setProfilePicture(url: String?)
    fun navigateToEditProfileFragment()
    fun navigateToLoginFragment(username: String)
    fun navigateToNewIdeaFragment(editIdeaId: String)
    fun hideMenu()
    fun showMenu()
    fun handleErrorResponse(errorMessage: String?)
}