package com.codingschool.ideabase.ui.neweditidea

interface NewEditIdeaView {

    fun showToast(any: Any)
    fun setActionBarTitleEdit()
    fun setCategoryListItems(items: List<String>)
    fun setIdeaImage(url: String)
    fun setSelectedCategory(category: String)
    fun getImageDialog()
    fun resetEmptyIdeaName()
    fun resetEmptyDescription()
    fun resetEmptyCategory()
    fun setInputNameError(any:Any)
    fun setInputDescriptionError(any:Any)
    fun setInputCategoryError(any:Any)
    fun navigateBack()
    fun infoDialog(id: String)
    fun navigateToDetailFragment(id: String)
    fun navigateToAllIdeas()
    fun cancelDialog()
    fun handleErrorResponse(errorMessage: String?)
}