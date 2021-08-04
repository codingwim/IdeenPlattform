package com.codingschool.ideabase.model.data

data class User(

    val id: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val profilePicture: String?,
    val isManager: Boolean
)
{
    override fun toString(): String {
        return let { it.firstname + " " + it.lastname }
    }
}
