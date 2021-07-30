package com.codingschool.ideabase.utils

const val baseUrl = "https://ideenmanagement.tailored-apps.com/api/"

const val NO_SEARCH_QUERY =""

const val NEW_IDEA = true
const val EDIT_IDEA = false

const val TOP_RANKED = true
const val ALL_IDEAS = false

// for automatic update api calls, Timeunit.SECONDS
const val INITIAL_DELAY = 5L
const val UPDATE_INTERVAL = 30L

// here we can set the minimum amoutn of ratings an idea needs, to be included on the top ranked screen
const val MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED = 1