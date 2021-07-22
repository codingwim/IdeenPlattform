package com.codingschool.ideabase.ui.register

interface RegisterView {
    fun showToast(text: String)
    fun navigateToLoginRegistered(username: String)
    fun navigateCancel()
}