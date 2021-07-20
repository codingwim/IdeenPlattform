package com.codingschool.ideabase.ui.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codingschool.ideabase.ui.data.User


abstract class AppDataBase: RoomDatabase() {

    abstract fun useryDao(): UserDao

}