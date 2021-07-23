package com.codingschool.ideabase.ui.list

import android.util.Log
import androidx.databinding.BaseObservable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.Category
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.NO_SEARCH_QUERY
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

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
        getdeasToAdapter(emptyList(), NO_SEARCH_QUERY)
    }

    fun attachView(view: ListView) {
        this.view = view
    }

    fun setFilterDialog(
        categoryArray: Array<String>,
        checkedItems: BooleanArray,
        searchText: String,
        messageSelectedCategories: String
    ) {
        view?.showFilterDialog(
            categoryArray,
            checkedItems,
            searchText,
            messageSelectedCategories
        )
    }

    fun setSearchDialog(
        categoryArray: Array<String>,
        checkedItems: BooleanArray,
        searchText: String
    ) {
        // Build the category message text
        val listOfSearchCategories = getlistOfSearchCategories(checkedItems)
        val selectedCategoriesAsString = listOfSearchCategories.joinToString(", ")
        val newMessageSelectedCategories =
            if (selectedCategoriesAsString.isEmpty()) "You can add categories to filter the result. Just click FILTER below"
            else "Filter by: " + selectedCategoriesAsString
        view?.showSearchDialog(categoryArray, checkedItems, searchText, newMessageSelectedCategories)
    }

    fun setInitialSearchDialog() {
        //val locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration())
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
                view?.showSearchDialog(
                    categoryArray,
                    checkedItems,
                    "",
                    "You can add categories to filter the result. Just click FILTER below"
                )

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
        // Build the category search List for the filtering
        val listOfSearchCategories = getlistOfSearchCategories(checkedItems)
        //Log.d("observer_ex", "selectedItems: $searchCategoryString ")
        getdeasToAdapter(
            listOfSearchCategories,
            searchText
        )
    }

    private fun getdeasToAdapter(
        listOfSearchCategories: List<String>,
        searchQuery: String
    ) {
        // if searchQuery = empty,
        //      we use searchquery "id" to return complete list !! ELSE return list with search keyword
        //      and THAN filter with the categoryList (if not empty)
        //      TODO add sort with the sorting pref
        //      TODO IF RESULT = EMPTY -> put overlay "no ideas found matching your search and/or filter criteria
        //

        val newSearchQuery = if (searchQuery.isEmpty()) "id" else searchQuery

        ideaApi.searchIdeas(newSearchQuery)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val listFromSearch =
                    if (listOfSearchCategories.isNotEmpty())
                        list.filter {
                            it.category.id in listOfSearchCategories
                        } else list
                // search list now filtered with selected categories
                adapter.updateList(listFromSearch)

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
                Log.e("observer_ex", "exception getting searched ideas: $t")

            }).addTo(compositeDisposable)
    }

    private fun getlistOfSearchCategories(checkedItems: BooleanArray): List<String> {
        val listOfSearchCategories = emptyList<String>().toMutableList()
        for (i in 0..checkedItems.size - 1) if (checkedItems[i]) listOfSearchCategories += categoryList[i].id
        return listOfSearchCategories
    }
}