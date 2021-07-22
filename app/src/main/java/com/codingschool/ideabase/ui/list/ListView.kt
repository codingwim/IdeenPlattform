package com.codingschool.ideabase.ui.list

interface ListView {
    fun showToast(any: Any)
    fun showPopupRatingMenu()
    fun navigateToDetailFragment(id: String)
    fun navigateToCommentFragment(id: String)
}