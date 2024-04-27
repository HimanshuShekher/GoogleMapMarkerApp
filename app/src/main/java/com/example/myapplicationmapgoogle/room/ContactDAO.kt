package com.example.myapplicationmapgoogle.room


import androidx.room.*

@Dao
interface ContactDAO {
    @Insert
     fun insertDataClass(dataClass: Contact)

    @Query("SELECT*FROM contact")
    fun getContact():List<Contact>
}