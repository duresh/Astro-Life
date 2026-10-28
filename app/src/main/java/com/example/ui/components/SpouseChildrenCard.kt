package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChildrenPrediction
import com.example.data.model.MarriagePrediction
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RubyRed
import com.example.ui.theme.StarBlue

@Composable
fun SpouseChildrenCard(
    marriage: MarriagePrediction,
    children: ChildrenPrediction,
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spouse_children_predictions_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- SECTION 1: SPOUSE & MARRIAGE PREDICTIONS ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("spouse_prediction_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RubyRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSinhala) "සහකරු / සහකාරිය සහ විවාහය" else "Spouse & Marriage Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 7th House & Kuja Dosha overview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OverviewChip(
                        label = if (isSinhala) "7 වැන්න" else "7th House",
                        value = if (isSinhala) marriage.seventhHouseSign.sinhalaName else marriage.seventhHouseSign.englishName,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewChip(
                        label = if (isSinhala) "7 අධිපති" else "7th Lord",
                        value = if (isSinhala) marriage.seventhLord.sinhalaName else marriage.seventhLord.englishName,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewChip(
                        label = if (isSinhala) "කුජ දෝෂය" else "Kuja Dosha",
                        value = if (!marriage.hasKujaDosha) {
                            if (isSinhala) "නැත" else "None"
                        } else if (marriage.isKujaDoshaCancelled) {
                            if (isSinhala) "භංගයි" else "Cancelled"
                        } else {
                            if (isSinhala) "ඇත" else "Active"
                        },
                        modifier = Modifier.weight(1f),
                        isHighlight = marriage.hasKujaDosha && !marriage.isKujaDoshaCancelled
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DetailItem(
                    title = if (isSinhala) "සහකරුගේ ගතිගුණ හා පෞරුෂය:" else "Spouse Nature & Personality:",
                    content = if (isSinhala) marriage.spousePersonalitySinhala else marriage.spousePersonalityEnglish
                )

                DetailItem(
                    title = if (isSinhala) "පෙනුම හා රූපසෝභාව:" else "Physical Features & Appearance:",
                    content = if (isSinhala) marriage.spouseAppearanceSinhala else marriage.spouseAppearanceEnglish
                )

                DetailItem(
                    title = if (isSinhala) "සුදුසු රැකියා / වෘත්තීය ක්ෂේත්‍රය:" else "Probable Career / Profession:",
                    content = if (isSinhala) marriage.spouseProfessionSinhala else marriage.spouseProfessionEnglish
                )

                DetailItem(
                    title = if (isSinhala) "විවාහ දිවියේ සමගිය:" else "Marital Harmony & Bliss:",
                    content = if (isSinhala) marriage.maritalHarmonySinhala else marriage.maritalHarmonyEnglish
                )

                if (marriage.hasKujaDosha) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (marriage.isKujaDoshaCancelled) StarBlue else RubyRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSinhala) marriage.kujaDoshaRemedySinhala else marriage.kujaDoshaRemedyEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Favorable Marriage Dates & Age Timelines Highlight Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSinhala) "📅 විවාහ වයස් හා සුබ දින වකවානු:" else "📅 Marriage Age Windows & Dates:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSinhala) marriage.marriageAgeWindowsSinhala else marriage.marriageAgeWindowsEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSinhala) marriage.marriageFavorableDatesSinhala else marriage.marriageFavorableDatesEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // --- SECTION 2: CHILDREN PREDICTIONS ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("children_prediction_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ChildCare,
                        contentDescription = null,
                        tint = StarBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSinhala) "දරුවන් පිළිබඳ විස්තර සහ දින වකවානු" else "Children & Progeny Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OverviewChip(
                        label = if (isSinhala) "5 වැන්න" else "5th House",
                        value = if (isSinhala) children.fifthHouseSign.sinhalaName else children.fifthHouseSign.englishName,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewChip(
                        label = if (isSinhala) "5 අධිපති" else "5th Lord",
                        value = if (isSinhala) children.fifthLord.sinhalaName else children.fifthLord.englishName,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewChip(
                        label = if (isSinhala) "ගුරු බලය" else "Jupiter Dignity",
                        value = if (isSinhala) children.jupiterDignity.sinhalaName else children.jupiterDignity.englishName,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DetailItem(
                    title = if (isSinhala) "දරු ඵල සලකුණු:" else "Children Indication:",
                    content = if (isSinhala) children.childrenCountIndicationSinhala else children.childrenCountIndicationEnglish
                )

                DetailItem(
                    title = if (isSinhala) "දරුවන්ගේ ස්වභාවය හා බුද්ධිය:" else "Nature & Intellect of Progeny:",
                    content = if (isSinhala) children.childrenNatureSinhala else children.childrenNatureEnglish
                )

                DetailItem(
                    title = if (isSinhala) "ජ්‍යෝතිෂ උපදෙස්:" else "Astrological Guidance:",
                    content = if (isSinhala) children.astrologicalGuidanceSinhala else children.astrologicalGuidanceEnglish
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Favorable Childbearing Timelines
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = StarBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSinhala) "👶 දරු සම්පත් ලැබීමේ සුබ වකවානු:" else "👶 Favorable Childbearing Timelines:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSinhala) children.favorablePeriodSinhala else children.favorablePeriodEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 4.dp),
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
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DetailItem(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
