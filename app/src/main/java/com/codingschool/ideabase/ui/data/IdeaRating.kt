package com.codingschool.ideabase.ui.data

import androidx.room.Embedded

data class IdeaRating(

    @Embedded val user: User?,
    val rating: Int,
)
