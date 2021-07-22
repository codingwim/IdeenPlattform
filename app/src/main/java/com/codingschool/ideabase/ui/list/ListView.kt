package com.codingschool.ideabase.ui.list

interface ListView {
    fun showToast(any: Any)
    fun showPopupRatingMenu()
    fun navigateToDetailFragment(id: String)
    fun navigateToCommentFragment(id: String)
    fun showSearchDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String)
    fun showFilterDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String)
}