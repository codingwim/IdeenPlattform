package com.codingschool.ideabase.model.data

data class Comment (

    val id: String,

    val author: User,

    val message: String,
    val created: String,
        )
{
    val authorName: String
        get() = author.firstname + " " + author.lastname

}