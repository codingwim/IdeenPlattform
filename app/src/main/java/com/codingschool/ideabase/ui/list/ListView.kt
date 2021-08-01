package com.codingschool.ideabase.ui.list

interface ListView {
    fun showToast(any: Any)
    fun getString(any: Any): String
    fun showPopupRatingDialog(id: String, checkedItem: Int)
    fun navigateToDetailFragment(id: String)
    fun navigateToCommentFragment(id: String, title: String)
    fun navigateToNewIdeaFragment()
    fun navigateToProfile(id: String)
    //fun navigateOffline(online: Boolean)
    fun moveToTopOfRecyclerview()
    fun showSearchDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String, messageSelecteCategories: String)
    fun showFilterDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String, messageSelecteCategories: String)
    fun hideTopBadge()
    fun setTopBadge()
    fun hideAllBadge()
    fun setAllBadge(numberOfNewItems: Int)
    fun setAllBadgeNoNumber()
}