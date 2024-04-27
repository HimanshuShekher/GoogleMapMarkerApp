package com.example.myapplicationmapgoogle.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 1)//Link entities class means data class
abstract class ContactDatabase:RoomDatabase() {
    abstract fun contactDao():ContactDAO


}