package com.codingschool.ideabase.ui.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "idea")
data class Idea(


    @ColumnInfo(name = "idea_id")
    val id: String,

    @Embedded val user: User?,
    val title: String,

    val released: Boolean,

    @Embedded val category: Category?,
    val description: String,

    val created: String,
    val lastUpdated: String,

    @Embedded val commentList: CommentList,
    val comments:String,
     val imageUrl: String,

    @Embedded val ideaRating: IdeaRating?,

)
