package com.codingschool.ideabase.ui.data

import android.media.Rating
import androidx.room.Embedded
import com.codingschool.ideabase.ui.data.CommentList
import com.codingschool.ideabase.ui.data.IdeaRating
import com.codingschool.ideabase.ui.data.User
import java.util.*

data class IdeaList (

    val id:String,

    @Embedded val user: User?,

    val title:String,
    val released:Boolean,

    @Embedded val category: Locale.Category?,

    val description:String,
    val created:String,
    val lastUpdated:String,

    @Embedded val commentList: CommentList,

    val imageUrl:String,

    @Embedded val rating: Rating,

    @Embedded val ideaRating: IdeaRating,

        )