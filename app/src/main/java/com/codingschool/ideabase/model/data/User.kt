package com.codingschool.ideabase.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val id: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "user_fname")
    val firstname: String,

    @ColumnInfo(name = "user_lname")
    val lastname: String,

    @ColumnInfo(name = "profilepic")
    val profilePicture: String?,

    @ColumnInfo(name = "isManager")
    val isManager: Boolean

)

{
    override fun toString(): String {
        return let { it.firstname + " " + it.lastname }
    }
}
