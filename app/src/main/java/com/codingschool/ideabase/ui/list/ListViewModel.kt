package com.codingschool.ideabase.ui.list

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
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
            view?.navigateToDetailFragment(id)
        }
        adapter.addCommentClickListener { id ->
            view?.navigateToCommentFragment(id)
        }
        adapter.addRateClickListener { id, position ->
            getMyRatingForThisIdeaAndStartDialog(id, position)
        }
        adapter.addProfileClickListener { id ->
            view?.navigateToProfile(id)
        }

        getCategoryItems()

        if (prefs.appJustStarted()) {
            loadAndShowTopRankedIdeas()
        } else {
            loadAndShowIdeasTopOrAll()
        }
    }

    fun attachView(view: ListView) {
        this.view = view
    }

    private fun loadAndShowTopRankedIdeas() {
        ideaApi.getAllIdeasNoFilter()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val trendList = setTrendAndStatusList(list)
                // returned trendlist is already sorted by ranking
                if (trendList.isEmpty()) view?.showNoTopRankedIdeasYet()
                else {
                    adapter.updateList(trendList)
                    // set prefs top ranked list
                    prefs.setTopRankedIds(trendList.map { it.id })
                }
                ifHasNewerOrUpdatedIdeasSetAllIdeasBadgeAccordingly(list)
                checkApiForUpdatesPeriodicallyToSetBadges()
                prefs.setAppNotJustStarted()
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting ideas: $t")
            }).addTo(compositeDisposable)
    }

    private fun loadAndShowIdeasTopOrAll() {
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
                if (trendList.isNotEmpty()) adapter.updateList(trendList) else {
                    if (topOrAll) view?.showNoTopRankedIdeasYet() else view?.showNoIdeasYet()
                }
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
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting ideas to adapter: $t")
            }).addTo(compositeDisposable)
    }

    private fun ifHasNewerOrUpdatedIdeasSetAllIdeasBadgeAccordingly(list: List<Idea>) {
        val dateLastUpdate = prefs.getLastAdapterUpdate()
        val countNew = list.count { (it.created.compareTo(dateLastUpdate) > 0) }
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

        for (idea in list) {
            if (idea.created > dateLastUpdate) idea.status = Status.NEW
            else if (idea.lastUpdated > dateLastUpdate) {
                if (idea.released) idea.status = Status.RELEASED
                else idea.status = Status.UPDATED
            } else idea.status = Status.NONE
        }

        for (i in rankedlist.indices) {
            val idea = rankedlist[i]
            if (topRankedIdsLastUpdate.isNotEmpty()) {
                val positionLastUpdate = (topRankedIdsLastUpdate.indexOfFirst { it == idea.id })
                if (positionLastUpdate != -1) {
                    when {
                        positionLastUpdate > i -> idea.trend = Trend.UP
                        positionLastUpdate < i -> idea.trend = Trend.DOWN
                        else -> idea.trend = Trend.NONE
                    }
                } else idea.trend = Trend.UP
            } else {
                idea.trend = Trend.NONE
            }
        }
        return if (topOrAll) rankedlist else list
    }

    private fun checkApiForUpdatesPeriodicallyToSetBadges() {
        updateObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getIdeasSetBadges()
            },
                { t ->
                    Log.e("IdeaBase_log", "exception with periodic update: $t")
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
                if (sortedList.isNotEmpty()) {
                    if (sortedList.size == topRankedIdsLastUpdate.size) {
                        for (i in sortedList.indices) {
                            val ideaId = sortedList[i].id
                            val rankLastUpdateId = topRankedIdsLastUpdate[i]
                            if (ideaId != rankLastUpdateId) {
                                trendChanges = true
                                break
                            }
                        }
                    } else trendChanges = true
                }
                if (trendChanges) {
                    view?.setTopBadge()
                } else view?.hideTopBadge()
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting ideas to adapter: $t")

            }).addTo(compositeDisposable)
    }

    private fun getMyRatingForThisIdeaAndStartDialog(id: String, position: Int) {
        var ratingGiven: Int?
        ideaApi.getIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                ratingGiven = idea.ratings.firstOrNull {
                    it.user?.id == prefs.getMyId()
                }.let {
                    it?.rating
                }
                val ratingItem = ratingGiven ?: -1
                view?.showPopupRatingDialog(id, ratingItem - 1, position)
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting idea: $t")
            }).addTo(compositeDisposable)
    }

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
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting categories: $t")
            }).addTo(compositeDisposable)
    }

    fun setSearchDialog(
        categoryArray: Array<String>,
        checkedItems: BooleanArray,
        searchText: String
    ) {
        // Build the category message text
        val listOfSearchCategories = getListOfSearchCategories(checkedItems)
        val selectedCategoriesAsString = listOfSearchCategories.joinToString(", ")
        view?.showSearchDialog(
            categoryArray,
            checkedItems,
            searchText,
            selectedCategoriesAsString,
            selectedCategoriesAsString.isNotEmpty()
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
        listOfSearchCategories = getListOfSearchCategories(checkedItems)
        searchString = searchTextFromDialog
        //Log.d("IdeaBase_log", "selectedItems: $searchCategoryString ")
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
                if (sortedList.isNotEmpty()) adapter.updateList(sortedList) else view?.showNoResultsFound()
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting searched ideas: $t")
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
                if (sortedList.isNotEmpty()) adapter.updateList(sortedList) else {
                    if (topOrAll) view?.showNoTopRankedIdeasYet() else view?.showNoIdeasYet()
                }
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting all ideas: $t")

            }).addTo(compositeDisposable)
    }

    private fun getListOfSearchCategories(checkedItems: BooleanArray): List<String> {
        val listOfSearchCategories = emptyList<String>().toMutableList()
        for (i in checkedItems.indices)
            if (checkedItems[i]) listOfSearchCategories += categoryList[i]
        return listOfSearchCategories
    }

    fun setRating(id: String, oldCheckedItem: Int, newCheckedItem: Int, position: Int) {
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
                    //refresh rv position
                    getUpdatedIdeaAndUpdateRVItemAtPosition(id, position)
                }, { t ->
                    Log.e("IdeaBase_log", "exception adding/updating rating user: $t")
                }).addTo(compositeDisposable)
        }
    }

    private fun getUpdatedIdeaAndUpdateRVItemAtPosition(id: String, position: Int) {
        ideaApi.getIdeaById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                if (topOrAll) {
                    adapter.updateRating(position, idea)
                    getIdeasSetBadges()
                } else {
                    adapter.updateRating(position, idea)
                }
            }, { t ->
                // handle error
                view?.handleErrorResponse(t.message)
            }).addTo(compositeDisposable)
    }

    fun addIdeaClicked() {
        view?.navigateToNewIdeaFragment()
    }
}