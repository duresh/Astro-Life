package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.data.model.*
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StarBlue

@Composable
fun LifeDestinyView(
    lifeDestiny: LifeDestinyAnalysis?,
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    if (lifeDestiny == null) return

    var selectedDomain by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("life_destiny_module"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Domain Selection Pills
        ScrollableTabRow(
            selectedTabIndex = selectedDomain,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {},
            modifier = Modifier.fillMaxWidth().testTag("destiny_tabs_row")
        ) {
            val tabs = listOf(
                Pair(if (isSinhala) "විදේශ ගමන්" else "Foreign Travel", Icons.Default.FlightTakeoff),
                Pair(if (isSinhala) "ධන ලාභ" else "Wealth Influx", Icons.Default.MonetizationOn),
                Pair(if (isSinhala) "වාහන & නිවාස" else "Vehicles & Property", Icons.Default.HomeWork),
                Pair(if (isSinhala) "ජයග්‍රහණ & පරාජයන්" else "Triumphs & Setbacks", Icons.Default.EmojiEvents),
                Pair(if (isSinhala) "පවුලෙන් ලැබීම් & දරුවන්" else "Family Gains & Kids", Icons.Default.Diversity3),
                Pair(if (isSinhala) "සෞඛ්‍ය ජීවිතය" else "Health & Vitality", Icons.Default.MonitorHeart),
                Pair(if (isSinhala) "කළ යුතු පින්කම්" else "Merits & Dana", Icons.Default.VolunteerActivism)
            )

            tabs.forEachIndexed { index, (title, icon) ->
                val isSelected = selectedDomain == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedDomain = index },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        )
                        .testTag("destiny_tab_$index"),
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

        // Domain View Content
        when (selectedDomain) {
            0 -> ForeignTravelCard(travel = lifeDestiny.foreignTravel, isSinhala = isSinhala)
            1 -> WealthGainsCard(wealth = lifeDestiny.wealth, isSinhala = isSinhala)
            2 -> PropertyVehiclesCard(property = lifeDestiny.propertyAndVehicle, isSinhala = isSinhala)
            3 -> TriumphsAndSetbacksCard(milestones = lifeDestiny.triumphsAndSetbacks, isSinhala = isSinhala)
            4 -> FamilyGainsCard(family = lifeDestiny.familyGains, isSinhala = isSinhala)
            5 -> HealthVitalityCard(health = lifeDestiny.health, isSinhala = isSinhala)
            6 -> MeritoriousDeedsCard(merits = lifeDestiny.meritoriousDeeds, isSinhala = isSinhala)
        }
    }
}

/**
 * 1. Foreign Travel & Settlement Card (විදේශ ගමන් සහ විදේශ වාසය)
 */
@Composable
fun ForeignTravelCard(travel: ForeignTravelPrediction, isSinhala: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("foreign_travel_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isSinhala) "විදේශ ගමන් සහ විදේශ වාසය පිළිබඳ විග්‍රහය" else "Foreign Travel & International Horizons",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "12, 9, 7 භාවයන් සහ රාහු ග්‍රහ පිහිටීම ඇසුරෙන්" else "12th, 9th, 7th houses and Rahu global transit analysis",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TravelExplore, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isSinhala) "විදේශගතවීමේ විභවය:" else "Global Settlement Prospect:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isSinhala) travel.settlementPotentialSinhala else travel.settlementPotentialEnglish,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(text = if (isSinhala) travel.twelfthHouseStatusSinhala else travel.twelfthHouseStatusEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
            Text(text = if (isSinhala) travel.ninthHouseStatusSinhala else travel.ninthHouseStatusEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
            Text(text = if (isSinhala) travel.rahuImpactSinhala else travel.rahuImpactEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)

            // Favorable Periods
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = if (isSinhala) "විදේශ ගමන් හා වීසා සඳහා සුබ කාල පරාසයන්:" else "Favorable Windows for Visas & Relocation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                val periods = if (isSinhala) travel.favorablePeriodsSinhala else travel.favorablePeriodsEnglish
                periods.forEach { period ->
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                        Text(text = "✈️ $period", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isSinhala) "වාසනාවන්ත රටවල්:" else "Fortuitous Countries:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = if (isSinhala) travel.suitableCountriesSinhala else travel.suitableCountriesEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = if (isSinhala) "විදේශ ගමන් සාර්ථකත්වයට ශාන්තිකර්ම:" else "Travel Success Remedies:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    Text(text = if (isSinhala) travel.remediesSinhala else travel.remediesEnglish, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/**
 * 2. Wealth & Financial Gains Card (ධන ලාභ සහ මූල්‍ය දියුණුව)
 */
@Composable
fun WealthGainsCard(wealth: WealthPrediction, isSinhala: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("wealth_gains_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isSinhala) "ධන ලාභ සහ මූල්‍ය සශ්‍රීකත්වය" else "Wealth Influx & Financial Accumulation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "2 වන ධනස්ථානය, 11 වන අයස්ථානය සහ ධන යෝග විග්‍රහය" else "2nd House of Wealth, 11th House of Gains & Dhana Yogas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(shape = RoundedCornerShape(12.dp), color = GoldAccent.copy(alpha = 0.15f), border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = if (isSinhala) "මූල්‍ය විභව මට්ටම:" else "Wealth Potential Grade:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = if (isSinhala) wealth.wealthPotentialGradeSinhala else wealth.wealthPotentialGradeEnglish, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                }
            }

            // Dhana Yogas
            val yogas = if (isSinhala) wealth.dhanaYogasSinhala else wealth.dhanaYogasEnglish
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = if (isSinhala) "සක්‍රිය ධන යෝග:" else "Active Wealth Yogas:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                yogas.forEach { yoga ->
                    Text(text = "✦ $yoga", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                }
            }

            Text(text = if (isSinhala) wealth.secondHouseWealthStatusSinhala else wealth.secondHouseWealthStatusEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
            Text(text = if (isSinhala) wealth.eleventhHouseGainsStatusSinhala else wealth.eleventhHouseGainsStatusEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)

            // Sudden Wealth & Windfalls
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isSinhala) "අහඹු ධන ලාභ සහ උරුමයන්:" else "Sudden Wealth & Windfalls:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    Text(text = if (isSinhala) wealth.suddenWealthProspectsSinhala else wealth.suddenWealthProspectsEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
                }
            }

            // Favorable Wealth Periods
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = if (isSinhala) "ධන සම්පත් උපරිමයෙන් ගලා එන ස්වර්ණමය කාල පරාස:" else "Peak Wealth Accumulation Horizons:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                val wPeriods = if (isSinhala) wealth.favorableWealthPeriodsSinhala else wealth.favorableWealthPeriodsEnglish
                wPeriods.forEach { p ->
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)) {
                        Text(text = "💰 $p", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            Text(text = if (isSinhala) "උපදෙස්: ${wealth.wealthPreservationAdviceSinhala}" else "Guidance: ${wealth.wealthPreservationAdviceEnglish}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * 3. Vehicles, House Construction & Property Card (යාන වාහන, නිවාස ඉදිකිරීම් සහ ඉඩකඩම් ලැබීම)
 */
@Composable
fun PropertyVehiclesCard(property: PropertyVehiclePrediction, isSinhala: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("property_vehicles_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HomeWork, contentDescription = null, tint = StarBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isSinhala) "යාන වාහන, නිවාස ඉදිකිරීම් සහ ඉඩකඩම් ලැබීම්" else "Conveyances, Real Estate & Architecture",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "4 වන භාවය, කුජ (භූමි කාරක) සහ සිකුරු (වාහන කාරක) බලය" else "4th House, Mars (Land) and Venus (Conveyances) dynamics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(text = if (isSinhala) property.fourthHouseStatusSinhala else property.fourthHouseStatusEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
            Text(text = if (isSinhala) property.marsBhoomiKarakaStatusSinhala else property.marsBhoomiKarakaStatusEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
            Text(text = if (isSinhala) property.venusVahanaKarakaStatusSinhala else property.venusVahanaKarakaStatusEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)

            // House construction timing
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = if (isSinhala) "නිවාස ඉදිකිරීමට හා මුල්ගල තැබීමට සුබ කාල පරාසයන්:" else "House Construction & Property Timelines:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                val cList = if (isSinhala) property.houseConstructionTimingSinhala else property.houseConstructionTimingEnglish
                cList.forEach { c ->
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                        Text(text = "🏡 $c", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            // Vehicle acquisition timing
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = if (isSinhala) "යාන වාහන මිලදී ගැනීමට සුබ දින වකවානු:" else "Vehicle Acquisition Horizons:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StarBlue)
                val vList = if (isSinhala) property.vehicleAcquisitionTimingSinhala else property.vehicleAcquisitionTimingEnglish
                vList.forEach { v ->
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                        Text(text = "🚗 $v", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            Text(text = if (isSinhala) property.landPropertyInheritanceSinhala else property.landPropertyInheritanceEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
            Text(text = if (isSinhala) "ප්‍රවේශම් විය යුතු කරුණු: ${property.propertyPrecautionsSinhala}" else "Precautions: ${property.propertyPrecautionsEnglish}", fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}

/**
 * 4. Life Triumphs & Setbacks Card (ජීවිතයේ ජයග්‍රහණ සහ පරාජයන් කාල පරාස සමඟ)
 */
@Composable
fun TriumphsAndSetbacksCard(milestones: LifeTriumphsAndSetbacks, isSinhala: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("triumphs_setbacks_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isSinhala) "ජීවිතයේ ජයග්‍රහණ සහ පරාජයන් කාල පරාස" else "Life Triumphs, Peaks & Vulnerable Phases",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "ස්වර්ණමය යුග සහ අභියෝගාත්මක සන්ධිස්ථාන" else "Golden eras vs crucible testing periods across life",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(shape = RoundedCornerShape(10.dp), color = GoldAccent.copy(alpha = 0.15f)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isSinhala) "දෛවයේ ප්‍රධාන හැරවුම් ලක්ෂ්‍ය (Turning Points):" else "Destiny Turning Point Ages:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    Text(text = if (isSinhala) milestones.turningPointAgesSinhala else milestones.turningPointAgesEnglish, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Triumphs Section
            Text(text = if (isSinhala) "🌟 ස්වර්ණමය ජයග්‍රහණ යුග (Peak Triumphs):" else "🌟 Peak Golden Milestones:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            milestones.majorTriumphs.forEach { t ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isSinhala) t.titleSinhala else t.titleEnglish, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = t.ageWindowStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = "📅 ${t.periodRangeStr}", fontSize = 10.sp, color = GoldAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = if (isSinhala) t.descriptionSinhala else t.descriptionEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = if (isSinhala) "උපදෙස: ${t.strategicGuidanceSinhala}" else "Strategy: ${t.strategicGuidanceEnglish}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Setbacks Section
            Text(text = if (isSinhala) "⚠️ ප්‍රවේශම් විය යුතු අභියෝගාත්මක කාල (Challenging Phases):" else "⚠️ Cautionary & Setback Horizons:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            milestones.majorSetbacks.forEach { s ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isSinhala) s.titleSinhala else s.titleEnglish, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text(text = s.ageWindowStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = "📅 ${s.periodRangeStr}", fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = if (isSinhala) s.descriptionSinhala else s.descriptionEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = if (isSinhala) "ජයගත යුතු මඟ: ${s.strategicGuidanceSinhala}" else "Mitigation: ${s.strategicGuidanceEnglish}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * 5. Gains from Spouse & Children, Children Future (කලත්‍රයා හා දරුවන්ගෙන් ලැබීම්, දරුවන්ගේ අනාගතය)
 */
@Composable
fun FamilyGainsCard(family: FamilyGainsPrediction, isSinhala: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("family_gains_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Diversity3, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isSinhala) "කලත්‍රයා සහ දරුවන්ගෙන් සිදුවන ලැබීම්" else "Gains From Spouse, Progeny & Kids' Future",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "7, 8 සහ 5 වන භාවයන්ගෙන් විවර වන පවුල් වාසනාව" else "7th, 8th and 5th house partner wealth and children destiny",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Gains through spouse
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isSinhala) "කලත්‍රයාගෙන් (සහකරු/සහකාරියගෙන්) ලැබීම්:" else "Gains & Fortune Through Spouse:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = if (isSinhala) family.gainsFromSpouseSinhala else family.gainsFromSpouseEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                }
            }

            // Gains through children
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ChildCare, contentDescription = null, tint = StarBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isSinhala) "දරුවන්ගෙන් දෙමාපියන්ට සිදුවන ලැබීම් සහ රැකවරණය:" else "Filial Devotion & Support From Children:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StarBlue)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = if (isSinhala) family.gainsFromChildrenSinhala else family.gainsFromChildrenEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                }
            }

            // Children Future Prospects
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = if (isSinhala) "දරුවන්ගේ අනාගතය, අධ්‍යාපනය සහ වෘත්තීය සාර්ථකත්වය:" else "Children's Academic & Career Horizons:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = if (isSinhala) family.childrenFutureProspectsSinhala else family.childrenFutureProspectsEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                }
            }

            // Family Prosperity Timeline
            val fTimeline = if (isSinhala) family.familyProsperityTimelineSinhala else family.familyProsperityTimelineEnglish
            fTimeline.forEach { line ->
                Text(text = "✦ $line", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
            }
        }
    }
}

/**
 * 6. Health & Wellness Forecast (සෞඛ්‍ය ජීවිතය සහ රෝග නිවාරණය)
 */
@Composable
fun HealthVitalityCard(health: HealthPrediction, isSinhala: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("health_vitality_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isSinhala) "සෞඛ්‍ය ජීවිතය සහ රෝග නිවාරණය" else "Health Constitution & Preventive Vitality",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "6 වන රෝගස්ථානය, ලග්න ශක්තිය සහ ආයුර්වේද සෞඛ්‍ය පිළිවෙත්" else "6th house health analysis, immunity, and Ayurvedic wellness",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(text = if (isSinhala) health.overallVitalitySinhala else health.overallVitalityEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)

            // Vulnerable body systems
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isSinhala) "විශේෂයෙන්ම සැලකිලිමත් විය යුතු ශරීර අංග / පද්ධති:" else "Vulnerable Bodily Systems to Monitor:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    val sys = if (isSinhala) health.vulnerableBodySystemsSinhala else health.vulnerableBodySystemsEnglish
                    sys.forEach { s ->
                        Text(text = "• $s", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
                    }
                }
            }

            // Sensitive age windows
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = if (isSinhala) "වෛද්‍ය පරීක්ෂණ කරගත යුතු සංවේදී වයස් සීමාවන්:" else "Sensitive Health Checkup Horizons:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                val windows = if (isSinhala) health.sensitiveAgeWindowsSinhala else health.sensitiveAgeWindowsEnglish
                windows.forEach { w ->
                    Text(text = "🩺 $w", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
                }
            }

            // Ayurvedic advice
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isSinhala) "ආයුර්වේද සහ ආහාර චර්යා උපදෙස්:" else "Ayurvedic Lifestyle & Dietary Wisdom:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = if (isSinhala) health.ayurvedicLifestyleAdviceSinhala else health.ayurvedicLifestyleAdviceEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
                }
            }
        }
    }
}

/**
 * 7. Meritorious Deeds & Dana Card (සිදු කළ යුතු පින්කම්)
 */
@Composable
fun MeritoriousDeedsCard(merits: MeritoriousDeedsGuidance, isSinhala: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("meritorious_deeds_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isSinhala) "ජීවිත දියුණුවට සිදු කළ යුතු උදාර පින්කම්" else "Auspicious Meritorious Deeds & Dana",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSinhala) "ග්‍රහ අපල දුරුවී භාග්‍යය උදාවන ප්‍රමුඛ දානමය පුණ්‍යකර්ම" else "Karmic merits recommended for your astrological chart",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(text = if (isSinhala) merits.karmicMeritImpactSinhala else merits.karmicMeritImpactEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)

            // Priority Charity List
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = if (isSinhala) "විශේෂයෙන්ම සිදු කළ යුතු පුණ්‍යකර්ම:" else "Priority Dana & Charity Recommendations:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                val list = if (isSinhala) merits.priorityCharityListSinhala else merits.priorityCharityListEnglish
                list.forEachIndexed { i, d ->
                    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                            Text(text = "${i + 1}. ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            Text(text = d, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)
                        }
                    }
                }
            }

            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)) {
                Text(text = if (isSinhala) "🗓️ පින්කම් කිරීමට සුබ දින: ${merits.bestDaysForDanaSinhala}" else "🗓️ Auspicious Timing: ${merits.bestDaysForDanaEnglish}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
