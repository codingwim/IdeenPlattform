package com.codingschool.ideabase.ui.data

import androidx.room.ColumnInfo

data class Idea(


    @ColumnInfo(name = "idea_id")
    val id: String,

    @ColumnInfo(name = "author")
    val title: String,

    val released: Boolean,

    @ColumnInfo(name = "category")
    val description: String,

    val created: String,
    val lastUpdated: String,

    @ColumnInfo()
    val comments:String,
     val imageUrl: String,

)
