package com.codingschool.ideabase.ui.list

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
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

    private var categoryList = emptyList<String>()
    private var categoryArrayDE: Array<String> = emptyArray()
    private var categoryArrayEN: Array<String> = emptyArray()


    private var listOfSearchCategories: List<String> = emptyList()
    private var searchString = ""

    private fun updateObservable(): Observable<Long> {
        return Observable.interval(INITIAL_DELAY, UPDATE_INTERVAL, TimeUnit.SECONDS)
    }

    fun init() {

        view?.hideAllBadge()
        view?.hideTopBadge()

        adapter.setTopOrAll(topOrAll)

        adapter.addIdeaClickListener { id ->
            Log.d("observer_ex", "Idea clicked")
            view?.navigateToDetailFragment(id)
        }
        adapter.addCommentClickListener { id ->
            view?.navigateToCommentFragment(id)
        }
        adapter.addRateClickListener { id ->
            getMyRatingForThisIdeaAndStartDialog(id)
        }
        adapter.addProfileClickListener { id ->
            view?.navigateToProfile(id)
        }
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.d(
                    "observer_ex",
                    "onItemRangeInserted with positionStart: $positionStart and itemCount: $itemCount"
                )
                view?.moveToPositionInRecyclerview(positionStart)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                Log.d(
                    "observer_ex",
                    "onItemRangeChanged with positionStart: $positionStart and itemCount: $itemCount"
                )
                view?.moveToPositionInRecyclerview(positionStart)
            }
        })

        getCategoryItems()

        /**
         * if APP JUST STARTED, on first load of top ranked, we get all ides from api, but only show up-to lastAdapterUpdated??
         *      + we set the badges to inform if newer available, (not for ranking status??)
         *      = the showed top rank list is the status of last time, user needs to click again (OR REFRESH ?) to show trend updates
         *  ELSE , we just CLICKED "top ranked" or "All" so we want to clear badges, and show all with trend info (if top ranked clicked) or status info (if all clicked)
         *
         *  Both options start the getPeriodicUpdatesToSetBadges
         */



        if (prefs.appJustStarted()) {
            ideaApi.getAllIdeasNoFilter()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    val trendList = setTrendAndStatusList(list)
                    // returned trendlist is already sorted by ranking
                    adapter.updateList(trendList)
                    // set prefs top ranked list
                    prefs.setTopRankedIds(trendList.map { it.id })
                    ifHasNewerOrUpdatedIdeasSetAllIdeasBadgeAccordingly(list)
                    checkApiForUpdatesPeriodicallyToSetBadges()
                    prefs.setAppNotJustStarted()
                }, { t ->
                    val responseMessage = t.message
                    if (responseMessage != null) {
                        if (responseMessage.contains(
                                "HTTP 401",
                                ignoreCase = true
                            )
                        ) {
                            Log.d("observer_ex", "401 Authorization not valid")
                            view?.showToast("You are not autorized ")
                        } else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception getting ideas: $t")

                }).addTo(compositeDisposable)

        } else {
            ideaApi.getAllIdeasNoFilter()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    val sortedList = when (topOrAll) {
                        true -> {
                            view?.hideTopBadge()
                            list.filter { it.numberOfRatings >= MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED }
                                .sortedWith(
                                    compareByDescending { it.avgRating })
                        }
                        false -> {
                            view?.hideAllBadge()
                            list.sortedWith(compareByDescending { it.lastUpdated })
                        }
                    }
                    val trendList = setTrendAndStatusList(sortedList)
                    adapter.updateList(trendList)
                    prefs.setLastAdapterUpdateNow()
                    view?.hideTopBadge()
                    view?.hideAllBadge()
                    if (topOrAll) {
                        // set prefs top ranked list
                        val rankedList = sortedList.map { it.id }
                        prefs.setTopRankedIds(rankedList)
                    }
                    checkApiForUpdatesPeriodicallyToSetBadges()
                    prefs.setAppNotJustStarted()
                }, { t ->
                    val responseMessage = t.message
                    if (responseMessage != null) {
                        if (responseMessage.contains(
                                "HTTP 401",
                                ignoreCase = true
                            )
                        ) {
                            Log.d("observer_ex", "401 Authorization not valid")
                            view?.showToast("You are not autorized ")
                        } else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception getting ideas to adapter: $t")

                }).addTo(compositeDisposable)
        }
    }

    private fun ifHasNewerOrUpdatedIdeasSetAllIdeasBadgeAccordingly(list: List<Idea>) {
        val dateLastUpdate = prefs.getLastAdapterUpdate()
        val countNew = list.count { (it.created.compareTo(dateLastUpdate) > 0) }
        val updated = list.count { (it.lastUpdated.compareTo(dateLastUpdate) > 0) }

        if (prefs.appJustStarted()) {
            if (countNew > 0) view?.setAllBadge(countNew)
            else {
                val atLEastOneIdeaUpdated = list.firstOrNull { it.lastUpdated > dateLastUpdate }
                if (atLEastOneIdeaUpdated != null) {
                    view?.setAllBadgeNoNumber()
                }
            }
        }
    }

    private fun setTrendAndStatusList(list: List<Idea>): List<Idea> {
        val dateLastUpdate = prefs.getLastAdapterUpdate()
        val topRankedIdsLastUpdate = prefs.getTopRankedIds()
        val rankedlist =
            list.filter { it.numberOfRatings >= MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED }
                .sortedByDescending { it.avgRating }

        for (i in 0..list.size - 1) {
            val idea = list[i]
            if (idea.created > dateLastUpdate) idea.status = Status.NEW
            else if (idea.lastUpdated > dateLastUpdate) {
                if (idea.released) idea.status = Status.RELEASED
                else idea.status = Status.UPDATED
            } else idea.status = Status.NONE
        }

        for (i in 0..rankedlist.size - 1) {
            val idea = rankedlist[i]

            if (topRankedIdsLastUpdate.isNotEmpty()) {
                val positionLastUpdate = (topRankedIdsLastUpdate.indexOfFirst { it == idea.id })
                if (positionLastUpdate != -1) {
                    if (positionLastUpdate > i) idea.trend = Trend.UP
                    else if (positionLastUpdate < i) idea.trend = Trend.DOWN
                    else idea.trend = Trend.NONE
                } else idea.trend = Trend.UP
            } else {
                idea.trend = Trend.NONE
            }
            /*Log.d(
                "observer_ex",
                "idea ${idea.title} has status ${idea.status} and trend ${idea.trend} "
            )*/
        }
        return if (topOrAll) rankedlist else list
    }


    private fun checkApiForUpdatesPeriodicallyToSetBadges() {
        updateObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //Log.d("observer_ex", "checking for updates now")
                getIdeasSetBadges()
            },
                { t ->
                    Log.e("observer_ex", "exception with periodic update: $t")
                }).addTo(compositeDisposable)

    }

    private fun getIdeasSetBadges() {
        val dateLastUpdate = prefs.getLastAdapterUpdate()
        val topRankedIdsLastUpdate = prefs.getTopRankedIds()
        ideaApi.getAllIdeasNoFilter()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val sortedList =
                    list.filter { it.numberOfRatings >= MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED }
                        .sortedWith(
                            compareByDescending { it.avgRating })
                // check if newer/updated ideas, set All badge
                val countNew = list.count { it.created > dateLastUpdate }
                if (countNew > 0) view?.setAllBadge(countNew)
                else {
                    val atLEastOneIdeaUpdated = list.firstOrNull { it.lastUpdated > dateLastUpdate }
                    if (atLEastOneIdeaUpdated != null) view?.setAllBadgeNoNumber() else view?.hideAllBadge()
                }
                // check if sortedList other ranking than lastUpdate
                var trendChanges = false
                if (sortedList.size == topRankedIdsLastUpdate.size) {
                    for (i in 0..sortedList.size - 1) {
                        val ideaId = sortedList[i].id
                        val rankLastUpdateId = topRankedIdsLastUpdate[i] ?: ""
                        if (ideaId != rankLastUpdateId) {
                            trendChanges = true
                            break
                        }
                    }
                } else trendChanges = true
                if (trendChanges) {
                    view?.setTopBadge()
                } else view?.hideTopBadge()


            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast(R.string.not_authorized)
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting ideas to adapter: $t")

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
                val ratingItem = ratingGiven ?: -1
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
                        view?.showToast(R.string.not_authorized)
                    } else if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "404 Idea not found")
                        view?.showToast(R.string.idea_not_found_message)

                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting idea: $t")
            }).addTo(compositeDisposable)
    }

    fun attachView(view: ListView) {
        this.view = view
    }

    /*    fun setFilterDialog(
            categoryArray: Array<String>,
            checkedItems: BooleanArray,
            searchText: String
        ) {
            view?.showFilterDialog(
                categoryArray,
                checkedItems,
                searchText
            )
        }*/
    private fun getCategoryItems() {
        ideaApi.getAllCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                categoryArrayEN = list.map { category ->
                    category.name_en
                }.toTypedArray()
                categoryArrayDE = list.map { category ->
                    category.name_de
                }.toTypedArray()
                categoryList = list.map { category ->
                    category.id
                }
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast(R.string.not_authorized)
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting categories: $t")
            }).addTo(compositeDisposable)
    }

    fun setSearchDialog(
        categoryArray: Array<String>,
        checkedItems: BooleanArray,
        searchText: String
    ) {
        // Build the category message text
        val listOfSearchCategories = getlistOfSearchCategories(checkedItems)
        val selectedCategoriesAsString = listOfSearchCategories.joinToString(", ")

        view?.showSearchDialog(
            categoryArray,
            checkedItems,
            searchText,
            selectedCategoriesAsString,
            !selectedCategoriesAsString.isEmpty()
        )
    }

    fun setInitialSearchDialog() {
        val categoryArray = if (prefs.isLangEn()) categoryArrayEN else categoryArrayDE
        val checkedItems = BooleanArray(categoryArray.size)

        setSearchDialog(
            categoryArray,
            checkedItems,
            ""
        )
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
        for (i in 0..checkedItems.size - 1)
            if (checkedItems[i]) listOfSearchCategories += categoryList[i]
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
                }, { t ->
                    Log.e("observer_ex", "exception adding/updating rating user: $t")
                }).addTo(compositeDisposable)
        }
    }

    fun addIdeaClicked() {
        view?.navigateToNewIdeaFragment()
    }
}