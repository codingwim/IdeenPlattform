package com.codingschool.ideabase.model.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Comment

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

    )
{

    fun Author() = author.firstname + " " + author.lastname
}
