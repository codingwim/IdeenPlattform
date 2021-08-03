package com.codingschool.ideabase.ui.list

interface ListView {
    fun showToast(any: Any)
    fun getString(any: Any): String
    fun showPopupRatingDialog(id: String, checkedItem: Int, position: Int)
    fun navigateToDetailFragment(id: String)
    fun navigateToCommentFragment(id: String)
    fun navigateToNewIdeaFragment()
    fun navigateToProfile(id: String)
    //fun navigateOffline(online: Boolean)
    fun moveToPositionInRecyclerview(position: Int)
    fun showSearchDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String, selectedCategoriesAsString: String, hasFilterSelection: Boolean)
    fun hideTopBadge()
    fun setTopBadge()
    fun hideAllBadge()
    fun setAllBadge(numberOfNewItems: Int)
    fun setAllBadgeNoNumber()
    fun handleErrorResponse(errorMessage: String?)
    fun showNoTopRankedIdeasYet()
    fun showNoIdeasYet()
    fun showNoResultsFound()
}