package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.calc.AstrologyCalculator
import com.example.data.local.AstroDatabase
import com.example.data.model.BirthChartResult
import com.example.data.model.BirthProfile
import com.example.data.model.CityLocation
import com.example.data.model.DailyPanchanga
import com.example.data.repository.AstroRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class AstroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AstroRepository

    // App Preferences
    private val _isSinhala = MutableStateFlow(true) // Sinhala primary as requested
    val isSinhala: StateFlow<Boolean> = _isSinhala.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Navigation state
    private val _currentTab = MutableStateFlow(0) // 0: Dashboard, 1: Chart, 2: Calendar, 3: Profiles, 4: Search
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Active Birth Chart in Viewer
    private val _activeChart = MutableStateFlow<BirthChartResult?>(null)
    val activeChart: StateFlow<BirthChartResult?> = _activeChart.asStateFlow()

    // Daily Litha / Panchanga
    private val _todayPanchanga = MutableStateFlow<DailyPanchanga>(
        repository_placeholder_panchanga()
    )
    val todayPanchanga: StateFlow<DailyPanchanga> = _todayPanchanga.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val savedProfiles: StateFlow<List<BirthProfile>>

    init {
        val db = AstroDatabase.getDatabase(application)
        repository = AstroRepository(db.birthProfileDao())

        savedProfiles = repository.allProfiles
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Seed initial profiles and default chart
        viewModelScope.launch {
            repository.seedInitialProfilesIfEmpty()
            _todayPanchanga.value = repository.getTodayPanchanga()

            // Initialize default active chart with a sample profile
            val sample = BirthProfile(
                name = "මාතර උපත (1984.09.12)",
                gender = "MALE",
                birthYear = 1984,
                birthMonth = 9,
                birthDay = 12,
                birthHour = 8,
                birthMinute = 50,
                locationName = "මාතර (Matara)",
                latitude = 5.9549,
                longitude = 80.5550
            )
            _activeChart.value = repository.calculateBirthChart(sample)
        }
    }

    fun toggleLanguage() {
        _isSinhala.value = !_isSinhala.value
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun calculateAndSetChart(
        name: String,
        gender: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        city: CityLocation
    ) {
        val result = AstrologyCalculator.calculateBirthChart(
            name = name.ifBlank { if (_isSinhala.value) "ජන්ම පත්‍රය" else "Birth Chart" },
            gender = gender,
            year = year,
            month = month,
            day = day,
            hour = hour,
            minute = minute,
            locationName = if (_isSinhala.value) city.sinhalaName else city.englishName,
            latitude = city.latitude,
            longitude = city.longitude,
            timezoneOffset = city.timezoneOffset
        )
        _activeChart.value = result
        _currentTab.value = 1 // Switch to Chart view
    }

    fun saveProfile(profile: BirthProfile) {
        viewModelScope.launch {
            repository.insertProfile(profile)
        }
    }

    fun deleteProfile(profile: BirthProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }

    fun toggleFavorite(profile: BirthProfile) {
        viewModelScope.launch {
            repository.toggleFavorite(profile.id, !profile.isFavorite)
        }
    }

    fun loadProfileIntoChart(profile: BirthProfile) {
        val result = repository.calculateBirthChart(profile)
        _activeChart.value = result
        _currentTab.value = 1 // Switch to chart tab
    }

    companion object {
        private fun repository_placeholder_panchanga(): DailyPanchanga {
            return com.example.data.calc.PanchangaCalculator.getDailyPanchanga(Calendar.getInstance())
        }
    }
}
