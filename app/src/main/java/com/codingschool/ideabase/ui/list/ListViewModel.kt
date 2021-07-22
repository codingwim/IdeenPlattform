package com.codingschool.ideabase.ui.list

import android.util.Log
import androidx.databinding.BaseObservable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.Category
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.NO_CATEGORY_FILTER
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers.io
import java.util.*

class ListViewModel(
    val adapter: IdeaListAdapter,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: ListView? = null

    private val compositeDisposable = CompositeDisposable()

    private var categoryList: List<Category> = emptyList()

    fun init() {
        // set initial adapter list here
        getAllIdeasToAdapter(NO_CATEGORY_FILTER)
    }

    fun attachView(view: ListView) {
        this.view = view
    }

    fun setFilterDialog() {
        //val locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration())
        // RESET category list and checked items
        var categoryArray: Array<String> = emptyArray()
        var checkedItems: BooleanArray = booleanArrayOf()

        ideaApi.getAllCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                categoryList = list
                for (i in list) {
                    //TODO local check to be done here
                    categoryArray += i.name_en
                    checkedItems += false
                }
                view?.showFilterDialog(categoryArray, checkedItems)

            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast("You are not autorized to log in")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting categories: $t")
            }).addTo(compositeDisposable)
    }

    fun filterWithSelected(checkedItems: BooleanArray) {

        val listOfSearchCategories = emptyList<String>().toMutableList()

        for (i in 0..checkedItems.size - 1) if (checkedItems[i]) listOfSearchCategories += categoryList[i].id
        val searchCategoryString = listOfSearchCategories.joinToString(",")
        Log.d("observer_ex", "selectedItems: $searchCategoryString ")
        if (searchCategoryString.isNotEmpty()) getAllIdeasToAdapter(searchCategoryString)
    }

    private fun getAllIdeasToAdapter(categoryFilter: String) {
        ideaApi.getAllIdeas(categoryFilter)
            //ideaApi.getIdeaById("6d01fe46-a1c3-4c81-baa4-4d353e905db9")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                adapter.setData(list)
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "404 Idea not found")
                        view?.showToast("The Idea was not found")
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast("You are not autorized to log in")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting specific idea: $t")

            }).addTo(compositeDisposable)
    }

}