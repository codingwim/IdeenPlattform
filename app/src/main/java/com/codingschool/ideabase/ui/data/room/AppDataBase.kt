package com.codingschool.ideabase.ui.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codingschool.ideabase.ui.data.User

@Database(entities = arrayOf((User::class)), version =1)
abstract class AppDataBase: RoomDatabase() {

    abstract fun userDao(): UserDao

}