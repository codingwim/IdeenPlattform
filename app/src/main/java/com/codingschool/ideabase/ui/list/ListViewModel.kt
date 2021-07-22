package com.codingschool.ideabase.ui.list

import android.util.Log
import androidx.databinding.BaseObservable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.Category
import com.codingschool.ideabase.model.data.Idea
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.NO_CATEGORY_FILTER
import com.codingschool.ideabase.utils.NO_SEARCH_QUERY
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
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
        getAllIdeasToAdapter(NO_CATEGORY_FILTER, NO_SEARCH_QUERY)
    }

    fun attachView(view: ListView) {
        this.view = view
    }

    fun setSearchDialog() {

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
                view?.showSearchDialog(categoryArray, checkedItems, "")

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

    fun filterWithSelectedItemsAndSearchText(checkedItems: BooleanArray, searchText: String) {
        // Build the category search string
        val listOfSearchCategories = emptyList<String>().toMutableList()
        for (i in 0..checkedItems.size - 1) if (checkedItems[i]) listOfSearchCategories += categoryList[i].id
        val searchCategoryString = listOfSearchCategories.joinToString(",")

        //Log.d("observer_ex", "selectedItems: $searchCategoryString ")

        if (searchCategoryString.isNotEmpty())
        if (searchCategoryString.isNotEmpty() or searchText.isNotEmpty()) getAllIdeasToAdapter(
            searchCategoryString,
            searchText
        )
    }

    private fun getAllIdeasToAdapter(searchCategoryString: String, searchQuery: String) {
        // we make 2 searches,
        //      one with filtered categories
        //      another with search string
        // ->   keep the intersection

        var listFilteredByCategories: List<Idea> = emptyList()

        if (searchCategoryString.isNotEmpty() or searchQuery.isEmpty()) {
            ideaApi.getAllIdeas(searchCategoryString)
                //ideaApi.getIdeaById("6d01fe46-a1c3-4c81-baa4-4d353e905db9")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    listFilteredByCategories = list
                    adapter.setData(listFilteredByCategories)
                    //Log.d("observer_ex", "adapter set with ${listFilteredByCategories[0].title} and more")
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
                            view?.showToast("You are not autorized to search")
                        } else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception getting ideas: $t")

                }).addTo(compositeDisposable)
        }

        var listFromSearch: List<Idea> = emptyList()

        if (searchQuery.isNotEmpty()) {

            ideaApi.searchIdeas(searchQuery)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    listFromSearch = list

                }, { t ->
                    val responseMessage = t.message
                    if (responseMessage != null) {
                        if (responseMessage.contains(
                                "HTTP 401",
                                ignoreCase = true
                            )
                        ) {
                            Log.d("observer_ex", "401 Authorization not valid")
                            view?.showToast("You are not autorized to search")
                        } else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception getting ideas: $t")

                }).addTo(compositeDisposable)
        }
        // listFilteredByCategories will return complete list if searchstring is empty, so populates on init!!
        if (searchQuery.isEmpty()) {

            adapter.setData(listFilteredByCategories)
        }
        else if (searchCategoryString.isEmpty()) adapter.setData(listFromSearch)
        else {
            // we got two lists and need to get the intersection
            val searchedAndFilteredList: List<Idea> = (listFilteredByCategories intersect listFromSearch).toList()
            adapter.setData(searchedAndFilteredList)
        }
    }

}