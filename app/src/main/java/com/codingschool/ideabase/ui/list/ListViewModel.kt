package com.codingschool.ideabase.ui.list

import android.util.Log
import androidx.databinding.BaseObservable
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.Category
import com.codingschool.ideabase.model.data.Idea
import com.codingschool.ideabase.model.data.PostIdeaRating
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class ListViewModel(
    private val topOrAll: Boolean,
    val adapter: IdeaListAdapter,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
) :
    BaseObservable() {

    private var view: ListView? = null

    val compositeDisposable = CompositeDisposable()

    private var categoryList: List<Category> = emptyList()

    private var ideaListBeforeUpdate: List<Idea> = emptyList()
    private var newestCreatedDate: String = ""
    private var newestLastUpdatedDate: String = ""

    private var listOfSearchCategories: List<String> = emptyList()
    private var searchString = ""

    private fun updateObservable(): Observable<Long> {
        return Observable.interval(INITIAL_DELAY, UPDATE_INTERVAL, TimeUnit.SECONDS)
    }

    fun init() {
        // set initial adapter list here
        getIdeasToAdapter(listOfSearchCategories, searchString)
        adapter.addIdeaClickListener { id ->
            Log.d("observer_ex", "Idea clicked")
            view?.navigateToDetailFragment(id)
        }
        adapter.addCommentClickListener { id, title ->
            Log.d("observer_ex", "comment clicked id: $id & title: $title")
            view?.navigateToCommentFragment(id, title)
        }
        adapter.addRateClickListener { id ->
            getMyRatingForThisIdeaAndStartDialog(id)
        }
        adapter.addProfileClickListener { id ->
            view?.navigateToProfile(id)
        }
        // check periodically for "newer" ideas if in ALL IDEA
        if (!topOrAll) checkApiForUpdatesPeriodically()
    }

    private fun checkApiForUpdatesPeriodically() {
        Log.d("observer_ex", "fun checkApiForUpdatesPeriodically called ")
        updateObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //Log.d("observer_ex", "checking for updates now")
                getIdeasToAdapter(
                    listOfSearchCategories,
                    searchString
                )
            },
                { t ->
                    Log.e("observer_ex", "exception with periodic update: $t")
                }).addTo(compositeDisposable)

    }

    private fun getMyRatingForThisIdeaAndStartDialog(id: String) {
        var ratingGiven: Int? = null
        ideaApi.getIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                ratingGiven = idea.ratings.firstOrNull {
                    it.user?.id == prefs.getMyId()
                }.let {
                    it?.rating
                }
                Log.d("observer_ex", "ratingGiven : $ratingGiven")
                val ratingItem = ratingGiven ?: -1
                Log.d("observer_ex", "ratingItem : $ratingItem")
                view?.showPopupRatingDialog(id, ratingItem - 1)
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
                    } else if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "404 Idea not found")
                        view?.showToast("Idea not found")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting idea: $t")
            }).addTo(compositeDisposable)
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
        // TODO how to extract string here ? add view getString with context (as taost...)
        val newMessageSelectedCategories =
            if (selectedCategoriesAsString.isEmpty()) "Click FILTER below to filter the result by categories"
            else "Result will be filtered by: " + selectedCategoriesAsString
        view?.showSearchDialog(
            categoryArray,
            checkedItems,
            searchText,
            newMessageSelectedCategories
        )
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

    fun filterWithSelectedItemsAndSearchText(
        checkedItems: BooleanArray,
        searchTextFromDialog: String
    ) {
        // Build the category search List for the filtering
        listOfSearchCategories = getlistOfSearchCategories(checkedItems)
        searchString = searchTextFromDialog
        //Log.d("observer_ex", "selectedItems: $searchCategoryString ")
        getIdeasToAdapter(
            listOfSearchCategories,
            searchString
        )
    }

    private fun getIdeasToAdapter(
        listOfSearchCategories: List<String>,
        searchQuery: String
    ) {
        // if searchQuery = empty,
        //      we use getAllIdeas to return complete list !! ELSE return list with search keyword
        //      and THAN filter with the categoryList (if not empty)
        //      TODO add sort with the sorting pref
        //      TODO IF RESULT = EMPTY -> put overlay "no ideas found matching your search and/or filter criteria
        //

        if (searchQuery.isEmpty()) getAllIdeas(listOfSearchCategories)
        else ideaApi.searchIdeas(searchQuery)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val listFromSearch =
                    if (listOfSearchCategories.isNotEmpty())
                        list.filter {
                            it.category.id in listOfSearchCategories
                        } else list
                // search list now filtered with selected categories
                Log.d("observer_ex", "sorting by $topOrAll and $searchQuery")
                val sortedList = when (topOrAll) {
                    true -> {
                        listFromSearch.filter { it.numberOfRatings >= MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED }
                            .sortedWith(
                                compareByDescending { it.avgRating })
                    }
                    false -> {
                        listFromSearch.sortedWith(compareByDescending { it.lastUpdated })
                    }

                }
                adapter.updateList(sortedList)

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

    private fun getAllIdeas(listOfSearchCategories: List<String>) {
        ideaApi.getAllIdeasNoFilter()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val listFromSearch =
                    if (listOfSearchCategories.isNotEmpty())
                        list.filter {
                            it.category.id in listOfSearchCategories
                        } else list
                // search list now filtered with selected categories
                Log.d("observer_ex", "sorting by $topOrAll and $listOfSearchCategories")
                val sortedList = when (topOrAll) {
                    // TODO add average cal or map  sum and count...
                    true -> {
                        listFromSearch.filter { it.numberOfRatings >= MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED }
                            .sortedWith(
                                compareByDescending { it.avgRating })
                    }
                    false -> {
                        listFromSearch.sortedWith(compareByDescending { it.lastUpdated })
                    }

                }
                adapter.updateList(sortedList)

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

    fun setRating(id: String, oldCheckedItem: Int, newCheckedItem: Int) {
        Log.d("observer_ex", "rating $newCheckedItem, before was $oldCheckedItem")
        // careful add +1 to rating checked item, they go 0..4
        if (oldCheckedItem != newCheckedItem) {
            val postIdeaRating = PostIdeaRating(
                newCheckedItem + 1
            )
            ideaApi.rateIdea(id, postIdeaRating)
                .onErrorComplete {
                    it is HttpException && ((it.code() == 401) or (it.code() == 400))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(
                        "observer_ex",
                        "rating has been added/updated replaced"
                    )
                }, { t ->
                    Log.e("observer_ex", "exception adding/updating rating user: $t")
                }).addTo(compositeDisposable)
        }
    }

    fun addIdeaClicked() {
        view?.navigateToNewIdeaFragment()
    }
}