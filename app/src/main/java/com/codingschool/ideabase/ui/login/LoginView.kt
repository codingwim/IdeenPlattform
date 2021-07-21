package com.codingschool.ideabase.ui.login

interface LoginView {
    fun showToast(any: Any)
    fun setInputUsernameError(text: String)
    fun resetUsernameError()
    fun setInputPasswordError(text: String)
    fun resetPasswordError()
    fun navigateToRegisterFragment()

}