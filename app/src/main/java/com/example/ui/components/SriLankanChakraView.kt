package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.GoldAccent

enum class ChartViewMode {
    LAGNA, NAVAMSHA, DUAL
}

@Composable
fun SriLankanChakraView(
    result: BirthChartResult,
    isSinhala: Boolean,
    modifier: Modifier = Modifier,
    initialMode: ChartViewMode = ChartViewMode.NAVAMSHA
) {
    var viewMode by remember { mutableStateOf(initialMode) }
    var selectedHouseNum by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("birth_chart_chakra_card"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- TOP TAB SELECTOR: ලග්නය | නවාංශ | ලග් + නවා ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeTabItem(
                label = if (isSinhala) "ලග්නය" else "Lagna",
                isSelected = viewMode == ChartViewMode.LAGNA,
                onClick = { viewMode = ChartViewMode.LAGNA },
                testTag = "tab_lagna_mode"
            )
            ModeTabItem(
                label = if (isSinhala) "නවාංශ" else "Navamsha",
                isSelected = viewMode == ChartViewMode.NAVAMSHA,
                onClick = { viewMode = ChartViewMode.NAVAMSHA },
                testTag = "tab_navamsha_mode"
            )
            ModeTabItem(
                label = if (isSinhala) "ලග් + නවා" else "Lagna + Nava",
                isSelected = viewMode == ChartViewMode.DUAL,
                onClick = { viewMode = ChartViewMode.DUAL },
                testTag = "tab_dual_mode"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (viewMode) {
            ChartViewMode.LAGNA -> {
                SingleChakraRenderer(
                    result = result,
                    isNavamsha = false,
                    isSinhala = isSinhala,
                    onHouseClick = { selectedHouseNum = it }
                )
            }
            ChartViewMode.NAVAMSHA -> {
                SingleChakraRenderer(
                    result = result,
                    isNavamsha = true,
                    isSinhala = isSinhala,
                    onHouseClick = { selectedHouseNum = it }
                )
            }
            ChartViewMode.DUAL -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = if (isSinhala) "රාශි (D1) කේන්ද්‍රය" else "Rashi (D1) Chart",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    SingleChakraRenderer(
                        result = result,
                        isNavamsha = false,
                        isSinhala = isSinhala,
                        onHouseClick = { selectedHouseNum = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isSinhala) "නවාංශ (D9) කේන්ද්‍රය" else "Navamsha (D9) Chart",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    SingleChakraRenderer(
                        result = result,
                        isNavamsha = true,
                        isSinhala = isSinhala,
                        onHouseClick = { selectedHouseNum = it }
                    )
                }
            }
        }

        // Details of selected house
        AnimatedVisibility(visible = selectedHouseNum != null) {
            selectedHouseNum?.let { houseNum ->
                val houseInfo = result.houses.find { it.houseNumber == houseNum }
                val isNav = viewMode == ChartViewMode.NAVAMSHA
                val lagnaSign = if (isNav) result.navamshaLagnaSign else result.lagnaSign
                val houseSign = ZodiacSign.fromId((lagnaSign.id + houseNum - 1) % 12)

                val planetsInHouse = result.planetaryPositions.filter { p ->
                    val sign = if (isNav) p.navamshaSign else p.sign
                    sign == houseSign
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSinhala) "${houseNum} වන භාවය (${houseSign.sinhalaName} රාශිය)"
                                else "House $houseNum (${houseSign.englishName})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { selectedHouseNum = null }) {
                                Text(if (isSinhala) "වසන්න" else "Close", fontSize = 12.sp)
                            }
                        }

                        Text(
                            text = if (isSinhala) "අධිපති: ${houseSign.lordPlanetSinhala} | කාරකත්වය: ${houseInfo?.significanceSinhala ?: ""}"
                            else "Ruler: ${houseSign.lordPlanetEnglish} | Significance: ${houseInfo?.significanceEnglish ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (planetsInHouse.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isSinhala) "පිහිටි ග්‍රහයින්:" else "Planets in House:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            planetsInHouse.forEach { p ->
                                Text(
                                    text = "• " + if (isSinhala) "${p.planet.sinhalaName} (${p.dignity.sinhalaName}) ${if (p.isRetrograde) "වක්‍ර" else ""}"
                                    else "${p.planet.englishName} (${p.dignity.englishName}) ${if (p.isRetrograde) "Retro" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
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
private fun ModeTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isSelected) Color(0xFFD46B18) else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "▾",
                fontSize = 11.sp,
                color = if (isSelected) Color(0xFFD46B18) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFD46B18))
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

@Composable
private fun SingleChakraRenderer(
    result: BirthChartResult,
    isNavamsha: Boolean,
    isSinhala: Boolean,
    onHouseClick: (Int) -> Unit
) {
    val lagnaSign = if (isNavamsha) result.navamshaLagnaSign else result.lagnaSign

    // Map each house number (1..12) to the planets inside it
    val housePlanets = remember(isNavamsha, result) {
        val map = mutableMapOf<Int, MutableList<PlanetPosition>>()
        for (h in 1..12) map[h] = mutableListOf()
        result.planetaryPositions.forEach { pos ->
            val sign = if (isNavamsha) pos.navamshaSign else pos.sign
            val h = ((sign.id - lagnaSign.id + 12) % 12) + 1
            map[h]?.add(pos)
        }
        map
    }

    val gridLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // The 3x3 Outer Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFFCFBF9))
                .border(1.5.dp, gridLineColor)
                .testTag("chakra_diagram_box")
        ) {
            // Draw the grid lines and corner diagonals
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val x1 = w / 3f
                val x2 = w * 2f / 3f
                val y1 = h / 3f
                val y2 = h * 2f / 3f

                val strokeW = 1.5.dp.toPx()

                // Vertical lines
                drawLine(gridLineColor, Offset(x1, 0f), Offset(x1, h), strokeWidth = strokeW)
                drawLine(gridLineColor, Offset(x2, 0f), Offset(x2, h), strokeWidth = strokeW)

                // Horizontal lines
                drawLine(gridLineColor, Offset(0f, y1), Offset(w, y1), strokeWidth = strokeW)
                drawLine(gridLineColor, Offset(0f, y2), Offset(w, y2), strokeWidth = strokeW)

                // Diagonals in the 4 corners:
                // Top-Left corner: (0,0) to (x1, y1)
                drawLine(gridLineColor, Offset(0f, 0f), Offset(x1, y1), strokeWidth = strokeW)

                // Top-Right corner: (w, 0) to (x2, y1)
                drawLine(gridLineColor, Offset(w, 0f), Offset(x2, y1), strokeWidth = strokeW)

                // Bottom-Left corner: (0, h) to (x1, y2)
                drawLine(gridLineColor, Offset(0f, h), Offset(x1, y2), strokeWidth = strokeW)

                // Bottom-Right corner: (w, h) to (x2, y2)
                drawLine(gridLineColor, Offset(w, h), Offset(x2, y2), strokeWidth = strokeW)
            }

            // Cell contents on top of canvas
            Column(modifier = Modifier.fillMaxSize()) {
                // ROW 0: Top-Left (Houses 2 & 3), Top-Center (House 1), Top-Right (Houses 12 & 11)
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    // Top-Left Cell
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        // Inner Triangle (House 2)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(2) }
                                .padding(top = 16.dp, end = 6.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                PlanetsListBadge(housePlanets[2] ?: emptyList(), isSinhala)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            }
                        }
                        // Outer Triangle (House 3)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(3) }
                                .padding(start = 6.dp, bottom = 6.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Text("HL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(2.dp))
                                PlanetsListBadge(housePlanets[3] ?: emptyList(), isSinhala)
                                Text("3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            }
                        }
                    }

                    // Top-Center Cell: House 1 (Lagna House)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onHouseClick(1) }
                            .padding(4.dp)
                    ) {
                        // Arudha Tag top-left
                        Text(
                            text = "A4",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.TopStart)
                        )

                        // Center planet
                        Box(
                            modifier = Modifier.align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            PlanetsListBadge(housePlanets[1] ?: emptyList(), isSinhala, isCenter = true)
                        }

                        // House 1 Teal Badge bottom-left
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF00897B))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "1",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Top-Right Cell: Houses 12 & 11
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        // Inner Triangle (House 12)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(12) }
                                .padding(top = 4.dp, start = 6.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column {
                                Text("A9 GL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(2.dp))
                                PlanetsListBadge(housePlanets[12] ?: emptyList(), isSinhala)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("12", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            }
                        }
                        // Outer Triangle (House 11)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(11) }
                                .padding(end = 6.dp, bottom = 4.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                PlanetsListBadge(housePlanets[11] ?: emptyList(), isSinhala)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("11", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("A3", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // ROW 1: House 4 (Left), Center Box (Rashi Emblem), House 10 (Right)
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    // Left Cell: House 4
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onHouseClick(4) }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "AL A8",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.TopStart)
                        )

                        Box(
                            modifier = Modifier.align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            PlanetsListBadge(housePlanets[4] ?: emptyList(), isSinhala, isCenter = true)
                        }

                        Text(
                            text = "4",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }

                    // CENTER BOX: Degree, Animal Emblem, Sign Name Pill Badge
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFF9F8F5))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Exact degree formatted like 03:05:16
                            Text(
                                text = String.format("%02d:%02d:%02d", result.lagnaDegree, result.lagnaMinute, 16),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Vector Animal Emblem for Lagna Sign
                            ZodiacSignEmblem(
                                sign = lagnaSign,
                                color = Color(0xFF1E88E5),
                                size = 46.dp
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            // Blue Pill Badge with Sign Name
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E88E5))
                                    .padding(horizontal = 14.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isSinhala) lagnaSign.sinhalaName else lagnaSign.englishName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Right Cell: House 10
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onHouseClick(10) }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "A2",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )

                        Box(
                            modifier = Modifier.align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            PlanetsListBadge(housePlanets[10] ?: emptyList(), isSinhala, isCenter = true)
                        }

                        Text(
                            text = "10",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }

                // ROW 2: Bottom-Left (Houses 5 & 6), Bottom-Center (House 7), Bottom-Right (Houses 8 & 9)
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    // Bottom-Left Cell: Houses 5 & 6
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        // Outer Triangle (House 5)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(5) }
                                .padding(start = 6.dp, top = 4.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("A7", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("5", fontSize = 10.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                PlanetsListBadge(housePlanets[5] ?: emptyList(), isSinhala)
                            }
                        }
                        // Inner Triangle (House 6)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(6) }
                                .padding(end = 6.dp, bottom = 4.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("6", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                Spacer(modifier = Modifier.height(2.dp))
                                PlanetsListBadge(housePlanets[6] ?: emptyList(), isSinhala)
                            }
                        }
                    }

                    // Bottom-Center Cell: House 7
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onHouseClick(7) }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "7",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )

                        Box(
                            modifier = Modifier.align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            PlanetsListBadge(housePlanets[7] ?: emptyList(), isSinhala, isCenter = true)
                        }

                        Text(
                            text = "A10",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }

                    // Bottom-Right Cell: Houses 8 & 9
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        // Inner Triangle (House 8)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(8) }
                                .padding(start = 4.dp, top = 4.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Green circle badge for 8
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2E7D32)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("8", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("9", fontSize = 10.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                PlanetsListBadge(housePlanets[8] ?: emptyList(), isSinhala)
                            }
                        }
                        // Outer Triangle (House 9)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onHouseClick(9) }
                                .padding(end = 6.dp, bottom = 4.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("A11 UL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(2.dp))
                                PlanetsListBadge(housePlanets[9] ?: emptyList(), isSinhala)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("A5 A6", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Sub-Title Bar with # Icon and Options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF388E3C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = if (isNavamsha) {
                    if (isSinhala) "නවාංශ (D9) කේන්ද්‍රය" else "Navamsha (D9) Chart"
                } else {
                    if (isSinhala) "රාශි (D1) කේන්ද්‍රය" else "Rashi (D1) Chart"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD46B18),
                textAlign = TextAlign.Center
            )

            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Options",
                tint = Color(0xFF388E3C),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PlanetsListBadge(
    planets: List<PlanetPosition>,
    isSinhala: Boolean,
    isCenter: Boolean = false
) {
    if (planets.isEmpty()) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isCenter) Arrangement.Center else Arrangement.Start
    ) {
        planets.take(3).forEach { p ->
            val pName = when (p.planet) {
                Planet.SUN -> if (isSinhala) "ර" else "Su"
                Planet.MOON -> if (isSinhala) "ච" else "Mo"
                Planet.MARS -> if (isSinhala) "කු" else "Ma"
                Planet.MERCURY -> if (isSinhala) "බු" else "Me"
                Planet.JUPITER -> if (isSinhala) "ගු" else "Ju"
                Planet.VENUS -> if (isSinhala) "සි" else "Ve"
                Planet.SATURN -> if (isSinhala) "ශ" else "Sa"
                Planet.RAHU -> if (isSinhala) "රා" else "Ra"
                Planet.KETU -> if (isSinhala) "කේ" else "Ke"
            }

            val pColor = when (p.planet) {
                Planet.MARS -> Color(0xFFD32F2F) // Red
                Planet.RAHU, Planet.KETU -> Color(0xFF2E7D32) // Green
                Planet.VENUS -> Color(0xFF1565C0) // Blue
                Planet.JUPITER -> Color(0xFFB78103) // Gold/Brown
                Planet.SUN -> Color(0xFF546E7A) // Blue-grey
                else -> Color(0xFF212121)
            }

            val retroStr = if (p.isRetrograde) "*" else ""

            Text(
                text = "$pName$retroStr",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = pColor,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}
