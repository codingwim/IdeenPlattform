package com.codingschool.ideabase.model.data

import androidx.room.Embedded

data class IdeaRating(

    val user: User?,
    val rating: Int
)
