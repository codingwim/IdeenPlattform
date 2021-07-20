package com.codingschool.ideabase.ui.list

import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : BaseObservable() {

    private var view: ListView? = null

    fun attachView(view: ListView) {
        this.view = view
    }


}