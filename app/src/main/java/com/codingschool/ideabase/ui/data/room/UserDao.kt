package com.codingschool.ideabase.ui.data.room

import androidx.room.*
import com.codingschool.ideabase.ui.data.User


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user")
    suspend fun getAllCities(): List<User>



}