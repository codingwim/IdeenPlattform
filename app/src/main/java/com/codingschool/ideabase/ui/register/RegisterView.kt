package com.codingschool.ideabase.ui.register

interface RegisterView {
    fun showToast(any: Any)
    fun setInputFirstnameError(any: Any)
    fun resetFirstnameError()
    fun setInputLastnameError(any: Any)
    fun resetLastnameError()
    fun setInputEmailError(any: Any)
    fun resetEmailError()
    fun setInputPasswordError(any: Any)
    fun resetPasswordError()
    fun setInputPasswordRepeatError(any: Any)
    fun resetPasswordRepeatError()
    fun setFocusPasswordInput()
    fun navigateToLoginRegistered(username: String)
    fun navigateCancelRegistration()
    //fun navigateOffline(online: Boolean)
}