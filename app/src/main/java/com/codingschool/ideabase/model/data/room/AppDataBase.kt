package com.codingschool.ideabase.model.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingschool.ideabase.model.data.Category
import com.codingschool.ideabase.model.data.Idea
import com.codingschool.ideabase.model.data.User

@Database(entities = arrayOf(User::class, Category::class), version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {

    abstract fun userDao(): UserDao

}