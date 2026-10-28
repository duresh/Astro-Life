package com.example.data.repository

import com.example.data.calc.AstrologyCalculator
import com.example.data.calc.PanchangaCalculator
import com.example.data.local.BirthProfileDao
import com.example.data.model.BirthChartResult
import com.example.data.model.BirthProfile
import com.example.data.model.DailyPanchanga
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class AstroRepository(private val profileDao: BirthProfileDao) {

    val allProfiles: Flow<List<BirthProfile>> = profileDao.getAllProfiles()

    fun searchProfiles(query: String): Flow<List<BirthProfile>> = profileDao.searchProfiles(query)

    suspend fun insertProfile(profile: BirthProfile): Long = profileDao.insertProfile(profile)

    suspend fun deleteProfile(profile: BirthProfile) = profileDao.deleteProfile(profile)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = profileDao.updateFavorite(id, isFavorite)

    suspend fun seedInitialProfilesIfEmpty() {
        if (profileDao.getProfileCount() == 0) {
            // Seed 2 authentic sample profiles
            profileDao.insertProfile(
                BirthProfile(
                    name = "මාතර උපත (1984.09.12)",
                    gender = "MALE",
                    birthYear = 1984,
                    birthMonth = 9,
                    birthDay = 12,
                    birthHour = 8,
                    birthMinute = 50,
                    locationName = "මාතර (Matara)",
                    latitude = 5.9549,
                    longitude = 80.5550,
                    notes = "තුලා ලග්නය - කුජ 2 වැන්නේ, සිකුරු 12 වැන්නේ",
                    isFavorite = true
                )
            )
            profileDao.insertProfile(
                BirthProfile(
                    name = "දිනුෂි විජේසිංහ (Dinushi)",
                    gender = "FEMALE",
                    birthYear = 1999,
                    birthMonth = 11,
                    birthDay = 24,
                    birthHour = 14,
                    birthMinute = 20,
                    locationName = "මහනුවර (Kandy)",
                    latitude = 7.2906,
                    longitude = 80.6337,
                    notes = "උසස් අධ්‍යාපන සහ රැකියා පලාපල",
                    isFavorite = false
                )
            )
        }
    }

    fun calculateBirthChart(profile: BirthProfile): BirthChartResult {
        return AstrologyCalculator.calculateBirthChart(
            name = profile.name,
            gender = profile.gender,
            year = profile.birthYear,
            month = profile.birthMonth,
            day = profile.birthDay,
            hour = profile.birthHour,
            minute = profile.birthMinute,
            locationName = profile.locationName,
            latitude = profile.latitude,
            longitude = profile.longitude
        )
    }

    fun getTodayPanchanga(): DailyPanchanga {
        return PanchangaCalculator.getDailyPanchanga(Calendar.getInstance())
    }
}
