package com.codingschool.ideabase.ui.neweditidea

interface NewEditIdeaView {

    fun showToast(any: Any)
    fun setActionBarTitle(title: String)
    fun setIdeaImage(url: String)
    fun resetEmptyIdeaName()
    fun resetEmptyDescription()
}