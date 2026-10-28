package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuspiciousMuhurtha
import com.example.data.model.DailyPanchanga
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RubyRed
import com.example.ui.theme.StarBlue

@Composable
fun CalendarScreen(
    panchanga: DailyPanchanga,
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showRahuTable by remember { mutableStateOf(false) }

    val categories = listOf(
        Pair("ALL", if (isSinhala) "සියල්ල" else "All"),
        Pair("MARRIAGE", if (isSinhala) "විවාහ" else "Weddings"),
        Pair("HOUSE", if (isSinhala) "ගෙවදීම්" else "House"),
        Pair("BUSINESS", if (isSinhala) "ව්‍යාපාර" else "Business"),
        Pair("VEHICLE", if (isSinhala) "වාහන" else "Vehicles"),
        Pair("EDUCATION", if (isSinhala) "අකුරු කියවීම" else "Education")
    )

    val filteredMuhurthas = remember(selectedCategory, panchanga.auspiciousTimes) {
        if (selectedCategory == "ALL") panchanga.auspiciousTimes
        else panchanga.auspiciousTimes.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Litha Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isSinhala) "ජ්‍යෝතිෂ ලිත සහ සුබ නැකත්" else "Astrological Litha & Muhurthas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${panchanga.dateFormatted} | ${if (isSinhala) panchanga.dayOfWeekSinhala else panchanga.dayOfWeekEnglish}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sri Lanka Poya Day alert
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = StarBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSinhala) "අද දින තිථිය: ${panchanga.tithiSinhala} (පුර/අව පක්ෂය)" else "Current Lunar Phase: ${panchanga.tithiEnglish}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Rahu Kalaya Reference Table Toggle
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rahu_kalaya_toggle_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = RubyRed.copy(alpha = 0.1f)
                ),
                border = BorderStroke(1.dp, RubyRed.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = RubyRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSinhala) "සතියේ රාහු කාල සටහන (දිවා)" else "Weekly Rahu Kalaya Table",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = RubyRed
                            )
                        }
                        TextButton(
                            onClick = { showRahuTable = !showRahuTable },
                            modifier = Modifier.testTag("toggle_rahu_table_button")
                        ) {
                            Text(
                                text = if (showRahuTable) (if (isSinhala) "සඟවන්න" else "Hide") else (if (isSinhala) "බලන්න" else "View"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (showRahuTable) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val weeklyRahu = listOf(
                            Triple("ඉරිදා", "Sunday", "16:30 - 18:00"),
                            Triple("සඳුදා", "Monday", "07:30 - 09:00"),
                            Triple("අඟහරුවාදා", "Tuesday", "15:00 - 16:30"),
                            Triple("බදාදා", "Wednesday", "12:00 - 13:30"),
                            Triple("බ්‍රහස්පතින්දා", "Thursday", "13:30 - 15:00"),
                            Triple("සිකුරාදා", "Friday", "10:30 - 12:00"),
                            Triple("සෙනසුරාදා", "Saturday", "09:00 - 10:30")
                        )

                        weeklyRahu.forEach { (sinDay, engDay, time) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isSinhala) sinDay else engDay,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = time,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RubyRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Auspicious Muhurthas Section
        item {
            Column {
                Text(
                    text = if (isSinhala) "ඉදිරි සුබ මුහුර්ත (නැකත්)" else "Upcoming Auspicious Muhurthas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { (code, label) ->
                        val isSelected = selectedCategory == code
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = code },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("muhurtha_filter_$code")
                        )
                    }
                }
            }
        }

        // Muhurtha Cards List
        items(filteredMuhurthas) { muhurtha ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("muhurtha_card_${muhurtha.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSinhala) muhurtha.titleSinhala else muhurtha.titleEnglish,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = muhurtha.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = muhurtha.dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldAccent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = muhurtha.timeRange, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSinhala) "සුබ දිශාව: ${muhurtha.subhaDirectionSinhala}" else "Auspicious Direction: ${muhurtha.subhaDirectionEnglish}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarBlue
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isSinhala) muhurtha.descriptionSinhala else muhurtha.descriptionEnglish,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
