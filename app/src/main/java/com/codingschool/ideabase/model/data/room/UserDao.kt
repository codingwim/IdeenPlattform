package com.codingschool.ideabase.model.data.room

import androidx.room.*
import com.codingschool.ideabase.model.data.User
import io.reactivex.Flowable


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flowable<List<User>>

}