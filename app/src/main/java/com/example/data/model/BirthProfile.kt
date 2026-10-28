package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "birth_profiles")
data class BirthProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String, // "MALE", "FEMALE", "OTHER"
    val birthYear: Int,
    val birthMonth: Int, // 1 - 12
    val birthDay: Int, // 1 - 31
    val birthHour: Int, // 0 - 23
    val birthMinute: Int, // 0 - 59
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
