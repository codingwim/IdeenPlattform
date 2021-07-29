package com.codingschool.ideabase.ui.profile

import com.codingschool.ideabase.ui.neweditidea.NewEditIdeaViewModel

interface ProfileView {
    fun showToast(any: Any)
    fun setProfilePicture(url: String?)
    fun navigateToEditProfileFragment()
    fun navigateToLoginFragment(username: String)
    fun navigateToNewIdeaFragment(editIdeaId: String)
    fun hideMenu()
    fun showMenu()
}