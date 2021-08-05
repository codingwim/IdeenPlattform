package com.codingschool.ideabase.ui.editprofile

interface EditProfileView {
    fun showToast(any: Any)
    fun showInfoDialog()
    fun setProfilePicture(url: String?)
    fun getProfileImageDialog()
    fun showOverlayChangePicture()
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
    fun handleErrorResponse(errorMessage: String?)
}