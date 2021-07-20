package com.codingschool.ideabase.ui.data

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class CommentList (

    val id: String,

    @Embedded val user: User?,

    val message: String,
    val created: String,
        )