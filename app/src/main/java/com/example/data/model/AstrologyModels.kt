package com.example.data.model

data class CityLocation(
    val sinhalaName: String,
    val englishName: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffset: Double = 5.5
) {
    companion object {
        val SRI_LANKAN_CITIES = listOf(
            CityLocation("කොළඹ (Colombo)", "Colombo", 6.9271, 79.8612),
            CityLocation("මහනුවර (Kandy)", "Kandy", 7.2906, 80.6337),
            CityLocation("ගාල්ල (Galle)", "Galle", 6.0535, 80.2210),
            CityLocation("යාපනය (Jaffna)", "Jaffna", 9.6615, 80.0255),
            CityLocation("කුරුණෑගල (Kurunegala)", "Kurunegala", 7.4863, 80.3623),
            CityLocation("අනුරාධපුරය (Anuradhapura)", "Anuradhapura", 8.3114, 80.4037),
            CityLocation("රත්නපුර (Ratnapura)", "Ratnapura", 6.6828, 80.4018),
            CityLocation("බදුල්ල (Badulla)", "Badulla", 6.9934, 81.0550),
            CityLocation("මීගමුව (Negombo)", "Negombo", 7.2008, 79.8736),
            CityLocation("මාතර (Matara)", "Matara", 5.9549, 80.5550),
            CityLocation("මඩකලපුව (Batticaloa)", "Batticaloa", 7.7310, 81.6747),
            CityLocation("ත්‍රිකුණාමලය (Trincomalee)", "Trincomalee", 8.5874, 81.2152),
            CityLocation("කළුතර (Kalutara)", "Kalutara", 6.5854, 79.9607),
            CityLocation("ගම්පහ (Gampaha)", "Gampaha", 7.0840, 79.9939),
            CityLocation("හම්බන්තොට (Hambantota)", "Hambantota", 6.1429, 81.1212),
            CityLocation("නුවරඑළිය (Nuwara Eliya)", "Nuwara Eliya", 6.9497, 80.7891),
            CityLocation("පොළොන්නරුව (Polonnaruwa)", "Polonnaruwa", 7.9403, 81.0188),
            CityLocation("කෑගල්ල (Kegalle)", "Kegalle", 7.2513, 80.3464),
            CityLocation("මොණරාගල (Monaragala)", "Monaragala", 6.8728, 81.3507),
            CityLocation("පුත්තලම (Puttalam)", "Puttalam", 8.0362, 79.8283)
        )
    }
}

data class PlanetPosition(
    val planet: Planet,
    val longitude: Double,
    val sign: ZodiacSign,
    val degreeInSign: Int,
    val minuteInSign: Int,
    val house: Int, // 1 to 12 from Lagna
    val isRetrograde: Boolean,
    val dignity: Dignity,
    val nakshatra: Nakshatra,
    val pada: Int,
    val navamshaSign: ZodiacSign
)

data class HouseInfo(
    val houseNumber: Int, // 1 to 12
    val bhavaNameSinhala: String,
    val bhavaNameEnglish: String,
    val sign: ZodiacSign,
    val lord: Planet,
    val planetsInside: List<PlanetPosition>,
    val significanceSinhala: String,
    val significanceEnglish: String,
    val readingSinhala: String,
    val readingEnglish: String
)

data class YogaInfo(
    val nameSinhala: String,
    val nameEnglish: String,
    val categorySinhala: String,
    val categoryEnglish: String,
    val planetsInvolved: List<Planet>,
    val descriptionSinhala: String,
    val descriptionEnglish: String,
    val futureImpactSinhala: String,
    val futureImpactEnglish: String,
    val activationTimeSinhala: String,
    val activationTimeEnglish: String
)

data class BhuktiPeriod(
    val majorPlanet: Planet,
    val subPlanet: Planet,
    val startDateStr: String,
    val endDateStr: String,
    val startYear: Int,
    val endYear: Int,
    val durationMonths: Double,
    val isCurrent: Boolean,
    val effectSinhala: String,
    val effectEnglish: String,
    val isChallenging: Boolean
)

data class DashaPeriod(
    val planet: Planet,
    val startYear: Int,
    val endYear: Int,
    val startDateStr: String,
    val endDateStr: String,
    val durationYears: Double,
    val isCurrent: Boolean,
    val forecastSinhala: String,
    val forecastEnglish: String,
    val bhuktis: List<BhuktiPeriod> = emptyList()
)

data class CurrentDashaBhuktiInfo(
    val majorPlanet: Planet,
    val subPlanet: Planet,
    val majorStartDateStr: String,
    val majorEndDateStr: String,
    val bhuktiStartDateStr: String,
    val bhuktiEndDateStr: String,
    val elapsedPercentage: Float, // 0 to 100
    val remainingDays: Int,
    val overallInfluenceSinhala: String,
    val overallInfluenceEnglish: String,
    val guidanceSinhala: String,
    val guidanceEnglish: String
)

data class InauspiciousPeriod(
    val titleSinhala: String,
    val titleEnglish: String,
    val categorySinhala: String, // ඒරාෂ්ටක, මාරක, දුෂ්ටස්ථාන අපල
    val categoryEnglish: String,
    val dateRangeStr: String,
    val ageWindowStr: String,
    val severityLevel: String, // HIGH, MEDIUM, MODERATE
    val warningDescriptionSinhala: String,
    val warningDescriptionEnglish: String,
    val precautionsSinhala: String,
    val precautionsEnglish: String,
    val remediesSinhala: String,
    val remediesEnglish: String
)

data class LifeDoshaInfo(
    val doshaNameSinhala: String,
    val doshaNameEnglish: String,
    val isPresent: Boolean,
    val intensityLevel: String, // STRONG, MODERATE, MILD, CANCELLED
    val affectedLifeAreasSinhala: String,
    val affectedLifeAreasEnglish: String,
    val criticalTimeRangeSinhala: String,
    val criticalTimeRangeEnglish: String,
    val descriptionSinhala: String,
    val descriptionEnglish: String,
    val remediesSinhala: String,
    val remediesEnglish: String
)

data class LongevityMarakaInfo(
    val ayurdayaCategorySinhala: String, // දීර්ඝ ආයුෂ (වසර 75-100+), මධ්‍ය ආයුෂ (වසර 60-75)
    val ayurdayaCategoryEnglish: String,
    val eighthHouseLordStatusSinhala: String,
    val eighthHouseLordStatusEnglish: String,
    val saturnLongevityFactorSinhala: String,
    val saturnLongevityFactorEnglish: String,
    val marakaHousesAnalysisSinhala: String, // 2 සහ 7 අධිපතියන්
    val marakaHousesAnalysisEnglish: String,
    val cautionLifeBracketsSinhala: List<String>,
    val cautionLifeBracketsEnglish: List<String>,
    val lifeExtendingRemediesSinhala: String,
    val lifeExtendingRemediesEnglish: String
)

data class PastLifeKarmaInfo(
    val pastLifeIdentitySinhala: String,
    val pastLifeIdentityEnglish: String,
    val accumulatedGoodMeritSinhala: String,
    val accumulatedGoodMeritEnglish: String,
    val pastLifeKarmaDebtSinhala: String,
    val pastLifeKarmaDebtEnglish: String,
    val ketuSpiritualLessonSinhala: String,
    val ketuSpiritualLessonEnglish: String,
    val rahuSoulMissionSinhala: String,
    val rahuSoulMissionEnglish: String,
    val soulEvolutionAdviceSinhala: String,
    val soulEvolutionAdviceEnglish: String
)

data class ProsperityGuidanceInfo(
    val luckyGemstoneSinhala: String,
    val luckyGemstoneEnglish: String,
    val gemstoneInstructionsSinhala: String,
    val gemstoneInstructionsEnglish: String,
    val luckyColorsSinhala: List<String>,
    val luckyColorsEnglish: List<String>,
    val unluckyColorsSinhala: List<String>,
    val unluckyColorsEnglish: List<String>,
    val luckyNumbers: List<Int>,
    val auspiciousDirectionsSinhala: String,
    val auspiciousDirectionsEnglish: String,
    val personalDeityWorshipSinhala: String,
    val personalDeityWorshipEnglish: String,
    val dailySuccessRitualsSinhala: List<String>,
    val dailySuccessRitualsEnglish: List<String>
)

data class VimshottariDashaAnalysis(
    val currentDashaBhukti: CurrentDashaBhuktiInfo?,
    val inauspiciousPeriods: List<InauspiciousPeriod>,
    val lifeDoshas: List<LifeDoshaInfo>,
    val longevityMaraka: LongevityMarakaInfo,
    val pastLifeKarma: PastLifeKarmaInfo,
    val prosperityGuidance: ProsperityGuidanceInfo
)

data class MarriagePrediction(
    val seventhHouseSign: ZodiacSign,
    val seventhLord: Planet,
    val seventhLordHouse: Int,
    val planetsInSeventh: List<Planet>,
    val hasKujaDosha: Boolean,
    val isKujaDoshaCancelled: Boolean,
    val kujaDoshaRemedySinhala: String,
    val kujaDoshaRemedyEnglish: String,
    val spousePersonalitySinhala: String,
    val spousePersonalityEnglish: String,
    val spouseAppearanceSinhala: String,
    val spouseAppearanceEnglish: String,
    val spouseProfessionSinhala: String,
    val spouseProfessionEnglish: String,
    val maritalHarmonySinhala: String,
    val maritalHarmonyEnglish: String,
    val marriageAgeWindowsSinhala: String,
    val marriageAgeWindowsEnglish: String,
    val marriageFavorableDatesSinhala: String,
    val marriageFavorableDatesEnglish: String
)

data class ChildrenPrediction(
    val fifthHouseSign: ZodiacSign,
    val fifthLord: Planet,
    val fifthLordHouse: Int,
    val planetsInFifth: List<Planet>,
    val jupiterDignity: Dignity,
    val childrenCountIndicationSinhala: String,
    val childrenCountIndicationEnglish: String,
    val childrenNatureSinhala: String,
    val childrenNatureEnglish: String,
    val favorablePeriodSinhala: String,
    val favorablePeriodEnglish: String,
    val astrologicalGuidanceSinhala: String,
    val astrologicalGuidanceEnglish: String
)

data class CareerPrediction(
    val tenthHouseSign: ZodiacSign,
    val tenthLord: Planet,
    val tenthLordHouse: Int,
    val planetsInTenth: List<Planet>,
    val suitableFieldsSinhala: String,
    val suitableFieldsEnglish: String,
    val wealthProspectsSinhala: String,
    val wealthProspectsEnglish: String
)

data class ForeignTravelPrediction(
    val hasForeignTravelYoga: Boolean,
    val settlementPotentialSinhala: String,
    val settlementPotentialEnglish: String,
    val twelfthHouseStatusSinhala: String,
    val twelfthHouseStatusEnglish: String,
    val ninthHouseStatusSinhala: String,
    val ninthHouseStatusEnglish: String,
    val rahuImpactSinhala: String,
    val rahuImpactEnglish: String,
    val favorablePeriodsSinhala: List<String>,
    val favorablePeriodsEnglish: List<String>,
    val suitableCountriesSinhala: String,
    val suitableCountriesEnglish: String,
    val remediesSinhala: String,
    val remediesEnglish: String
)

data class WealthPrediction(
    val wealthPotentialGradeSinhala: String,
    val wealthPotentialGradeEnglish: String,
    val dhanaYogasSinhala: List<String>,
    val dhanaYogasEnglish: List<String>,
    val secondHouseWealthStatusSinhala: String,
    val secondHouseWealthStatusEnglish: String,
    val eleventhHouseGainsStatusSinhala: String,
    val eleventhHouseGainsStatusEnglish: String,
    val suddenWealthProspectsSinhala: String,
    val suddenWealthProspectsEnglish: String,
    val favorableWealthPeriodsSinhala: List<String>,
    val favorableWealthPeriodsEnglish: List<String>,
    val wealthPreservationAdviceSinhala: String,
    val wealthPreservationAdviceEnglish: String
)

data class PropertyVehiclePrediction(
    val fourthHouseStatusSinhala: String,
    val fourthHouseStatusEnglish: String,
    val marsBhoomiKarakaStatusSinhala: String,
    val marsBhoomiKarakaStatusEnglish: String,
    val venusVahanaKarakaStatusSinhala: String,
    val venusVahanaKarakaStatusEnglish: String,
    val houseConstructionTimingSinhala: List<String>,
    val houseConstructionTimingEnglish: List<String>,
    val vehicleAcquisitionTimingSinhala: List<String>,
    val vehicleAcquisitionTimingEnglish: List<String>,
    val landPropertyInheritanceSinhala: String,
    val landPropertyInheritanceEnglish: String,
    val propertyPrecautionsSinhala: String,
    val propertyPrecautionsEnglish: String
)

data class LifeMilestoneEvent(
    val titleSinhala: String,
    val titleEnglish: String,
    val periodRangeStr: String,
    val ageWindowStr: String,
    val isTriumph: Boolean,
    val descriptionSinhala: String,
    val descriptionEnglish: String,
    val strategicGuidanceSinhala: String,
    val strategicGuidanceEnglish: String
)

data class LifeTriumphsAndSetbacks(
    val majorTriumphs: List<LifeMilestoneEvent>,
    val majorSetbacks: List<LifeMilestoneEvent>,
    val turningPointAgesSinhala: String,
    val turningPointAgesEnglish: String
)

data class MeritoriousDeedsGuidance(
    val priorityCharityListSinhala: List<String>,
    val priorityCharityListEnglish: List<String>,
    val karmicMeritImpactSinhala: String,
    val karmicMeritImpactEnglish: String,
    val bestDaysForDanaSinhala: String,
    val bestDaysForDanaEnglish: String
)

data class FamilyGainsPrediction(
    val gainsFromSpouseSinhala: String,
    val gainsFromSpouseEnglish: String,
    val gainsFromChildrenSinhala: String,
    val gainsFromChildrenEnglish: String,
    val childrenFutureProspectsSinhala: String,
    val childrenFutureProspectsEnglish: String,
    val familyProsperityTimelineSinhala: List<String>,
    val familyProsperityTimelineEnglish: List<String>
)

data class HealthPrediction(
    val overallVitalitySinhala: String,
    val overallVitalityEnglish: String,
    val vulnerableBodySystemsSinhala: List<String>,
    val vulnerableBodySystemsEnglish: List<String>,
    val sensitiveAgeWindowsSinhala: List<String>,
    val sensitiveAgeWindowsEnglish: List<String>,
    val ayurvedicLifestyleAdviceSinhala: String,
    val ayurvedicLifestyleAdviceEnglish: String
)

data class LifeDestinyAnalysis(
    val foreignTravel: ForeignTravelPrediction,
    val wealth: WealthPrediction,
    val propertyAndVehicle: PropertyVehiclePrediction,
    val triumphsAndSetbacks: LifeTriumphsAndSetbacks,
    val meritoriousDeeds: MeritoriousDeedsGuidance,
    val familyGains: FamilyGainsPrediction,
    val health: HealthPrediction
)

data class BirthChartResult(
    val name: String,
    val gender: String,
    val birthDateFormatted: String,
    val birthTimeFormatted: String,
    val locationName: String,
    val lagnaSign: ZodiacSign,
    val lagnaDegree: Int,
    val lagnaMinute: Int,
    val moonSign: ZodiacSign,
    val moonNakshatra: Nakshatra,
    val moonPada: Int,
    val navamshaLagnaSign: ZodiacSign,
    val planetaryPositions: List<PlanetPosition>,
    val houses: List<HouseInfo>,
    val yogas: List<YogaInfo>,
    val dashaPeriods: List<DashaPeriod>,
    val currentDasha: DashaPeriod?,
    val marriagePrediction: MarriagePrediction,
    val childrenPrediction: ChildrenPrediction,
    val careerPrediction: CareerPrediction,
    val generalSummarySinhala: String,
    val generalSummaryEnglish: String,
    val vimsottariAnalysis: VimshottariDashaAnalysis? = null,
    val lifeDestiny: LifeDestinyAnalysis? = null
)

data class DailyPanchanga(
    val dateFormatted: String,
    val dayOfWeekSinhala: String,
    val dayOfWeekEnglish: String,
    val tithiSinhala: String,
    val tithiEnglish: String,
    val nakshatraSinhala: String,
    val nakshatraEnglish: String,
    val yogaSinhala: String,
    val yogaEnglish: String,
    val karanaSinhala: String,
    val karanaEnglish: String,
    val rahuKalayaStart: String,
    val rahuKalayaEnd: String,
    val maruSituDisawaSinhala: String,
    val maruSituDisawaEnglish: String,
    val subhaDisawaSinhala: String,
    val subhaDisawaEnglish: String,
    val auspiciousTimes: List<AuspiciousMuhurtha>,
    val dailyAdviceSinhala: String,
    val dailyAdviceEnglish: String
)

data class AuspiciousMuhurtha(
    val id: String,
    val titleSinhala: String,
    val titleEnglish: String,
    val category: String, // MARRIAGE, HOUSE, BUSINESS, VEHICLE, EDUCATION
    val dateStr: String,
    val timeRange: String,
    val subhaDirectionSinhala: String,
    val subhaDirectionEnglish: String,
    val descriptionSinhala: String,
    val descriptionEnglish: String
)
