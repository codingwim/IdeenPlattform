package com.codingschool.ideabase.utils

const val baseUrl = "https://ideenmanagement.tailored-apps.com/api/"

const val NO_SEARCH_QUERY =""

const val NEW_IDEA = true
const val EDIT_IDEA = false

const val TOP_RANKED = true
const val ALL_IDEAS = false

/*enum class Trend(val text: String) { NONE(""), UP("Up"), DOWN("Down") }
enum class Status(val text: String) { NONE(""), RELEASED("Released"), UPDATED("Updated"), NEW("new")}*/
enum class Trend { NONE, UP, DOWN }
enum class Status { NONE, RELEASED, UPDATED, NEW}

// for automatic update api calls, Timeunit.SECONDS
const val INITIAL_DELAY = 5L
const val UPDATE_INTERVAL = 30L

// here we can set the minimum amoutn of ratings an idea needs, to be included on the top ranked screen
const val MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED = 2