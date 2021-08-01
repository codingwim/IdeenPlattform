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
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


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
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                view?.moveToTopOfRecyclerview()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                // works !! dont move but put a star to icon?
                super.onItemRangeChanged(positionStart, itemCount)
                view?.moveToTopOfRecyclerview()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            }
        })

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
                    Log.d("observer_ex", "TopRanked from prefs: ${prefs.getTopRankedIds().size}")
                    prefs.setTopRankedIds(trendList.map { it.id })
                    Log.d("observer_ex", "TopRanked set to prefs: ${prefs.getTopRankedIds().size}")
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
                        Log.d(
                            "observer_ex",
                            "TopRanked from prefs: ${prefs.getTopRankedIds().size}"
                        )
                        prefs.setTopRankedIds(rankedList)
                        Log.d(
                            "observer_ex",
                            "toporall: true and set rankedList to prefs: ${rankedList.size} "
                        )

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
        val updated = list.count { (it.lastUpdated.compareTo(dateLastUpdate) > 0)}
        Log.d("observer_ex", "compare to date = $dateLastUpdate result new: $countNew")

        if (prefs.appJustStarted()) {
            if (countNew > 0) view?.setAllBadge(countNew)
            else {
                val atLEastOneIdeaUpdated = list.firstOrNull { it.lastUpdated > dateLastUpdate }
                if (atLEastOneIdeaUpdated != null) {
                    view?.setAllBadgeNoNumber()
                    Log.d("observer_ex", "initial check updatebadge set")
                }
            }
        }
    }

    private fun setTrendAndStatusList(list: List<Idea>): List<Idea> {
        val dateLastUpdate = prefs.getLastAdapterUpdate()
        val topRankedIdsLastUpdate = prefs.getTopRankedIds()
        Log.d("observer_ex", "topRankedLastUpdate.size: ${topRankedIdsLastUpdate.size}")

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
                //Log.d("observer_ex", "i: $i, title idea: ${idea.title}, id: ${idea.id} , id lastUpdate: ${topRankedIdsLastUpdate[i]}, idFoundLastTimeAt: $positionLastUpdate")
                if (positionLastUpdate != -1) {
                    if (positionLastUpdate > i) idea.trend = Trend.UP
                    else if (positionLastUpdate < i) idea.trend = Trend.DOWN
                    else idea.trend = Trend.NONE
                } else idea.trend = Trend.UP
            } else {
                idea.trend = Trend.NONE
            }
            Log.d(
                "observer_ex",
                "idea ${idea.title} has status ${idea.status} and trend ${idea.trend} "
            )
        }
    return if (topOrAll) rankedlist else list
}


private fun checkApiForUpdatesPeriodicallyToSetBadges() {
    Log.d("observer_ex", "fun checkApiForUpdatesPeriodically called ")
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
    Log.d("observer_ex", "topRankedIdsLastUpdate.size: ${topRankedIdsLastUpdate.size} ")

    ideaApi.getAllIdeasNoFilter()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ list ->
            val sortedList =
                list.filter { it.numberOfRatings >= MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED }
                    .sortedWith(
                        compareByDescending { it.avgRating })
            // check if newer/updated ideas, set All badge
            val countNew = list.count { it.created > dateLastUpdate }
            Log.d("observer_ex", "topOrAll: ${topOrAll} and countnew: $countNew ")
            if (countNew > 0) view?.setAllBadge(countNew)
            else {
                val atLEastOneIdeaUpdated = list.firstOrNull { it.lastUpdated > dateLastUpdate }
                if (atLEastOneIdeaUpdated != null) view?.setAllBadgeNoNumber() else view?.hideAllBadge()
            }
            // check if sortedList other ranking than lastUpdate
            var trendChanges = false
            if (sortedList.size == topRankedIdsLastUpdate.size) {
                Log.d(
                    "observer_ex",
                    "topOrAll: ${topOrAll} and ranking comparison listsize equal: ${sortedList.size} "
                )
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
                Log.e(
                    "observer_ex",
                    "topOrAll: ${topOrAll} SHOW TOP BADGE ??? "
                )

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
                    view?.showToast("You are not autorized ")
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