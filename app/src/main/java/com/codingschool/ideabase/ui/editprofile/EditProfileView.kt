package com.codingschool.ideabase.ui.editprofile

interface EditProfileView {
    fun showToast(any: Any)
    fun setInputFirstnameError(any: Any)
    fun resetFirstnameError()
    fun setInputLastnameError(any: Any)
    fun resetLastnameError()
    fun setInputPasswordError(any: Any)
    fun resetPasswordError()
    fun setInputPasswordRepeatError(any: Any)
    fun resetPasswordRepeatError()
    fun setFocusPasswordInput()
    fun navigateToLoginRegistered(username: String)
}