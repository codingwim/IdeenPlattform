package com.codingschool.ideabase.ui.register

interface RegisterView {
/*    fun showToast(text: String)
    fun showToast(res: Int)*/
    fun showToast(any: Any)
    fun navigateToLoginRegistered(username: String)
    fun navigateCancelRegistration()
}