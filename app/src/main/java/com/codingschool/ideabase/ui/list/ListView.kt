package com.codingschool.ideabase.ui.list

interface ListView {
    fun showToast(any: Any)
    fun showPopupRatingDialog(id: String, ratingArray: Array<String>, checkedItem: Int)
    fun navigateToDetailFragment(id: String)
    fun navigateToCommentFragment(id: String, title: String)
    fun navigateToNewIdeaFragment(newIdea: Boolean)
    fun showSearchDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String, messageSelecteCategories: String)
    fun showFilterDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String, messageSelecteCategories: String)
}