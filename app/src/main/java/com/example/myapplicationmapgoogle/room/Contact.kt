package com.example.myapplicationmapgoogle.room


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val relation: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
