package com.codingschool.ideabase.ui.comment

import com.codingschool.ideabase.utils.toast

interface CommentView {
     fun showToast(any: Any)
     fun setCommentEmptyError(any: Any)
     fun resetCommentEmptyError()
     fun navigateBack()
     //fun navigateOffline(online: Boolean)
     fun cancelDialog()
     fun setProfileImage(url: String)
}