package com.codingschool.ideabase.ui.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class User(


    @ColumnInfo(name = "user_id")
    val id: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "user_fname")
    val firstname: String,

    @ColumnInfo(name = "user_lname")
    val lastname: String,

    @ColumnInfo(name = "profilepic")
    val profilepic: String,

    @ColumnInfo(name = "isManager")
    val isManager: Boolean,


)
