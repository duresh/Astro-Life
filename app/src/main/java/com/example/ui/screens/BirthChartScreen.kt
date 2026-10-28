package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StarBlue
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthChartScreen(
    activeChart: BirthChartResult?,
    isSinhala: Boolean,
    onCalculateChart: (String, String, Int, Int, Int, Int, Int, CityLocation) -> Unit,
    onSaveChart: (BirthProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInputForm by remember { mutableStateOf(activeChart == null) }

    val currentCal = remember { Calendar.getInstance() }

    // Form inputs default to current time
    var nameInput by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("MALE") }
    var birthYear by remember { mutableIntStateOf(currentCal.get(Calendar.YEAR)) }
    var birthMonth by remember { mutableIntStateOf(currentCal.get(Calendar.MONTH) + 1) }
    var birthDay by remember { mutableIntStateOf(currentCal.get(Calendar.DAY_OF_MONTH)) }
    var birthHour by remember { mutableIntStateOf(currentCal.get(Calendar.HOUR_OF_DAY)) }
    var birthMinute by remember { mutableIntStateOf(currentCal.get(Calendar.MINUTE)) }
    var selectedCity by remember { mutableStateOf<CityLocation>(CityLocation.SRI_LANKAN_CITIES.first()) }
    var isCityDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDetailTab by remember { mutableIntStateOf(0) }
    var savedSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val initialMillis = remember(birthYear, birthMonth, birthDay) {
        val c = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        c.set(birthYear, birthMonth - 1, birthDay, 12, 0, 0)
        c.timeInMillis
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    val timePickerState = rememberTimePickerState(initialHour = birthHour, initialMinute = birthMinute, is24Hour = false)

    // Calendar DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val c = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
                            birthYear = c.get(Calendar.YEAR)
                            birthMonth = c.get(Calendar.MONTH) + 1
                            birthDay = c.get(Calendar.DAY_OF_MONTH)
                        }
                        showDatePicker = false
                    },
                    modifier = Modifier.testTag("confirm_date_picker_button")
                ) {
                    Text(if (isSinhala) "තහවුරු කරන්න" else "Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(if (isSinhala) "අවලංගුයි" else "Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Clock TimePicker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        birthHour = timePickerState.hour
                        birthMinute = timePickerState.minute
                        showTimePicker = false
                    },
                    modifier = Modifier.testTag("confirm_time_picker_button")
                ) {
                    Text(if (isSinhala) "තහවුරු කරන්න" else "Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(if (isSinhala) "අවලංගුයි" else "Cancel")
                }
            },
            title = {
                Text(
                    text = if (isSinhala) "උපන් වේලාව තෝරන්න (Clock)" else "Select Birth Time",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("birth_chart_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle Input Form Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showInputForm = !showInputForm }
                    .testTag("toggle_input_form_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isSinhala) "උපන් විස්තර ඇතුළත් කිරීම" else "Birth Details Input",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (showInputForm) {
                                    if (isSinhala) "පෝරමය සැඟවීමට තට්ටු කරන්න" else "Tap to collapse form"
                                } else {
                                    if (isSinhala) "නව කේන්ද්‍රයක් සැකසීමට තට්ටු කරන්න" else "Tap to edit or generate new chart"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = if (showInputForm) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Expandable Input Form
        if (showInputForm) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("birth_input_form"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isSinhala) "ජන්ම පත්‍රය සඳහා උපන් තොරතුරු" else "Enter Birth Information",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Name input
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text(if (isSinhala) "නම (Name)" else "Full Name") },
                            placeholder = { Text(if (isSinhala) "උදා: කසුන් පෙරේරා" else "e.g. Kasun Perera") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("birth_name_input"),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        )

                        // Gender Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                Triple("MALE", if (isSinhala) "පුරුෂ (Male)" else "Male", Icons.Default.Male),
                                Triple("FEMALE", if (isSinhala) "ස්ත්‍රී (Female)" else "Female", Icons.Default.Female)
                            ).forEach { (genderCode, label, icon) ->
                                val isSelected = selectedGender == genderCode
                                OutlinedButton(
                                    onClick = { selectedGender = genderCode },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("gender_button_$genderCode"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        // Quick Current Time Button
                        OutlinedButton(
                            onClick = {
                                val c = Calendar.getInstance()
                                birthYear = c.get(Calendar.YEAR)
                                birthMonth = c.get(Calendar.MONTH) + 1
                                birthDay = c.get(Calendar.DAY_OF_MONTH)
                                birthHour = c.get(Calendar.HOUR_OF_DAY)
                                birthMinute = c.get(Calendar.MINUTE)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("use_current_time_form_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = GoldAccent.copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSinhala) "🕒 වත්මන් වේලාව යොදන්න (Use Current Time)" else "🕒 Set to Current Time",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // --- CONVENIENT CALENDAR DATE PICKER BUTTON ---
                        val sinhalaMonths = listOf("ජනවාරි", "පෙබරවාරි", "මාර්තු", "අප්‍රේල්", "මැයි", "ජූනි", "ජූලි", "අගෝස්තු", "සැප්තැම්බර්", "ඔක්තෝබර්", "නොවැම්බර්", "දෙසැම්බර්")
                        val monthName = sinhalaMonths.getOrElse(birthMonth - 1) { "$birthMonth" }

                        OutlinedCard(
                            onClick = { showDatePicker = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_calendar_picker_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Calendar",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isSinhala) "උපන් දිනය (කැලැන්ඩරය)" else "Birth Date (Calendar)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = if (isSinhala) "$birthYear $monthName $birthDay ($birthYear-%02d-%02d)".format(birthMonth, birthDay)
                                            else "$birthYear-%02d-%02d".format(birthMonth, birthDay),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                TextButton(onClick = { showDatePicker = true }) {
                                    Text(if (isSinhala) "වෙනස් කරන්න" else "Change", fontSize = 12.sp)
                                }
                            }
                        }

                        // Fine-tuning Number Adjusters for Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NumberWheelSelector(
                                label = if (isSinhala) "වසර" else "Year",
                                value = birthYear,
                                range = 1920..2026,
                                onValueChange = { birthYear = it },
                                modifier = Modifier.weight(1.3f)
                            )
                            NumberWheelSelector(
                                label = if (isSinhala) "මස" else "Month",
                                value = birthMonth,
                                range = 1..12,
                                onValueChange = { birthMonth = it },
                                modifier = Modifier.weight(1f)
                            )
                            NumberWheelSelector(
                                label = if (isSinhala) "දින" else "Day",
                                value = birthDay,
                                range = 1..31,
                                onValueChange = { birthDay = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // --- CONVENIENT CLOCK TIME PICKER BUTTON ---
                        val amPm = if (birthHour < 12) (if (isSinhala) "පෙ.ව." else "AM") else (if (isSinhala) "ප.ව." else "PM")
                        val displayHour12 = if (birthHour == 0) 12 else if (birthHour > 12) birthHour - 12 else birthHour

                        OutlinedCard(
                            onClick = { showTimePicker = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_clock_picker_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Clock",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isSinhala) "උපන් වේලාව (ඔරලෝසුව)" else "Birth Time (Clock)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$amPm %02d:%02d (%02d:%02d පැය 24)".format(displayHour12, birthMinute, birthHour, birthMinute),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                                TextButton(onClick = { showTimePicker = true }) {
                                    Text(if (isSinhala) "වෙනස් කරන්න" else "Change", fontSize = 12.sp)
                                }
                            }
                        }

                        // Fine-tuning Number Adjusters for Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NumberWheelSelector(
                                label = if (isSinhala) "පැය (0-23)" else "Hour",
                                value = birthHour,
                                range = 0..23,
                                onValueChange = { birthHour = it },
                                modifier = Modifier.weight(1f)
                            )
                            NumberWheelSelector(
                                label = if (isSinhala) "මිනිත්තු" else "Minute",
                                value = birthMinute,
                                range = 0..59,
                                onValueChange = { birthMinute = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Birth Location Dropdown
                        Text(
                            text = if (isSinhala) "උපන් ස්ථානය (Birth Location)" else "Birth Location (City)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        ExposedDropdownMenuBox(
                            expanded = isCityDropdownExpanded,
                            onExpandedChange = { isCityDropdownExpanded = !isCityDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = if (isSinhala) selectedCity.sinhalaName else selectedCity.englishName,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCityDropdownExpanded) },
                                leadingIcon = {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("birth_city_selector")
                            )

                            ExposedDropdownMenu(
                                expanded = isCityDropdownExpanded,
                                onDismissRequest = { isCityDropdownExpanded = false }
                            ) {
                                CityLocation.SRI_LANKAN_CITIES.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(if (isSinhala) city.sinhalaName else city.englishName) },
                                        onClick = {
                                            selectedCity = city
                                            isCityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                onCalculateChart(
                                    nameInput.ifBlank { if (isSinhala) "ජන්ම පත්‍රය" else "Birth Chart" },
                                    selectedGender,
                                    birthYear,
                                    birthMonth,
                                    birthDay,
                                    birthHour,
                                    birthMinute,
                                    selectedCity
                                )
                                showInputForm = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("calculate_chart_submit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSinhala) "කේන්ද්‍රය සාදන්න (Generate Chart)" else "Generate Birth Chart",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Chart Display
        if (activeChart != null) {
            // General Overview Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chart_summary_header"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = activeChart.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${activeChart.birthDateFormatted} | ${activeChart.birthTimeFormatted} | ${activeChart.locationName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Save Chart Button
                            IconButton(
                                onClick = {
                                    val parts = activeChart.birthDateFormatted.split("-")
                                    val timeParts = activeChart.birthTimeFormatted.split(":")
                                    val profile = BirthProfile(
                                        name = activeChart.name,
                                        gender = activeChart.gender,
                                        birthYear = parts.getOrNull(0)?.toIntOrNull() ?: 1998,
                                        birthMonth = parts.getOrNull(1)?.toIntOrNull() ?: 1,
                                        birthDay = parts.getOrNull(2)?.toIntOrNull() ?: 1,
                                        birthHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 10,
                                        birthMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0,
                                        locationName = activeChart.locationName,
                                        latitude = 6.9271,
                                        longitude = 79.8612,
                                        isFavorite = false
                                    )
                                    onSaveChart(profile)
                                    savedSuccessMessage = if (isSinhala) "කේන්ද්‍රය සාර්ථකව සුරකින ලදී!" else "Chart saved to local storage!"
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .testTag("save_current_chart_button")
                            ) {
                                Icon(
                                    Icons.Default.BookmarkAdd,
                                    contentDescription = "Save Chart",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        savedSuccessMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "✓ $msg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4 Key Badges: Lagna, Moon Sign, Nakshatra, Pada
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BadgeItem(
                                label = if (isSinhala) "ලග්නය" else "Lagna",
                                value = if (isSinhala) activeChart.lagnaSign.sinhalaName else activeChart.lagnaSign.englishName,
                                modifier = Modifier.weight(1f)
                            )
                            BadgeItem(
                                label = if (isSinhala) "ජන්ම රාශිය" else "Moon Sign",
                                value = if (isSinhala) activeChart.moonSign.sinhalaName else activeChart.moonSign.englishName,
                                modifier = Modifier.weight(1f)
                            )
                            BadgeItem(
                                label = if (isSinhala) "නැකත" else "Nakshatra",
                                value = if (isSinhala) activeChart.moonNakshatra.sinhalaName else activeChart.moonNakshatra.englishName,
                                modifier = Modifier.weight(1f)
                            )
                            BadgeItem(
                                label = if (isSinhala) "පාදය" else "Pada",
                                value = "${activeChart.moonPada}",
                                modifier = Modifier.weight(0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isSinhala) activeChart.generalSummarySinhala else activeChart.generalSummaryEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Traditional Sri Lankan Chakra Box View
            item {
                SriLankanChakraView(
                    result = activeChart,
                    isSinhala = isSinhala
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Date & Time Steppers (Matching screenshot exactly)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("datetime_steppers_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Day Stepper
                        DateTimeStepperBox(
                            value = String.format("%02d", birthDay),
                            label = if (isSinhala) "දින" else "Day",
                            onIncrement = {
                                birthDay = if (birthDay < 31) birthDay + 1 else 1
                                onCalculateChart(nameInput, selectedGender, birthYear, birthMonth, birthDay, birthHour, birthMinute, selectedCity)
                            },
                            onDecrement = {
                                birthDay = if (birthDay > 1) birthDay - 1 else 31
                                onCalculateChart(nameInput, selectedGender, birthYear, birthMonth, birthDay, birthHour, birthMinute, selectedCity)
                            },
                            isHighlight = true
                        )

                        // Hour Stepper
                        DateTimeStepperBox(
                            value = String.format("%02d", birthHour),
                            label = if (isSinhala) "පැය" else "Hour",
                            onIncrement = {
                                birthHour = (birthHour + 1) % 24
                                onCalculateChart(nameInput, selectedGender, birthYear, birthMonth, birthDay, birthHour, birthMinute, selectedCity)
                            },
                            onDecrement = {
                                birthHour = if (birthHour > 0) birthHour - 1 else 23
                                onCalculateChart(nameInput, selectedGender, birthYear, birthMonth, birthDay, birthHour, birthMinute, selectedCity)
                            }
                        )

                        Text(":", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Minute Stepper
                        DateTimeStepperBox(
                            value = String.format("%02d", birthMinute),
                            label = if (isSinhala) "මිනි." else "Min",
                            onIncrement = {
                                birthMinute = (birthMinute + 1) % 60
                                onCalculateChart(nameInput, selectedGender, birthYear, birthMonth, birthDay, birthHour, birthMinute, selectedCity)
                            },
                            onDecrement = {
                                birthMinute = if (birthMinute > 0) birthMinute - 1 else 59
                                onCalculateChart(nameInput, selectedGender, birthYear, birthMonth, birthDay, birthHour, birthMinute, selectedCity)
                            }
                        )

                        Text(":", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Second Stepper (visual placeholder)
                        DateTimeStepperBox(
                            value = "00",
                            label = if (isSinhala) "තත්." else "Sec",
                            onIncrement = {},
                            onDecrement = {}
                        )
                    }
                }
            }

            // Insight Sub-Tabs Selector
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedDetailTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("insight_subtabs_row")
                ) {
                    val tabs = listOf(
                        Pair(if (isSinhala) "විංශෝත්තරී දශා & ජීවිත විග්‍රහය" else "Vimshottari Dasha & Life Analysis", Icons.Default.HourglassTop),
                        Pair(if (isSinhala) "විදේශ, ධන, වාහන & සෞඛ්‍යය" else "Destiny, Travel, Wealth & Health", Icons.Default.Explore),
                        Pair(if (isSinhala) "යෝග සහ අනාගතය" else "Yogas & Future", Icons.Default.AutoAwesome),
                        Pair(if (isSinhala) "සහකරු & දරුවන්" else "Spouse & Children", Icons.Default.Favorite),
                        Pair(if (isSinhala) "භාව 12 සහ ග්‍රහයින්" else "12 Houses & Planets", Icons.Default.TableChart)
                    )

                    tabs.forEachIndexed { index, (title, icon) ->
                        val isSelected = selectedDetailTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedDetailTab = index },
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .testTag("subtab_$index"),
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedDetailTab) {
                0 -> {
                    // Vimshottari Dasha and Full Life Analysis (Current Dasha/Bhukti, Inauspicious periods, Doshas, Longevity, Past Life, Prosperity)
                    item {
                        VimshottariDashaView(
                            dashaPeriods = activeChart.dashaPeriods,
                            analysis = activeChart.vimsottariAnalysis,
                            isSinhala = isSinhala
                        )
                    }
                }
                1 -> {
                    // Foreign Travel, Wealth, Property & Vehicles, Triumphs & Setbacks, Family Gains, Health & Meritorious Deeds
                    item {
                        LifeDestinyView(
                            lifeDestiny = activeChart.lifeDestiny,
                            isSinhala = isSinhala
                        )
                    }
                }
                2 -> {
                    // Yogas and how future unfolds
                    item {
                        YogasCardList(
                            yogas = activeChart.yogas,
                            isSinhala = isSinhala
                        )
                    }
                }
                3 -> {
                    // Spouse and Children predictions with timelines
                    item {
                        SpouseChildrenCard(
                            marriage = activeChart.marriagePrediction,
                            children = activeChart.childrenPrediction,
                            isSinhala = isSinhala
                        )
                    }
                }
                4 -> {
                    // 12 Houses and Planetary Positions Table
                    item {
                        HousesAndPlanetsTable(
                            result = activeChart,
                            isSinhala = isSinhala
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NumberWheelSelector(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { if (value > range.first) onValueChange(value - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
            }
            Text(
                text = "$value",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { if (value < range.last) onValueChange(value + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun HousesAndPlanetsTable(
    result: BirthChartResult,
    isSinhala: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("houses_and_planets_table"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isSinhala) "නවාග්‍රහ පිහිටීම (Planetary Dignities)" else "Planetary Positions & Dignities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            result.planetaryPositions.forEach { pos ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSinhala) pos.planet.sinhalaName else pos.planet.englishName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.width(60.dp)
                        )
                        if (pos.isRetrograde) {
                            Text(
                                text = if (isSinhala) "(ව)" else "(R)",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "${if (isSinhala) pos.sign.sinhalaName else pos.sign.englishName} ${pos.degreeInSign}°${pos.minuteInSign}'",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (pos.dignity) {
                                    Dignity.EXALTED -> GoldAccent.copy(alpha = 0.25f)
                                    Dignity.DEBILITATED -> MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
                                    Dignity.OWN_SIGN -> StarBlue.copy(alpha = 0.25f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSinhala) pos.dignity.sinhalaName else pos.dignity.englishName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (pos.dignity) {
                                Dignity.EXALTED -> GoldAccent
                                Dignity.DEBILITATED -> MaterialTheme.colorScheme.error
                                Dignity.OWN_SIGN -> StarBlue
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    Text(
                        text = if (isSinhala) "${pos.house} වැන්න" else "H${pos.house}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun DateTimeStepperBox(
    value: String,
    label: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    isHighlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(58.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isHighlight) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        IconButton(
            onClick = onIncrement,
            modifier = Modifier.size(26.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", modifier = Modifier.size(18.dp))
        }

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        IconButton(
            onClick = onDecrement,
            modifier = Modifier.size(26.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", modifier = Modifier.size(18.dp))
        }
    }
}
