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
    // after changing pwd, we need to let user login again, as base auth has changed!!
    fun navigateToLoginRegistered(username: String)
}