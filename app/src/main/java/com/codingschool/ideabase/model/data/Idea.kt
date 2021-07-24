package com.codingschool.ideabase.model.data

import androidx.room.PrimaryKey
//@Entity(tableName = "idea")
data class Idea(

    @PrimaryKey
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

    val authorName: String
        get() = author.firstname + " " + author.lastname

    val numberOfRatings: Int
        get() = ratings.size

    val avgRating: Double
        get() = if (numberOfRatings > 0) ratings.map { it.rating }.average()
        else 0.0
}
