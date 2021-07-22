package com.codingschool.ideabase.model.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Relation

data class CreateIdea(

    var title: String,
    val categoryId: String,
    val description: String,
)
