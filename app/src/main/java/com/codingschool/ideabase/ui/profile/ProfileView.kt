package com.codingschool.ideabase.ui.profile

interface ProfileView {
    fun showToast(text: String)
    fun showPopupReleaseAlert()
    fun showPopupDeleteAlert()
    fun navigateToEditProfileFragment(id: String)
    fun navigateToLoginFragment()
}