package com.codingschool.ideabase.utils

const val baseUrl = "https://ideenmanagement.tailored-apps.com/api/"

/*enum class Trend(val text: String) { NONE(""), UP("Up"), DOWN("Down") }
enum class Status(val text: String) { NONE(""), RELEASED("Released"), UPDATED("Updated"), NEW("new")}*/
enum class Trend { NONE, UP, DOWN }
enum class Status { NONE, RELEASED, UPDATED, NEW}

// for automatic update api calls, Timeunit.SECONDS
const val INITIAL_DELAY = 2L
const val UPDATE_INTERVAL = 15L

// here we can set the minimum amount of ratings an idea needs, to be included on the top ranked screen Min 2 recommended
const val MIN_NUM_RATINGS_SHOW_IDEA_ON_TOP_RANKED = 6