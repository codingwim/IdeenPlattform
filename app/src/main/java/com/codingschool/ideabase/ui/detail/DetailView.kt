package com.codingschool.ideabase.ui.detail

interface DetailView {
    fun showToast(any: Any)
    fun setIdeaImage(uri: String)
    fun navigateBack()
    fun navigateToEditFragment(id: String)
    fun removeReleaseMenuItem()
    fun removeEditMenuItem()
}