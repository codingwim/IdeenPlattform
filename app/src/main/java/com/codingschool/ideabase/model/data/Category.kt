package com.codingschool.ideabase.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(

    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "category_nameE")
    val name_en: String,
    @ColumnInfo(name = "category_nameD")
    val name_de: String
)
