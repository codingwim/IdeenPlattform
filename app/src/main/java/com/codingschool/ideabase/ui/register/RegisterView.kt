package com.codingschool.ideabase.ui.register

interface RegisterView {
    fun showToast(any: Any)
    fun setInputFirstnameError(text: String)
    fun resetFirstnameError()
    fun setInputLastnameError(text: String)
    fun resetLastnameError()
    fun setInputEmailError(text: String)
    fun resetEmailError()
    fun setInputPasswordError(text: String)
    fun resetPasswordError()
    fun setInputPasswordRepeatError(text: String)
    fun resetPasswordRepeatError()
    fun setFocusPasswordInput()
    fun navigateToLoginRegistered(username: String)
    fun navigateCancelRegistration()
}