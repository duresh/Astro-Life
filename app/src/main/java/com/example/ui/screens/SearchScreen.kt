package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StarBlue

data class AstroGlossaryItem(
    val id: String,
    val titleSinhala: String,
    val titleEnglish: String,
    val categorySinhala: String,
    val categoryEnglish: String,
    val categoryType: String, // YOGA, PLANET, BHAVA, CONCEPT
    val summarySinhala: String,
    val summaryEnglish: String,
    val detailSinhala: String,
    val detailEnglish: String
)

@Composable
fun SearchScreen(
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    val glossaryItems = remember {
        listOf(
            AstroGlossaryItem(
                id = "yoga_gajakesari",
                titleSinhala = "ගජකේසරි යෝගය (Gajakesari)",
                titleEnglish = "Gajakesari Yoga",
                categorySinhala = "රාජ යෝග",
                categoryEnglish = "Raja Yoga",
                categoryType = "YOGA",
                summarySinhala = "සඳුගෙන් කේන්ද්‍රස්ථානයක (1, 4, 7, 10) ගුරු පිහිටීමෙන් සෑදෙන උතුම් යෝගයකි.",
                summaryEnglish = "Formed when Jupiter is in Kendra (1, 4, 7, 10) from the Moon.",
                detailSinhala = "සමාජයේ නොසැලෙන ගෞරවය, කීර්තිය, සතුරන් මැඩලීමේ බලය සහ අතිමහත් ධන සම්පත් හිමිවේ. ගුරු සහ සඳු දශාවලදී ක්‍රියාත්මක වේ.",
                detailEnglish = "Grants lion-like authority, unshakeable public honor, overcoming rivals and enduring wealth during Jupiter/Moon cycles."
            ),
            AstroGlossaryItem(
                id = "concept_kuja_dosha",
                titleSinhala = "කුජ දෝෂය (භෞම දෝෂය / Manglik)",
                titleEnglish = "Kuja Dosha (Manglik)",
                categorySinhala = "විවාහ දෝෂ",
                categoryEnglish = "Marital Combinations",
                categoryType = "CONCEPT",
                summarySinhala = "ලග්නයෙන් 1, 2, 4, 7, 8, 12 යන ස්ථානවල කුජ පිහිටීම.",
                summaryEnglish = "Mars positioned in houses 1, 2, 4, 7, 8, or 12 from Lagna.",
                detailSinhala = "විවාහයේදී මතභේද හෝ ප්‍රමාදයන් ඇතිවිය හැක. එහෙත් කුජ උච්ච/ස්වක්ෂේත්‍ර වීමෙන් හෝ සමාන කුජ බලය ඇති අයෙකු විවාහ කරගැනීමෙන් දෝෂය සම්පූර්ණයෙන් භංග වේ.",
                detailEnglish = "May introduce marital friction or delays, yet cancelled if Mars is exalted/own sign, or matched with a partner with similar Mars placement."
            ),
            AstroGlossaryItem(
                id = "yoga_ruchaka",
                titleSinhala = "රුචක යෝගය (Ruchaka)",
                titleEnglish = "Ruchaka Yoga",
                categorySinhala = "පංච මහා පුරුෂ",
                categoryEnglish = "Pancha Mahapurusha",
                categoryType = "YOGA",
                summarySinhala = "කුජ කේන්ද්‍රස්ථානයක උච්චව (මකර) හෝ ස්වක්ෂේත්‍රව (මේෂ, වෘශ්චික) පිහිටීම.",
                summaryEnglish = "Mars in Kendra in Capricorn, Aries, or Scorpio.",
                detailSinhala = "අසීමිත නිර්භීතකම, හමුදා/ආරක්ෂක හෝ ව්‍යවසායකත්ව ජය, ඉඩකඩම් සහ නිවාස සම්පත.",
                detailEnglish = "Unyielding bravery, strategic commander skills, massive land/real-estate assets."
            ),
            AstroGlossaryItem(
                id = "yoga_budhaditya",
                titleSinhala = "බුධාදිත්‍ය යෝගය (Budhaditya)",
                titleEnglish = "Budhaditya Yoga",
                categorySinhala = "ඥාන යෝග",
                categoryEnglish = "Intellectual Yoga",
                categoryType = "YOGA",
                summarySinhala = "රවි සහ බුධ එකම රාශියේ සංයෝග වීමෙන් ඇතිවේ.",
                summaryEnglish = "Conjunction of Sun and Mercury in the same house.",
                detailSinhala = "විශිෂ්ට බුද්ධිය, තර්ක ඥානය, විශ්ලේෂණ කුසලතාව, පරිපාලන සහ විභාග ජයග්‍රහණ.",
                detailEnglish = "Sharp intellect, academic excellence, administrative prowess, persuasive debate."
            ),
            AstroGlossaryItem(
                id = "concept_rahu_kalaya",
                titleSinhala = "රාහු කාලය (Rahu Kalaya)",
                titleEnglish = "Rahu Kalaya",
                categorySinhala = "මුහුර්ත විද්‍යාව",
                categoryEnglish = "Muhurtha Timing",
                categoryType = "CONCEPT",
                summarySinhala = "සෑම දිනකම දිවා කාලය සමාන කොටස් 8 කට බෙදා ගණනය කරන අසුභ හෝරාව.",
                summaryEnglish = "Inauspicious 90-minute daily planetary period governed by shadow node Rahu.",
                detailSinhala = "රාහු කාලය තුළ නව කටයුතු ඇරඹීම, ගිවිසුම් අත්සන් කිරීම හෝ මංගල චාරිත්‍ර කිරීමෙන් වැළකී සිටීම සුබදායකය.",
                detailEnglish = "Commencing major journeys, contracts, or ceremonial beginnings during Rahu Kalaya is advised against."
            ),
            AstroGlossaryItem(
                id = "bhava_seventh",
                titleSinhala = "7 වන භාවය (කලත්‍ර ස්ථානය)",
                titleEnglish = "7th House (Kalatra Bhava)",
                categorySinhala = "භාව 12",
                categoryEnglish = "12 Houses",
                categoryType = "BHAVA",
                summarySinhala = "සහකරු හෝ සහකාරිය, විවාහ දිවිය, ව්‍යාපාරික හවුල්කාරිත්වය.",
                summaryEnglish = "Governs spouse, marriage, business partnerships and public rapport.",
                detailSinhala = "7 වැන්න, 7 අධිපති සහ සිකුරු ග්‍රහයාගේ පිහිටීම අනුව සහකරුගේ ස්වභාවය, විවාහ වන වයස සහ සමගිය තීරණය වේ.",
                detailEnglish = "7th house, its ruling planet, and Venus reveal partner's traits, marriage age windows and domestic harmony."
            ),
            AstroGlossaryItem(
                id = "bhava_fifth",
                titleSinhala = "5 වන භාවය (පුත්‍ර ස්ථානය)",
                titleEnglish = "5th House (Putra Bhava)",
                categorySinhala = "භාව 12",
                categoryEnglish = "12 Houses",
                categoryType = "BHAVA",
                summarySinhala = "දරුවන්, පූර්ව පින්, බුද්ධිය, නිර්මාණශීලී කුසලතා.",
                summaryEnglish = "Governs progeny, past life merits, intelligence, creative arts.",
                detailSinhala = "5 වැන්න සහ ගුරු (පුත්‍රකාරක) ග්‍රහයා තුළින් දරු සම්පත් ලැබීමේ කාල වකවානු සහ දරුවන්ගේ දක්ෂතා හෙළිවේ.",
                detailEnglish = "5th house and Jupiter (Putrakaraka) determine childbearing timelines and intellectual talents of offspring."
            ),
            AstroGlossaryItem(
                id = "concept_navamsha",
                titleSinhala = "නවාංශක කේන්ද්‍රය (D-9 Chart)",
                titleEnglish = "Navamsha Chakra (D-9)",
                categorySinhala = "වර්ග කේන්ද්‍ර",
                categoryEnglish = "Divisional Charts",
                categoryType = "CONCEPT",
                summarySinhala = "රාශියක් කොටස් 9 කට බෙදා සකසන වඩාත්ම ප්‍රබල විවාහ සහ ඉරණම් කේන්ද්‍රය.",
                summaryEnglish = "1/9th harmonic divisional chart crucial for marriage and inner soul destiny.",
                detailSinhala = "රාශි කේන්ද්‍රය ශරීරය නම් නවාංශකය එහි ආත්මයයි. විවාහය සහ ජීවිතයේ දෙවන භාගය නවාංශකයෙන් පරීක්ෂා කෙරේ.",
                detailEnglish = "If Rashi chart is the tree, Navamsha is the fruit. Evaluates marriage bliss and post-marriage destiny."
            ),
            AstroGlossaryItem(
                id = "concept_dasha",
                titleSinhala = "විංශෝත්තරී මහා දශා පද්ධතිය (Vimshottari)",
                titleEnglish = "Vimshottari Dasha System",
                categorySinhala = "දශා පද්ධති",
                categoryEnglish = "Dasha Systems",
                categoryType = "CONCEPT",
                summarySinhala = "මිනිස් ආයුෂ වසර 120 ක් ලෙස ගණන් බලන ග්‍රහ දශා චක්‍රය.",
                summaryEnglish = "120-year planetary life-cycle mapped from Moon's nakshatra at birth.",
                detailSinhala = "කේතු(7), සිකුරු(20), රවි(6), සඳු(10), කුජ(7), රාහු(18), ගුරු(16), ශනි(19), බුධ(17) ලෙස පිළිවෙලින් ක්‍රියාත්මක වේ.",
                detailEnglish = "Cycles through Ketu, Venus, Sun, Moon, Mars, Rahu, Jupiter, Saturn, and Mercury."
            ),
            AstroGlossaryItem(
                id = "concept_bhukti",
                titleSinhala = "අන්තර් දශා / භුක්ති (Bhukti / Antardasha)",
                titleEnglish = "Bhukti (Antardasha Sub-Periods)",
                categorySinhala = "දශා පද්ධති",
                categoryEnglish = "Dasha Systems",
                categoryType = "CONCEPT",
                summarySinhala = "මහා දශාවක් තුළ ක්‍රියාත්මක වන අතුරු ග්‍රහ කාලසීමාවන් 9.",
                summaryEnglish = "Nine proportional sub-periods operating within each major Mahadasha cycle.",
                detailSinhala = "මහා දශාධිපතියාගේ සිට අනුපිළිවෙලින් ග්‍රහයින් 9 දෙනාගේ අන්තර් දශා ක්‍රියාත්මක වේ. දින වකවානු සමඟ ක්ෂණික ඵල ලබා දෙයි.",
                detailEnglish = "Each sub-period is calculated as (Major Years x Minor Years) / 10 months. Triggers concrete events."
            ),
            AstroGlossaryItem(
                id = "concept_erashtaka",
                titleSinhala = "ශනි ඒරාෂ්ටකය (ජන්ම, ලග්න, අෂ්ටම, කණ්ඨක)",
                titleEnglish = "Saturn Erashtaka (Sade Sati & Transits)",
                categorySinhala = "අපල කාල",
                categoryEnglish = "Challenging Periods",
                categoryType = "CONCEPT",
                summarySinhala = "සෙනසුරු ග්‍රහයා සඳු සහ ලග්නය මතින් ගමන් ගන්නා පරීක්ෂණ කාලසීමාව.",
                summaryEnglish = "Major Saturn transit cycles over natal Moon and Lagna representing karmic refinement.",
                detailSinhala = "ජන්ම ඒරාෂ්ටකය (වසර 7 1/2), අෂ්ටම ඒරාෂ්ටකය (වසර 2 1/2), කණ්ඨක ඒරාෂ්ටකය. බෝධි පූජා සහ තල දන් දීමෙන් අපල සමනය වේ.",
                detailEnglish = "Janma (Sade Sati, 7.5 yrs), Ashtama (8th house, 2.5 yrs), Kantaka (4th/10th). Mitigated via Bodhi pooja and charity."
            ),
            AstroGlossaryItem(
                id = "concept_longevity",
                titleSinhala = "ආයුර්දාය සහ මාරක ස්ථාන (Longevity & Maraka)",
                titleEnglish = "Ayurdaya & Maraka Houses",
                categorySinhala = "ආයුෂ විද්‍යාව",
                categoryEnglish = "Longevity Studies",
                categoryType = "CONCEPT",
                summarySinhala = "8 වැන්න, ශනි ග්‍රහයා සහ 2, 7 මාරක ස්ථාන ඇසුරෙන් කෙරෙන ආයුෂ විග්‍රහය.",
                summaryEnglish = "Evaluation of life expectancy via 8th house, Saturn, and 2nd/7th Maraka houses.",
                detailSinhala = "දීර්ඝ ආයුෂ (75+), මධ්‍ය ආයුෂ (60-75). මාරක දශාවලදී මහා මෘත්‍යුඤ්ජය මන්ත්‍රය සහ අභයදාන පින්කම් ආයුෂ වර්ධනය කරයි.",
                detailEnglish = "Classified into Deergha (75+) or Madhya (60-75). Maha Mrityunjaya mantra and animal releases strengthen life force."
            ),
            AstroGlossaryItem(
                id = "concept_past_life",
                titleSinhala = "පෙර භවය සහ පූර්ව පුණ්‍යය (Past Life Karma)",
                titleEnglish = "Purva Janma Karma (Past Life)",
                categorySinhala = "කර්ම විද්‍යාව",
                categoryEnglish = "Karmic Destiny",
                categoryType = "CONCEPT",
                summarySinhala = "9 වන භාග්‍යස්ථානය, 5 වැන්න සහ කේතු-රාහු අක්ෂයෙන් හෙළිවන පෙර භව තොරතුරු.",
                summaryEnglish = "Reincarnation memory deciphered from 9th/5th houses and Ketu-Rahu nodal axis.",
                detailSinhala = "පෙර භවයේ සමාජ තත්ත්වය, රැගෙන ආ පින් සහ ගෙවීමට ඇති කර්ම ණය මනාව හෙළිදරව් කෙරේ.",
                detailEnglish = "Reveals past life identity, stored merit (Purva Punya), and karmic lessons to resolve in this birth."
            ),
            AstroGlossaryItem(
                id = "concept_prosperity",
                titleSinhala = "දියුණුවට වාසනාවන්ත මැණික් සහ ශාන්තිකර්ම (Prosperity)",
                titleEnglish = "Auspicious Gemstones & Prosperity Remedies",
                categorySinhala = "ශාන්තිකර්ම",
                categoryEnglish = "Remedies",
                categoryType = "CONCEPT",
                summarySinhala = "ලග්නාධිපති හා භාග්‍යාධිපති මත පදනම් වූ ජීව මැණික, වර්ණ, අංක සහ දෛනික චර්යාවන්.",
                summaryEnglish = "Tailored gemstones, lucky colors, auspicious directions, and daily rituals for success.",
                detailSinhala = "පැළඳිය යුතු ලෝහය, ඇඟිල්ල, ඉෂ්ට දේවතා වන්දනාව සහ දෛනික පින්කම් තුළින් බාධක ජයගත හැක.",
                detailEnglish = "Correct metal, finger mounting, personal deity devotion, and positive daily habits ensure steady progress."
            )
        )
    }

    val categories = listOf(
        Pair("ALL", if (isSinhala) "සියල්ල" else "All"),
        Pair("YOGA", if (isSinhala) "යෝග" else "Yogas"),
        Pair("CONCEPT", if (isSinhala) "සංකල්ප" else "Concepts"),
        Pair("BHAVA", if (isSinhala) "භාවයන්" else "Houses")
    )

    val filteredItems = remember(query, selectedCategory, glossaryItems) {
        glossaryItems.filter { item ->
            val matchesCat = selectedCategory == "ALL" || item.categoryType == selectedCategory
            val matchesQuery = query.isBlank() ||
                    item.titleSinhala.contains(query, ignoreCase = true) ||
                    item.titleEnglish.contains(query, ignoreCase = true) ||
                    item.summarySinhala.contains(query, ignoreCase = true) ||
                    item.summaryEnglish.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("search_screen")
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(if (isSinhala) "යෝග, ග්‍රහයින්, භාවයන් හෝ නැකත් සොයන්න..." else "Search yogas, planets, houses, nakshatras...")
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("glossary_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
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
                    modifier = Modifier.testTag("search_cat_$code")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isSinhala) "ජ්‍යෝතිෂ ශබ්දකෝෂය සහ මූලධර්ම (${filteredItems.size})" else "Astrological Glossary & Principles (${filteredItems.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredItems, key = { it.id }) { item ->
                val isExpanded = expandedItemId == item.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedItemId = if (isExpanded) null else item.id }
                        .testTag("glossary_item_${item.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                                text = if (isSinhala) item.titleSinhala else item.titleEnglish,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isSinhala) item.categorySinhala else item.categoryEnglish,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isSinhala) item.summarySinhala else item.summaryEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (isSinhala) "විස්තරාත්මක පැහැදිලි කිරීම:" else "In-depth Astrological Explanation:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isSinhala) item.detailSinhala else item.detailEnglish,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = if (isExpanded) (if (isSinhala) "අඩු කරන්න ▲" else "Less ▲") else (if (isSinhala) "තව විස්තර ▼" else "More ▼"),
                                fontSize = 11.sp,
                                color = StarBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
