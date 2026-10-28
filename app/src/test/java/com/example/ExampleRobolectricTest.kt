package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.calc.AstrologyCalculator
import com.example.data.calc.PanchangaCalculator
import com.example.data.model.Planet
import com.example.data.model.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Astro-Life", appName)
  }

  @Test
  fun `birth chart generated using current time produces complete horoscope`() {
    val now = Calendar.getInstance()
    val chart = AstrologyCalculator.calculateBirthChart(
      name = "වත්මන් කේන්ද්‍රය",
      gender = "MALE",
      year = now.get(Calendar.YEAR),
      month = now.get(Calendar.MONTH) + 1,
      day = now.get(Calendar.DAY_OF_MONTH),
      hour = now.get(Calendar.HOUR_OF_DAY),
      minute = now.get(Calendar.MINUTE),
      locationName = "කොළඹ (Colombo)",
      latitude = 6.9271,
      longitude = 79.8612
    )

    assertNotNull(chart)
    assertNotNull(chart.lagnaSign)
    assertNotNull(chart.navamshaLagnaSign)
    assertEquals(12, chart.houses.size)
    assertEquals(9, chart.planetaryPositions.size)
    assertEquals(9, chart.dashaPeriods.size)
    assertNotNull(chart.vimsottariAnalysis)
    assertNotNull(chart.lifeDestiny)
  }

  @Test
  fun `astrology calculator produces valid birth chart`() {
    val chart = AstrologyCalculator.calculateBirthChart(
      name = "ටෙස්ට් කේන්ද්‍රය",
      gender = "MALE",
      year = 1996,
      month = 5,
      day = 18,
      hour = 9,
      minute = 45,
      locationName = "කොළඹ",
      latitude = 6.9271,
      longitude = 79.8612
    )

    assertNotNull(chart)
    assertEquals(12, chart.houses.size)
    assertEquals(9, chart.planetaryPositions.size)
    assertEquals(9, chart.dashaPeriods.size)
    assertNotNull(chart.marriagePrediction)
    assertNotNull(chart.childrenPrediction)
    assertTrue(chart.yogas.isNotEmpty())
  }

  @Test
  fun `1984-09-12 Matara birth chart verifies Venus in House 12 and Mars in House 2`() {
    val chart = AstrologyCalculator.calculateBirthChart(
      name = "මාතර උපත",
      gender = "MALE",
      year = 1984,
      month = 9,
      day = 12,
      hour = 8,
      minute = 50,
      locationName = "මාතර (Matara)",
      latitude = 5.9549,
      longitude = 80.5550
    )

    assertNotNull(chart)
    // Tula Lagna (Libra)
    assertEquals(ZodiacSign.LIBRA, chart.lagnaSign)

    val mars = chart.planetaryPositions.find { it.planet == Planet.MARS }
    assertNotNull(mars)
    assertEquals(2, mars?.house) // Mars in 2nd house (Scorpio)

    val venus = chart.planetaryPositions.find { it.planet == Planet.VENUS }
    assertNotNull(venus)
    assertEquals(12, venus?.house) // Venus in 12th house (Virgo)
  }

  @Test
  fun `panchanga calculator produces daily litha`() {
    val litha = PanchangaCalculator.getDailyPanchanga(Calendar.getInstance())
    assertNotNull(litha)
    assertTrue(litha.rahuKalayaStart.isNotEmpty())
    assertTrue(litha.rahuKalayaEnd.isNotEmpty())
    assertTrue(litha.auspiciousTimes.isNotEmpty())
  }

  @Test
  fun `vimshottari dasha calculates major periods and sub bhuktis with date ranges`() {
    val chart = AstrologyCalculator.calculateBirthChart(
      name = "මාතර උපත",
      gender = "MALE",
      year = 1984,
      month = 9,
      day = 12,
      hour = 8,
      minute = 50,
      locationName = "මාතර (Matara)",
      latitude = 5.9549,
      longitude = 80.5550
    )

    assertNotNull(chart)
    assertEquals(9, chart.dashaPeriods.size)

    // Check that each Mahadasha contains 9 sub Bhuktis
    val fullDasha = chart.dashaPeriods[1] // 2nd dasha is a full dasha
    assertEquals(9, fullDasha.bhuktis.size)
    assertTrue(fullDasha.bhuktis.all { it.startDateStr.isNotEmpty() && it.endDateStr.isNotEmpty() })

    // Check comprehensive life analysis
    val analysis = chart.vimsottariAnalysis
    assertNotNull(analysis)

    // Current Dasha & Bhukti
    assertNotNull(analysis?.currentDashaBhukti)
    assertTrue((analysis?.currentDashaBhukti?.elapsedPercentage ?: -1f) >= 0f)

    // Inauspicious Periods & Erashtaka
    assertTrue(analysis?.inauspiciousPeriods?.isNotEmpty() == true)
    assertTrue(analysis?.inauspiciousPeriods?.any { it.titleSinhala.contains("ඒරාෂ්ටකය") } == true)

    // Life Doshas
    assertTrue(analysis?.lifeDoshas?.isNotEmpty() == true)
    assertTrue(analysis?.lifeDoshas?.any { it.doshaNameSinhala.contains("කුජ") } == true)

    // Longevity & Maraka
    assertNotNull(analysis?.longevityMaraka)
    assertTrue(analysis?.longevityMaraka?.ayurdayaCategorySinhala?.isNotEmpty() == true)
    assertTrue(analysis?.longevityMaraka?.cautionLifeBracketsSinhala?.isNotEmpty() == true)

    // Past Life
    assertNotNull(analysis?.pastLifeKarma)
    assertTrue(analysis?.pastLifeKarma?.pastLifeIdentitySinhala?.isNotEmpty() == true)

    // Prosperity Guidance
    assertNotNull(analysis?.prosperityGuidance)
    assertTrue(analysis?.prosperityGuidance?.luckyGemstoneSinhala?.isNotEmpty() == true)
    assertTrue(analysis?.prosperityGuidance?.luckyNumbers?.isNotEmpty() == true)

    // Check Life Destiny Predictions (Foreign travel, wealth, vehicles, triumphs, family gains, health, dana)
    val destiny = chart.lifeDestiny
    assertNotNull(destiny)
    assertNotNull(destiny?.foreignTravel)
    assertTrue(destiny?.foreignTravel?.favorablePeriodsSinhala?.isNotEmpty() == true)
    assertNotNull(destiny?.wealth)
    assertTrue(destiny?.wealth?.dhanaYogasSinhala?.isNotEmpty() == true)
    assertNotNull(destiny?.propertyAndVehicle)
    assertTrue(destiny?.propertyAndVehicle?.houseConstructionTimingSinhala?.isNotEmpty() == true)
    assertTrue(destiny?.propertyAndVehicle?.vehicleAcquisitionTimingSinhala?.isNotEmpty() == true)
    assertNotNull(destiny?.triumphsAndSetbacks)
    assertTrue(destiny?.triumphsAndSetbacks?.majorTriumphs?.isNotEmpty() == true)
    assertNotNull(destiny?.familyGains)
    assertTrue(destiny?.familyGains?.gainsFromSpouseSinhala?.isNotEmpty() == true)
    assertTrue(destiny?.familyGains?.childrenFutureProspectsSinhala?.isNotEmpty() == true)
    assertNotNull(destiny?.health)
    assertTrue(destiny?.health?.vulnerableBodySystemsSinhala?.isNotEmpty() == true)
    assertNotNull(destiny?.meritoriousDeeds)
    assertTrue(destiny?.meritoriousDeeds?.priorityCharityListSinhala?.isNotEmpty() == true)
  }
}
