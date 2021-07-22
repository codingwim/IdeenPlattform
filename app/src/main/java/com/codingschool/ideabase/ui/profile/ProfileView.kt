package com.codingschool.ideabase.ui.profile

interface ProfileView {
    fun showToast(any: Any)
    fun showPopupReleaseAlert()
    fun showPopupDeleteAlert()
    fun navigateToEditProfileFragment(id: String)
    fun navigateToLoginFragment()
}