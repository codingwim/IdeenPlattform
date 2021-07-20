package com.codingschool.ideabase.ui.list

interface ListView {
    fun showToast(text: String)
    fun showPopupRatingMenu()
    fun navigateToDetailFragment(id: String)
    fun navigateToCommentFragment(id: String)
}