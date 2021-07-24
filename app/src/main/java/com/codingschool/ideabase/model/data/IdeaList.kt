package com.codingschool.ideabase.model.data

import androidx.room.Embedded
import androidx.room.PrimaryKey
import java.util.*

data class IdeaList (

    // NOT USED. If needed, to be checked
    val id:String,


    val user: User?,

    val title:String,
    val released:Boolean,

    val category: Locale.Category?,

    val description:String,
    val created:String,
    val lastUpdated:String,

    val commentList: List<Comment>,

    val imageUrl:String,

    val ideaRating: IdeaRating,

        )