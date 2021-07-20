package com.codingschool.ideabase.ui.data

import androidx.room.ColumnInfo

data class CommentList (

    val id: String,

    @ColumnInfo(name= "Author")
    val message: String,
    val created: String,
        )