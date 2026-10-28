package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.AstroLifeTheme
import com.example.ui.viewmodel.AstroViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AstroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val isSinhala by viewModel.isSinhala.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val activeChart by viewModel.activeChart.collectAsStateWithLifecycle()
            val savedProfiles by viewModel.savedProfiles.collectAsStateWithLifecycle()
            val todayPanchanga by viewModel.todayPanchanga.collectAsStateWithLifecycle()

            // Back button handling: return to dashboard if on another tab
            BackHandler(enabled = currentTab != 0) {
                viewModel.setTab(0)
            }

            AstroLifeTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            val screenTitle = when (currentTab) {
                                0 -> if (isSinhala) "Astro-Life ජ්‍යෝතිෂය" else "Astro-Life"
                                1 -> if (isSinhala) "ජන්ම කේන්ද්‍රය" else "Birth Chart"
                                2 -> if (isSinhala) "අවුරුදු ලිත" else "Litha & Muhurthas"
                                3 -> if (isSinhala) "සුරැකි කේන්ද්‍ර" else "Saved Profiles"
                                4 -> if (isSinhala) "ජ්‍යෝතිෂ සෙවුම" else "Astrology Search"
                                else -> "Astro-Life"
                            }
                            AppTopBar(
                                title = screenTitle,
                                isSinhala = isSinhala,
                                isDarkMode = isDarkMode,
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onToggleDarkMode = { viewModel.toggleDarkMode() }
                            )
                        },
                        bottomBar = {
                            AstroBottomNavigation(
                                currentTab = currentTab,
                                isSinhala = isSinhala,
                                onTabSelected = { viewModel.setTab(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                0 -> DashboardScreen(
                                    panchanga = todayPanchanga,
                                    savedProfiles = savedProfiles,
                                    isSinhala = isSinhala,
                                    onNavigateToCreateChart = { viewModel.setTab(1) },
                                    onSelectProfile = { profile -> viewModel.loadProfileIntoChart(profile) },
                                    onNavigateToCalendar = { viewModel.setTab(2) },
                                    onUseCurrentTimeChart = { viewModel.generateCurrentTimeChartAndShow() }
                                )
                                1 -> BirthChartScreen(
                                    activeChart = activeChart,
                                    isSinhala = isSinhala,
                                    onCalculateChart = { name, gender, y, m, d, h, min, city ->
                                        viewModel.calculateAndSetChart(name, gender, y, m, d, h, min, city)
                                    },
                                    onSaveChart = { profile ->
                                        viewModel.saveProfile(profile)
                                    }
                                )
                                2 -> CalendarScreen(
                                    panchanga = todayPanchanga,
                                    isSinhala = isSinhala
                                )
                                3 -> ProfilesScreen(
                                    profiles = savedProfiles,
                                    isSinhala = isSinhala,
                                    onSelectProfile = { profile -> viewModel.loadProfileIntoChart(profile) },
                                    onToggleFavorite = { profile -> viewModel.toggleFavorite(profile) },
                                    onDeleteProfile = { profile -> viewModel.deleteProfile(profile) },
                                    onNavigateToCreate = { viewModel.setTab(1) }
                                )
                                4 -> SearchScreen(
                                    isSinhala = isSinhala
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AstroBottomNavigation(
    currentTab: Int,
    isSinhala: Boolean,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("astro_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(0, if (isSinhala) "මුල් පිටුව" else "Home", Icons.Default.Dashboard),
            Triple(1, if (isSinhala) "කේන්ද්‍රය" else "Chart", Icons.Default.AutoAwesome),
            Triple(2, if (isSinhala) "ලිත" else "Litha", Icons.Default.CalendarMonth),
            Triple(3, if (isSinhala) "සුරැකි" else "Saved", Icons.Default.Bookmarks),
            Triple(4, if (isSinhala) "සෙවුම" else "Search", Icons.Default.Search)
        )

        navItems.forEach { (index, title, icon) ->
            val isSelected = currentTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("nav_tab_$index")
            )
        }
    }
}
