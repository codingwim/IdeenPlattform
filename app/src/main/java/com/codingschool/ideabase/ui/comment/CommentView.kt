package com.codingschool.ideabase.ui.comment

interface CommentView {
     fun showToast(any: Any)
     fun setCommentEmptyError(any: Any)
     fun resetCommentEmptyError()
     fun navigateBack()
     fun cancelDialog()
     fun setProfileImage(url: String)
     fun handleErrorResponse(errorMessage: String?)
}