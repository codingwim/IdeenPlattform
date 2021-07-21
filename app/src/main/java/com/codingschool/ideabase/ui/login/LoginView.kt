package com.codingschool.ideabase.ui.login

interface LoginView {
    fun showToast(text: String)
    fun setInputEmptyError(text: String)
    fun navigateToRegisterFragment()
    fun resetError()
}