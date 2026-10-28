package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StarBlue

@Composable
fun VimshottariDashaView(
    dashaPeriods: List<DashaPeriod>,
    analysis: VimshottariDashaAnalysis?,
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vimshottari_dasha_module"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Navigation Pills
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {},
            modifier = Modifier.fillMaxWidth().testTag("vimshottari_section_tabs")
        ) {
            val sections = listOf(
                Pair(if (isSinhala) "දශා සහ භුක්ති" else "Dasha & Bhukti", Icons.Default.HourglassTop),
                Pair(if (isSinhala) "අසුබ සහ ප්‍රවේශම් විය යුතු කාල" else "Inauspicious Periods", Icons.Default.WarningAmber),
                Pair(if (isSinhala) "දෝෂ ජීවිතය" else "Life Doshas", Icons.Default.Shield),
                Pair(if (isSinhala) "ආයුෂ සහ මාරක" else "Longevity & Maraka", Icons.Default.HealthAndSafety),
                Pair(if (isSinhala) "පෙර භවය" else "Past Life Karma", Icons.Default.Psychology),
                Pair(if (isSinhala) "දියුණුවට කළ යුතු දේ" else "Prosperity Guidance", Icons.Default.Lightbulb)
            )

            sections.forEachIndexed { index, (title, icon) ->
                val isSelected = selectedSection == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedSection = index },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        )
                        .testTag("vimshottari_tab_$index"),
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

        // Section Content
        when (selectedSection) {
            0 -> {
                // Current Dasha/Bhukti card + Full 81 Bhukti Timeline
                analysis?.currentDashaBhukti?.let { current ->
                    CurrentDashaBhuktiCard(current = current, isSinhala = isSinhala)
                }
                DashaBhuktiTimelineCard(dashaPeriods = dashaPeriods, isSinhala = isSinhala)
            }
            1 -> {
                // Inauspicious & Erashtaka Periods
                InauspiciousPeriodsCard(
                    periods = analysis?.inauspiciousPeriods ?: emptyList(),
                    isSinhala = isSinhala
                )
            }
            2 -> {
                // Life Doshas
                LifeDoshasCard(
                    doshas = analysis?.lifeDoshas ?: emptyList(),
                    isSinhala = isSinhala
                )
            }
            3 -> {
                // Longevity & Maraka
                analysis?.longevityMaraka?.let { longevity ->
                    LongevityMarakaCard(longevity = longevity, isSinhala = isSinhala)
                }
            }
            4 -> {
                // Past Life Karma
                analysis?.pastLifeKarma?.let { pastLife ->
                    PastLifeKarmaCard(pastLife = pastLife, isSinhala = isSinhala)
                }
            }
            5 -> {
                // Prosperity Guidance & Remedies
                analysis?.prosperityGuidance?.let { prosperity ->
                    ProsperityGuidanceCard(prosperity = prosperity, isSinhala = isSinhala)
                }
            }
        }
    }
}

/**
 * Visual highlight card for the native's currently active Major and Minor planetary periods.
 */
@Composable
fun CurrentDashaBhuktiCard(
    current: CurrentDashaBhuktiInfo,
    isSinhala: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("current_dasha_bhukti_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GoldAccent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSinhala) "දැනට ක්‍රියාකාරී දශාව සහ භුක්තිය" else "Currently Active Dasha & Bhukti",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldAccent.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (isSinhala) "දින ${current.remainingDays}ක් ඉතිරියි" else "${current.remainingDays} days left",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Major & Minor Period Hero Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isSinhala) "මහා දශාව" else "Major Dasha",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isSinhala) current.majorPlanet.sinhalaName else current.majorPlanet.englishName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${current.majorStartDateStr} - ${current.majorEndDateStr}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isSinhala) "අන්තර් දශාව (භුක්තිය)" else "Minor Bhukti",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isSinhala) current.subPlanet.sinhalaName else current.subPlanet.englishName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = StarBlue
                    )
                    Text(
                        text = "${current.bhuktiStartDateStr} - ${current.bhuktiEndDateStr}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isSinhala) "භුක්ති කාලය ගෙවී යාම" else "Bhukti Elapsed Progress",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${"%.1f".format(current.elapsedPercentage)}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { current.elapsedPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isSinhala) current.overallInfluenceSinhala else current.overallInfluenceEnglish,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.TipsAndUpdates,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSinhala) current.guidanceSinhala else current.guidanceEnglish,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Interactive Dasha and 9-Bhukti hierarchical timeline card.
 */
@Composable
fun DashaBhuktiTimelineCard(
    dashaPeriods: List<DashaPeriod>,
    isSinhala: Boolean
) {
    var selectedDasha by remember {
        mutableStateOf(dashaPeriods.find { it.isCurrent } ?: dashaPeriods.firstOrNull())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dasha_bhukti_timeline_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isSinhala) "විංශෝත්තරී මහා දශා සහ අන්තර් දශා කාලසටහන" else "Vimshottari Mahadasha & Bhukti Hierarchy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isSinhala) "අදාළ මහා දශාව මත තට්ටු කර එහි අන්තර් දශා 9 සහ දින වකවානු බලන්න" else "Tap any Mahadasha to inspect all 9 Bhuktis with exact date windows",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mahadasha Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dashaPeriods) { dasha ->
                    val isSelected = selectedDasha == dasha
                    val isCurrent = dasha.isCurrent

                    Surface(
                        onClick = { selectedDasha = dasha },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isCurrent -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        },
                        border = BorderStroke(
                            if (isCurrent) 1.5.dp else 1.dp,
                            if (isCurrent) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("dasha_pill_${dasha.planet.name}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else GoldAccent)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (isSinhala) dasha.planet.sinhalaName else dasha.planet.englishName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${dasha.startYear} - ${dasha.endYear}",
                                fontSize = 10.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Bhuktis under selected Mahadasha
            selectedDasha?.let { dasha ->
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSinhala) "${dasha.planet.sinhalaName} මහ දශාවේ අන්තර් දශා (භුක්ති)" else "Sub-periods under ${dasha.planet.englishName} Mahadasha",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${dasha.startDateStr} සිට ${dasha.endDateStr}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    dasha.bhuktis.forEach { bhukti ->
                        var isExpanded by remember { mutableStateOf(false) }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                bhukti.isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                bhukti.isChallenging -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    bhukti.isCurrent -> GoldAccent
                                    bhukti.isChallenging -> MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                                .testTag("bhukti_row_${bhukti.majorPlanet.name}_${bhukti.subPlanet.name}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (bhukti.isCurrent) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(GoldAccent)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = if (isSinhala) "${bhukti.majorPlanet.sinhalaName} - ${bhukti.subPlanet.sinhalaName}"
                                            else "${bhukti.majorPlanet.englishName} - ${bhukti.subPlanet.englishName}",
                                            fontSize = 12.sp,
                                            fontWeight = if (bhukti.isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (bhukti.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (bhukti.isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GoldAccent.copy(alpha = 0.25f)
                                            ) {
                                                Text(
                                                    text = if (isSinhala) "ක්‍රියාකාරී" else "Active",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldAccent,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${bhukti.startDateStr}  ➔  ${bhukti.endDateStr}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                            thickness = 0.8.dp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isSinhala) bhukti.effectSinhala else bhukti.effectEnglish,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 16.sp
                                        )
                                        if (bhukti.isChallenging) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (isSinhala) "⚠️ සැලකිලිමත් විය යුතු කාලයකි: ග්‍රහ ශාන්තිකර්ම සහ ඉවසීමෙන් කටයුතු කිරීම යෝග්‍ය වේ."
                                                else "⚠️ Challenging transition: Practice patience and undertake planetary pacification.",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inauspicious & Caution Periods Card (ජීවිතයේ අසුබ කාල සහ ප්‍රවේශම් විය යුතු කාල).
 */
@Composable
fun InauspiciousPeriodsCard(
    periods: List<InauspiciousPeriod>,
    isSinhala: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inauspicious_periods_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isSinhala) "ජීවිතයේ අසුබ කාල සහ ප්‍රවේශම් විය යුතු කාල" else "Inauspicious & Caution Periods",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isSinhala) "ශනි ඒරාෂ්ටක, මාරක සහ දුෂ්ටස්ථාන අපල කාල පරාසයන්" else "Saturn Sade Sati, Erashtaka, and Maraka timeline windows",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            periods.forEach { period ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (period.severityLevel == "HIGH") MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("inauspicious_item_${period.categoryEnglish}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSinhala) period.titleSinhala else period.titleEnglish,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (period.severityLevel == "HIGH") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (period.severityLevel == "HIGH") MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = if (period.severityLevel == "HIGH") {
                                        if (isSinhala) "දැඩි අවධානයක්" else "High Caution"
                                    } else {
                                        if (isSinhala) "මධ්‍යම අවධානයක්" else "Moderate"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (period.severityLevel == "HIGH") MaterialTheme.colorScheme.onErrorContainer
                                    else MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📅 ${period.dateRangeStr}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = period.ageWindowStr,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isSinhala) period.warningDescriptionSinhala else period.warningDescriptionEnglish,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Precautions
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isSinhala) "⚠️ ප්‍රවේශම් විය යුතු කරුණු:" else "⚠️ Precautions to take:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = if (isSinhala) period.precautionsSinhala else period.precautionsEnglish,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Remedies
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isSinhala) "✨ කළ යුතු ශාන්තිකර්ම & පින්කම්:" else "✨ Astrological Remedies:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isSinhala) period.remediesSinhala else period.remediesEnglish,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Life Doshas Card (දෝෂ ජීවිතය - Kuja, Kalsarpa, Pitru, Guru Chandal, Vish).
 */
@Composable
fun LifeDoshasCard(
    doshas: List<LifeDoshaInfo>,
    isSinhala: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("life_doshas_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isSinhala) "දෝෂ ජීවිතය සහ ග්‍රහ බලපෑම්" else "Life Doshas & Planetary Afflictions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "කුජ, කාලසර්ප, පීතෘ, ගුරු චණ්ඩාල සහ විෂ දෝෂ විග්‍රහය" else "Detailed audit of Kuja, Kala Sarpa, Pitru, and other doshas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            doshas.forEach { dosha ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (dosha.isPresent && !dosha.intensityLevel.contains("භංග")) MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("dosha_item_${dosha.doshaNameEnglish}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSinhala) dosha.doshaNameSinhala else dosha.doshaNameEnglish,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    !dosha.isPresent -> MaterialTheme.colorScheme.primaryContainer
                                    dosha.intensityLevel.contains("භංග") -> GoldAccent.copy(alpha = 0.25f)
                                    else -> MaterialTheme.colorScheme.errorContainer
                                }
                            ) {
                                Text(
                                    text = dosha.intensityLevel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        !dosha.isPresent -> MaterialTheme.colorScheme.primary
                                        dosha.intensityLevel.contains("භංග") -> GoldAccent
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isSinhala) "බලපෑම: ${dosha.affectedLifeAreasSinhala}" else "Impact: ${dosha.affectedLifeAreasEnglish}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = if (isSinhala) "කාල පරාසය: ${dosha.criticalTimeRangeSinhala}" else "Timeframe: ${dosha.criticalTimeRangeEnglish}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isSinhala) dosha.descriptionSinhala else dosha.descriptionEnglish,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isSinhala) "ශාන්තිකර්ම සහ සෙත් පැතීම:" else "Prescribed Remedies:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                                Text(
                                    text = if (isSinhala) dosha.remediesSinhala else dosha.remediesEnglish,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Longevity, Death & Maraka Analysis Card (ආයුෂ, මරණය සහ මාරක විග්‍රහය).
 */
@Composable
fun LongevityMarakaCard(
    longevity: LongevityMarakaInfo,
    isSinhala: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("longevity_maraka_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isSinhala) "ආයුෂ, මරණය සහ මාරක විග්‍රහය" else "Longevity, Maraka & Life Preservation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "8 වන ආයුෂ භාවය, ශනිගේ බලය සහ මාරක ස්ථාන ගණනය කිරීම්" else "8th house, Saturn vitality factor, and Maraka transit periods",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Ayurdaya Category Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isSinhala) "ආයුර්දාය වර්ගීකරණය" else "Classical Longevity Category",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isSinhala) longevity.ayurdayaCategorySinhala else longevity.ayurdayaCategoryEnglish,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 8th House & Saturn
            Text(
                text = if (isSinhala) longevity.eighthHouseLordStatusSinhala else longevity.eighthHouseLordStatusEnglish,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )

            Text(
                text = if (isSinhala) longevity.saturnLongevityFactorSinhala else longevity.saturnLongevityFactorEnglish,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Maraka analysis
            Text(
                text = if (isSinhala) longevity.marakaHousesAnalysisSinhala else longevity.marakaHousesAnalysisEnglish,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )

            // Caution age brackets
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isSinhala) "මාරක අපල ඉස්මතු විය හැකි ප්‍රවේශම් විය යුතු කාල සීමාවන්:" else "Sensitive Longevity Caution Windows:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                val list = if (isSinhala) longevity.cautionLifeBracketsSinhala else longevity.cautionLifeBracketsEnglish
                list.forEach { bracket ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = bracket,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Life extending remedies
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSinhala) "ආයුෂ වර්ධක පුණ්‍යකර්ම සහ ශාන්තිකර්ම:" else "Life-Preserving Spiritual Practices:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSinhala) longevity.lifeExtendingRemediesSinhala else longevity.lifeExtendingRemediesEnglish,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Past Life Karma Card (පෙර භවය පිළිබඳ කරුණු).
 */
@Composable
fun PastLifeKarmaCard(
    pastLife: PastLifeKarmaInfo,
    isSinhala: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("past_life_karma_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Psychology,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isSinhala) "පෙර භවය පිළිබඳ කරුණු (පූර්ව ජන්ම කර්ම)" else "Past Life Karma & Reincarnation Memory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "9 වන භාග්‍යස්ථානය, 5 වන පුණ්‍යස්ථානය සහ කේතු-රාහු කර්ම අක්ෂය" else "9th & 5th houses, Ketu liberation & Rahu soul desires",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Past Life Identity Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GoldAccent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isSinhala) "පෙර භවයේ භූමිකාව සහ සමාජ තත්ත්වය:" else "Past Incarnation Identity:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSinhala) pastLife.pastLifeIdentitySinhala else pastLife.pastLifeIdentityEnglish,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp
                    )
                }
            }

            // Accumulated Good Merit
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isSinhala) "✨ පෙර කළ පින් (පූර්ව පුණ්‍ය ආශිර්වාද):" else "✨ Accumulated Past Merits:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSinhala) pastLife.accumulatedGoodMeritSinhala else pastLife.accumulatedGoodMeritEnglish,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }

            // Karmic Debt
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isSinhala) "⚖️ මෙලොවදී ගෙවීමට ඇති කර්ම ණය:" else "⚖️ Karmic Debts to Balance:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSinhala) pastLife.pastLifeKarmaDebtSinhala else pastLife.pastLifeKarmaDebtEnglish,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }

            // Ketu & Rahu Soul Axis
            Text(
                text = if (isSinhala) pastLife.ketuSpiritualLessonSinhala else pastLife.ketuSpiritualLessonEnglish,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )

            Text(
                text = if (isSinhala) pastLife.rahuSoulMissionSinhala else pastLife.rahuSoulMissionEnglish,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )

            // Soul Evolution Advice
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isSinhala) "ආත්මීය පාරිශුද්ධියට සහ විමුක්තියට මඟ:" else "Guidance for Soul Evolution:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSinhala) pastLife.soulEvolutionAdviceSinhala else pastLife.soulEvolutionAdviceEnglish,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Prosperity Guidance Card (දියුණු වීමට කළ යුතු දේ සහ ශාන්තිකර්ම).
 */
@Composable
fun ProsperityGuidanceCard(
    prosperity: ProsperityGuidanceInfo,
    isSinhala: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prosperity_guidance_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isSinhala) "දියුණු වීමට කළ යුතු දේ සහ ශාන්තිකර්ම" else "Guidelines & Remedies for Prosperity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "වාසනාවන්ත මැණික්, වර්ණ, අංක, දිශා සහ දෛනික වත්පිළිවෙත්" else "Lucky gems, colors, numbers, directions, and success rituals",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Lucky Gemstone Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Diamond,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isSinhala) "වාසනාවන්ත මැණික (ජීව මැණික):" else "Auspicious Life Gemstone:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isSinhala) prosperity.luckyGemstoneSinhala else prosperity.luckyGemstoneEnglish,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldAccent
                        )
                        Text(
                            text = if (isSinhala) prosperity.gemstoneInstructionsSinhala else prosperity.gemstoneInstructionsEnglish,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Colors & Numbers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isSinhala) "ශුභ වර්ණ:" else "Lucky Colors:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val cols = if (isSinhala) prosperity.luckyColorsSinhala else prosperity.luckyColorsEnglish
                        Text(
                            text = cols.joinToString(", "),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSinhala) "අශුභ වර්ණ:" else "Unlucky Colors:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        val uncols = if (isSinhala) prosperity.unluckyColorsSinhala else prosperity.unluckyColorsEnglish
                        Text(
                            text = uncols.joinToString(", "),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isSinhala) "වාසනාවන්ත අංක:" else "Lucky Numbers:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = prosperity.luckyNumbers.joinToString(", "),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSinhala) "ජයග්‍රාහී දිශාව:" else "Auspicious Direction:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isSinhala) prosperity.auspiciousDirectionsSinhala else prosperity.auspiciousDirectionsEnglish,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Personal Deity Worship
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isSinhala) "ඉෂ්ට දේවතා වන්දනාව:" else "Personal Deity Devotion:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = if (isSinhala) prosperity.personalDeityWorshipSinhala else prosperity.personalDeityWorshipEnglish,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Daily Success Rituals
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isSinhala) "දියුණුව සඳහා දෛනික පුරුදු සහ ශාන්තිකර්ම:" else "Daily Success Practices & Rituals:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                val rituals = if (isSinhala) prosperity.dailySuccessRitualsSinhala else prosperity.dailySuccessRitualsEnglish
                rituals.forEachIndexed { i, ritual ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "${i + 1}. ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = ritual,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
