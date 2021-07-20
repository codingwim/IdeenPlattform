package com.codingschool.ideabase.ui.data

import androidx.room.ColumnInfo
import androidx.room.Relation

data class CreateIdea(

    var title: String,
    @ColumnInfo(name="categoryId")
    val categoryId: String,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )

    val description: String,
)
