package com.codingschool.ideabase.ui.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "category")
data class Category(


    @ColumnInfo(name = "category_id")
    val id: String,

    @ColumnInfo(name = "category_nameE")
    val name_en: String,
    @ColumnInfo(name = "category_nameD")
    val name_de: String,
)
