package com.codingschool.ideabase.ui.profile

import com.codingschool.ideabase.ui.neweditidea.NewEditIdeaViewModel

interface ProfileView {
    fun showToast(any: Any)
    fun navigateToEditProfileFragment()
    fun navigateToLoginFragment(username: String)
    fun navigateToNewIdeaFragment(editIdea: String)
    fun hideMenu()
}