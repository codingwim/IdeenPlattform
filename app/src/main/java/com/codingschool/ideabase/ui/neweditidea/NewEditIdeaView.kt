package com.codingschool.ideabase.ui.neweditidea

interface NewEditIdeaView {

    fun showToast(any: Any)
    fun setActionBarTitle(title: String)
    fun setCategoryListItems(items: List<String>)
    fun setIdeaImage(url: String)
    //fun setSelectedCategory(category: String)
    fun resetEmptyIdeaName()
    fun resetEmptyDescription()
    fun setInputNameError(any:Any)
    fun setInputDescriptionError(any:Any)
    fun setInputCategoryError(any:Any)
}