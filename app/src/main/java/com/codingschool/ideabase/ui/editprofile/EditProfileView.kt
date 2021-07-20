package com.codingschool.ideabase.ui.editprofile

interface EditProfileView {
    fun showToast(text: String)
    // after changing pwd, we need to let user login again, as base auth has changed!!
    fun navigateToLoginRegistered(username: String)
}