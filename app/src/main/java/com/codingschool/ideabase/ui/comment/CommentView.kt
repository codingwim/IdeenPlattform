package com.codingschool.ideabase.ui.comment

import com.codingschool.ideabase.utils.toast

interface CommentView {
     fun showToast(any: Any)
     fun setCommentEmptyError(any: Any)
     fun setTitle(title: String)
}