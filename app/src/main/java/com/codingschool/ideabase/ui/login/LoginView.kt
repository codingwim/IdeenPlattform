package com.codingschool.ideabase.ui.login

interface LoginView {
    fun showToast(any: Any)
    fun setInputUsernameError(any: Any)
    fun resetUsernameError()
    fun setInputPasswordError(any: Any)
    fun resetPasswordError()
    fun navigateToRegisterFragment()
    fun navigateToTopRankedFragment()
    fun showSetProfilePictureDialog()
    fun navigateToEditProfileFragmentAndLoadPictureSelector()
}