package com.codingschool.ideabase.ui.comment

interface CommentView {
     fun showToast(any: Any)
     fun setError()
     fun clearError()
     fun navigateBack()
     fun cancelDialog()
     fun setProfileImage(url: String)
     fun handleErrorResponse(errorMessage: String?)
}