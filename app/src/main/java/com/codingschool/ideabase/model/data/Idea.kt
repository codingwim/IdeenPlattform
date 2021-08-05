package com.codingschool.ideabase.model.data

import com.codingschool.ideabase.utils.Status
import com.codingschool.ideabase.utils.Trend

data class Idea(

    val id: String,

    val author: User,

    val title: String,

    val released: Boolean,

    val category: Category,

    val description: String,

    val created: String,
    val lastUpdated: String,

    val comments: List<Comment>,

    val imageUrl: String,

    val ratings: List<IdeaRating>

) {

    var trend: Trend = Trend.NONE
    var status: Status = Status.NONE

    val authorName: String
        get() = author.firstname + " " + author.lastname

    val numberOfRatings: Int
        get() = ratings.size

    val avgRating: Double
        get() = if (numberOfRatings > 0) ratings.map { it.rating }.average()
        else 0.0
}
