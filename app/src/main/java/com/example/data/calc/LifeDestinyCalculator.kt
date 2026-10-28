package com.example.data.calc

import com.example.data.model.*
import java.util.Calendar

object LifeDestinyCalculator {

    /**
     * Calculates comprehensive life destiny predictions:
     * Foreign travel, wealth gains, property/vehicles, life triumphs and setbacks,
     * meritorious deeds (dana), gains from spouse & children, children's future, and health vitality.
     */
    fun calculateLifeDestiny(
        birthYear: Int,
        lagnaSign: ZodiacSign,
        moonSign: ZodiacSign,
        positions: List<PlanetPosition>,
        houses: List<HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): LifeDestinyAnalysis {
        val posMap = positions.associateBy { it.planet }
        val houseMap = houses.associateBy { it.houseNumber }

        val foreignTravel = calculateForeignTravel(birthYear, lagnaSign, posMap, houseMap, dashaPeriods)
        val wealth = calculateWealth(birthYear, lagnaSign, posMap, houseMap, dashaPeriods)
        val propertyAndVehicle = calculatePropertyAndVehicle(birthYear, lagnaSign, posMap, houseMap, dashaPeriods)
        val triumphsAndSetbacks = calculateTriumphsAndSetbacks(birthYear, posMap, houseMap, dashaPeriods)
        val meritoriousDeeds = calculateMeritoriousDeeds(lagnaSign, posMap, houseMap)
        val familyGains = calculateFamilyGains(lagnaSign, posMap, houseMap, dashaPeriods)
        val health = calculateHealth(birthYear, lagnaSign, posMap, houseMap)

        return LifeDestinyAnalysis(
            foreignTravel = foreignTravel,
            wealth = wealth,
            propertyAndVehicle = propertyAndVehicle,
            triumphsAndSetbacks = triumphsAndSetbacks,
            meritoriousDeeds = meritoriousDeeds,
            familyGains = familyGains,
            health = health
        )
    }

    private fun calculateForeignTravel(
        birthYear: Int,
        lagnaSign: ZodiacSign,
        posMap: Map<Planet, PlanetPosition>,
        houseMap: Map<Int, HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): ForeignTravelPrediction {
        val twelfthHouse = houseMap[12] ?: houseMap.values.first()
        val ninthHouse = houseMap[9] ?: houseMap.values.first()
        val seventhHouse = houseMap[7] ?: houseMap.values.first()

        val twelfthLord = twelfthHouse.lord
        val ninthLord = ninthHouse.lord
        val rahu = posMap[Planet.RAHU]
        val moon = posMap[Planet.MOON]

        val has12thConnection = twelfthHouse.planetsInside.isNotEmpty() ||
                listOf(9, 12, 7, 3).contains(posMap[twelfthLord]?.house)
        val hasRahuForeign = rahu != null && listOf(12, 9, 7, 3, 10).contains(rahu.house)
        val isMovableMoon = moon != null && listOf(ZodiacSign.ARIES, ZodiacSign.CANCER, ZodiacSign.LIBRA, ZodiacSign.CAPRICORN).contains(moon.sign)

        val hasForeignYoga = has12thConnection || hasRahuForeign || isMovableMoon

        val settlementSin: String
        val settlementEng: String
        if (hasRahuForeign && has12thConnection) {
            settlementSin = "ස්ථිර විදේශ පදිංචිය (Permanent Residency / PR) හෝ දීර්ඝකාලීන රැකියා සඳහා උසස් විදේශ යෝගයක් පවතී."
            settlementEng = "High potential for permanent overseas settlement (PR/Citizenship) and lucrative long-term international careers."
        } else if (hasForeignYoga) {
            settlementSin = "වෘත්තීය, ව්‍යාපාරික හෝ අධ්‍යාපනික කටයුතු වෙනුවෙන් නිතර විදේශගත වීමේ යෝග්‍යතාවක් පවතී."
            settlementEng = "Frequent international travel for professional projects, scholastic advancements, and commercial ventures."
        } else {
            settlementSin = "විවේක සුවය හා සංචාරක අරමුණු සඳහා විදේශ ගමන් යෙදෙන අතර ස්ථිර පදිංචිය මව්බිමේ වඩාත් සුබදායකය."
            settlementEng = "Short-term foreign journeys for leisure and pilgrimage; permanent grounding in homeland is harmonious."
        }

        val twelfthStatusSin = "12 වන වැය හා විදේශස්ථානය ${twelfthHouse.sign.sinhalaName} වන අතර අධිපති ${twelfthLord.sinhalaName} වේ. විදේශ සබඳතා හා විදේශගත ආදායම් මාර්ගවලට යහපත් ඉඩකඩක් සපයයි."
        val twelfthStatusEng = "12th House of foreign lands is in ${twelfthHouse.sign.englishName} ruled by ${twelfthLord.englishName}, favoring global liaisons and foreign currency income."

        val ninthStatusSin = "9 වන භාග්‍ය හා දුර ගමන් ස්ථානය ${ninthHouse.sign.sinhalaName} වන අතර එහි අධිපති ${ninthLord.sinhalaName} විදේශීය ජයග්‍රහණවලට භාග්‍යය උදා කරයි."
        val ninthStatusEng = "9th House of long journeys is in ${ninthHouse.sign.englishName} ruled by ${ninthLord.englishName}, opening fortuitous horizons overseas."

        val rahuSin = if (rahu != null) "විදේශ කාරක රාහු ${rahu.house} වැන්නේ තැන්පත්ව ඇති බැවින් සාම්ප්‍රදායික සීමාවන් බිඳිමින් විදේශීය අවස්ථා ආකර්ෂණය කර ගැනීමේ නොතිත් ආශාවක් ලබාදෙයි."
        else "රාහු ග්‍රහයාගේ පිහිටීම විදේශ ගමන්වලට මධ්‍යස්ථ බලපෑමක් දක්වයි."
        val rahuEng = if (rahu != null) "Rahu in House ${rahu.house} fuels adventurous ambition to expand into international territories."
        else "Rahu offers moderate foreign travel influence."

        // Favorable timing for travel/visas
        val travelTimesSin = mutableListOf<String>()
        val travelTimesEng = mutableListOf<String>()

        val candidateDashas = dashaPeriods.filter {
            it.planet == Planet.RAHU || it.planet == twelfthLord || it.planet == ninthLord || it.planet == Planet.MOON
        }

        for (dasha in candidateDashas.take(3)) {
            travelTimesSin.add("${dasha.planet.sinhalaName} මහ දශාව (${dasha.startYear} - ${dasha.endYear}): විදේශ ගමන්, වීසා බලපත්‍ර ලැබීම් සහ විදේශ රැකියා සඳහා අතිශය සුබදායක කාලයකි.")
            travelTimesEng.add("${dasha.planet.englishName} Mahadasha (${dasha.startYear} - ${dasha.endYear}): Prime window for visa approvals, international relocation, and global employment.")
        }

        if (travelTimesSin.isEmpty()) {
            travelTimesSin.add("වයස 29 - 33 සහ 42 - 46 අතර කාලය: විදේශීය සංචාර හා අවස්ථා සඳහා විශේෂයෙන්ම හිතකරය.")
            travelTimesEng.add("Ages 29 - 33 and 42 - 46: Highly favorable windows for international travel and cross-border ventures.")
        }

        val countriesSin = "බටහිර හා උතුරු දිශානුගත රටවල් (එක්සත් රාජධානිය, යුරෝපය, කැනඩාව, ඕස්ට්‍රේලියාව) මෙන්ම ආසියාතික දියුණු රටවල් වඩාත් වාසනාවන්ත වේ."
        val countriesEng = "Western and northern geographies (UK, European Union, Canada, Australia) and developed Asian commerce hubs."

        val remediesSin = "විදේශ ගමන් සාර්ථක වීමට රාහු අපල සමනය කරගැනීම, බ්‍රහස්පතින්දා හෝ සෙනසුරාදා දිනවල බෝධි පූජා පැවැත්වීම, සහ පිටත්වීමට පෙර විෂ්ණු දෙවියන්ට භාරවීම ඉතා යහපත්ය."
        val remediesEng = "Rahu propitiation, lighting mustard oil lamps at Bodhi tree on Thursdays/Saturdays, and blessings at Lord Vishnu shrine."

        return ForeignTravelPrediction(
            hasForeignTravelYoga = hasForeignYoga,
            settlementPotentialSinhala = settlementSin,
            settlementPotentialEnglish = settlementEng,
            twelfthHouseStatusSinhala = twelfthStatusSin,
            twelfthHouseStatusEnglish = twelfthStatusEng,
            ninthHouseStatusSinhala = ninthStatusSin,
            ninthHouseStatusEnglish = ninthStatusEng,
            rahuImpactSinhala = rahuSin,
            rahuImpactEnglish = rahuEng,
            favorablePeriodsSinhala = travelTimesSin,
            favorablePeriodsEnglish = travelTimesEng,
            suitableCountriesSinhala = countriesSin,
            suitableCountriesEnglish = countriesEng,
            remediesSinhala = remediesSin,
            remediesEnglish = remediesEng
        )
    }

    private fun calculateWealth(
        birthYear: Int,
        lagnaSign: ZodiacSign,
        posMap: Map<Planet, PlanetPosition>,
        houseMap: Map<Int, HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): WealthPrediction {
        val secondHouse = houseMap[2] ?: houseMap.values.first()
        val eleventhHouse = houseMap[11] ?: houseMap.values.first()
        val ninthHouse = houseMap[9] ?: houseMap.values.first()

        val secondLord = secondHouse.lord
        val eleventhLord = eleventhHouse.lord
        val jupiter = posMap[Planet.JUPITER]

        val yogasListSin = mutableListOf<String>()
        val yogasListEng = mutableListOf<String>()

        yogasListSin.add("2 වන ධනස්ථානය (${secondHouse.sign.sinhalaName}) සහ 11 වන ලාභස්ථානය (${eleventhHouse.sign.sinhalaName}) සක්‍රිය ධන යෝගයක් නිර්මාණය කරයි.")
        yogasListEng.add("Alliance between 2nd house (${secondHouse.sign.englishName}) and 11th house (${eleventhHouse.sign.englishName}) establishes sustained wealth inflow.")

        if (jupiter != null && listOf(1, 2, 5, 9, 11).contains(jupiter.house)) {
            yogasListSin.add("ධනකාරක ගුරු ${jupiter.house} වැන්නේ පිහිටීම නිසා ලක්ෂ්මී යෝග බලය සහ මූල්‍ය ආශිර්වාදය හිමිවේ.")
            yogasListEng.add("Wealth karaka Jupiter in House ${jupiter.house} activates Lakshmi Yoga, multiplying assets and monetary grace.")
        }

        val gradeSin = "ස්ථාවර ඉහළ ධනවත්කම සහ ක්‍රමානුකූල වත්කම් වර්ධනය (High Enduring Prosperity)"
        val gradeEng = "Substantial enduring wealth with progressive multi-asset appreciation"

        val secondStatusSin = "2 වන ධනස්ථානාධිපති ${secondLord.sinhalaName} ග්‍රහයා මූල්‍ය සංචිත, බැංකු තැන්පතු සහ මුදල් ඉතිරිකිරීමේ ශක්තිය පාලනය කරයි."
        val secondStatusEng = "2nd lord ${secondLord.englishName} safeguards liquid capital reserves, bank holdings, and prudent reinvestments."

        val eleventhStatusSin = "11 වන අයස්ථානාධිපති ${eleventhLord.sinhalaName} ග්‍රහයා බහුවිධ ආදායම් මාර්ග සහ ව්‍යාපාරික ලාභ ලබාදීමට ශක්තිමත්ය."
        val eleventhStatusEng = "11th lord ${eleventhLord.englishName} generates multiple revenue streams and entrepreneurial surplus."

        val suddenSin = "5 වැන්න සහ 8 වැන්න ශුභ ග්‍රහයින්ගේ දෘෂ්ටියට ලක්වන බැවින් අනපේක්ෂිත දේපළ උරුමයන්, කොටස් වෙළඳපල ලාභ සහ අහඹු ධන වාසනාවන් ජීවිතයේ මැදිවියේදී උදාවේ."
        val suddenEng = "Favorable aspects on 5th and 8th houses indicate sudden windfalls, legacy inheritances, and profitable investments during middle years."

        val wealthPeriodsSin = mutableListOf<String>()
        val wealthPeriodsEng = mutableListOf<String>()

        val primeWealthDashas = dashaPeriods.filter {
            it.planet == secondLord || it.planet == eleventhLord || it.planet == Planet.JUPITER || it.planet == Planet.VENUS
        }

        for (dasha in primeWealthDashas.take(3)) {
            wealthPeriodsSin.add("${dasha.planet.sinhalaName} මහ දශාව (${dasha.startYear} - ${dasha.endYear}): ධන ධාන්‍ය සම්පත් උපරිමයෙන් වර්ධනය වන, ආයෝජන සාර්ථක වන ස්වර්ණමය කාලයකි.")
            wealthPeriodsEng.add("${dasha.planet.englishName} Mahadasha (${dasha.startYear} - ${dasha.endYear}): Peak prosperity window for capital accumulation and lucrative expansions.")
        }

        val adviceSin = "හදිසි ලාභ බලාපොරොත්තුවෙන් කෙරෙන අවදානම් සූදු හෝ තහනම් ගනුදෙනුවලින් වළකින්න. ඉඩකඩම්, ස්ථාවර තැන්පතු සහ රත්‍රං වැනි ස්ථිර වත්කම්වල ආයෝජනය කරන්න."
        val adviceEng = "Refrain from speculative gambling; anchor profits into tangible real estate, solid gold, and diversified long-term reserves."

        return WealthPrediction(
            wealthPotentialGradeSinhala = gradeSin,
            wealthPotentialGradeEnglish = gradeEng,
            dhanaYogasSinhala = yogasListSin,
            dhanaYogasEnglish = yogasListEng,
            secondHouseWealthStatusSinhala = secondStatusSin,
            secondHouseWealthStatusEnglish = secondStatusEng,
            eleventhHouseGainsStatusSinhala = eleventhStatusSin,
            eleventhHouseGainsStatusEnglish = eleventhStatusEng,
            suddenWealthProspectsSinhala = suddenSin,
            suddenWealthProspectsEnglish = suddenEng,
            favorableWealthPeriodsSinhala = wealthPeriodsSin,
            favorableWealthPeriodsEnglish = wealthPeriodsEng,
            wealthPreservationAdviceSinhala = adviceSin,
            wealthPreservationAdviceEnglish = adviceEng
        )
    }

    private fun calculatePropertyAndVehicle(
        birthYear: Int,
        lagnaSign: ZodiacSign,
        posMap: Map<Planet, PlanetPosition>,
        houseMap: Map<Int, HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): PropertyVehiclePrediction {
        val fourthHouse = houseMap[4] ?: houseMap.values.first()
        val fourthLord = fourthHouse.lord
        val mars = posMap[Planet.MARS]
        val venus = posMap[Planet.VENUS]

        val fourthSin = "4 වන ගෘහ, වාහන හා ඉඩකඩම් භාවය ${fourthHouse.sign.sinhalaName} වන අතර එහි අධිපති ${fourthLord.sinhalaName} වේ. මනරම් වාසස්ථානයක් සහ සුඛෝපභෝගී වාහන හිමිකම පෙන්නුම් කරයි."
        val fourthEng = "4th House of vehicles and estates is in ${fourthHouse.sign.englishName} ruled by ${fourthLord.englishName}, blessing the native with elegant properties and conveyances."

        val marsSin = if (mars != null) "භූමි කාරක කුජ ග්‍රහයා ${mars.house} වැන්නේ පිහිටීම නිසා ඉඩකඩම් හිමිකම, ගොඩනැගිලි ඉදිකිරීම් සහ නිවාස ප්‍රතිසංස්කරණ සාර්ථකව සිදුකිරීමේ ශක්තිය ලැබේ."
        else "කුජ ග්‍රහයා ඉඩකඩම් ක්ෂේත්‍රයේ සාමාන්‍ය ශක්තියක් ලබාදෙයි."
        val marsEng = if (mars != null) "Mars as Bhoomi Karaka (land significator) in House ${mars.house} bestows power to acquire plots, construct residences, and manage estates."
        else "Mars provides balanced real estate strength."

        val venusSin = if (venus != null) "වාහන කාරක සිකුරු ${venus.house} වැන්නේ තැන්පත්ව ඇති බැවින් නවීන, ආකර්ෂණීය සහ සැපපහසු වාහන හිමිකර ගැනීමේ භාග්‍යය නොඅඩුව ලැබේ."
        else "සිකුරු ග්‍රහයා මධ්‍යස්ථ වාහන වාසනාවක් සපයයි."
        val venusEng = if (venus != null) "Venus as Vahana Karaka in House ${venus.house} bestows aesthetic taste, comfortable cars, and enjoyable journeys."
        else "Venus grants standard vehicle fortune."

        // Construction timing
        val constructionTimingSin = listOf(
            "වයස 32 - 35 අතර කාලය: නිවසක් මිලදී ගැනීමට හෝ නව නිවසක මුල්ගල තැබීමට අතිශයින්ම සුබදායකය.",
            "වයස 46 - 49 අතර කාලය: දේපළ පුළුල් කිරීම, දෙවන නිවසක් හෝ වාණිජ ගොඩනැගිලි ඉදිකිරීම සඳහා ඉතා යහපත්ය."
        )
        val constructionTimingEng = listOf(
            "Ages 32 - 35: Ideal timeframe for laying foundation stones, purchasing property, or housewarming rituals.",
            "Ages 46 - 49: Favorable phase for property expansion, secondary estates, or commercial development."
        )

        // Vehicle acquisition timing
        val vehicleTimingSin = listOf(
            "වයස 28 - 30 අතර කාලය: ප්‍රථම මෝටර් රථය හෝ නවීන වාහනයක් මිලදී ගැනීමට සුබ යෝග උදාවේ.",
            "වයස 41 - 44 අතර කාලය: ඉහළ පන්තියේ සුඛෝපභෝගී වාහනයක් හිමිවීමේ භාග්‍යය පවතී."
        )
        val vehicleTimingEng = listOf(
            "Ages 28 - 30: Auspicious horizon for acquiring a personal automobile or upgrading transport.",
            "Ages 41 - 44: Period supporting luxury vehicle acquisitions and prestigious conveyances."
        )

        val inheritanceSin = "මවුපිය පාර්ශවයෙන් හිමිවන ඉඩකඩම් හෝ දේපළ අයිතියක් ඇති අතර, ස්වෝත්සාහයෙන් උපයා ගන්නා ඉඩකඩම් ඊට වඩා බලවත්ව වර්ධනය වේ."
        val inheritanceEng = "Entitled to ancestral or parental property portions, complemented by self-acquired real estate of higher value."

        val precautionsSin = "ඉඩම් හා නිවාස මිලදී ගැනීමේදී ඔප්පු තිරප්පු සහ නීතිමය ලියකියවිලි නීතිඥවරයෙකු ලවා සූක්ෂ්මව පරීක්ෂා කරගන්න. අඟහරුවාදා දිනවල ඉඩම් ගිවිසුම් අත්සන් කිරීමෙන් වළකින්න."
        val precautionsEng = "Conduct diligent title searches through qualified legal counsel before property transactions; avoid finalizing deed signings on Tuesdays."

        return PropertyVehiclePrediction(
            fourthHouseStatusSinhala = fourthSin,
            fourthHouseStatusEnglish = fourthEng,
            marsBhoomiKarakaStatusSinhala = marsSin,
            marsBhoomiKarakaStatusEnglish = marsEng,
            venusVahanaKarakaStatusSinhala = venusSin,
            venusVahanaKarakaStatusEnglish = venusEng,
            houseConstructionTimingSinhala = constructionTimingSin,
            houseConstructionTimingEnglish = constructionTimingEng,
            vehicleAcquisitionTimingSinhala = vehicleTimingSin,
            vehicleAcquisitionTimingEnglish = vehicleTimingEng,
            landPropertyInheritanceSinhala = inheritanceSin,
            landPropertyInheritanceEnglish = inheritanceEng,
            propertyPrecautionsSinhala = precautionsSin,
            propertyPrecautionsEnglish = precautionsEng
        )
    }

    private fun calculateTriumphsAndSetbacks(
        birthYear: Int,
        posMap: Map<Planet, PlanetPosition>,
        houseMap: Map<Int, HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): LifeTriumphsAndSetbacks {
        val triumphs = mutableListOf<LifeMilestoneEvent>()
        val setbacks = mutableListOf<LifeMilestoneEvent>()

        // Analyze Dasha timeline to produce explicit milestones
        for (dasha in dashaPeriods) {
            val ageStart = dasha.startYear - birthYear
            val ageEnd = dasha.endYear - birthYear

            if (ageStart > 85) continue

            when (dasha.planet) {
                Planet.JUPITER -> {
                    triumphs.add(
                        LifeMilestoneEvent(
                            titleSinhala = "ගුරු මහ දශාවේ ස්වර්ණමය යුගය සහ ධර්ම විජය",
                            titleEnglish = "Jupiter Mahadasha Golden Milestone",
                            periodRangeStr = "${dasha.startDateStr} සිට ${dasha.endDateStr} දක්වා",
                            ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                            isTriumph = true,
                            descriptionSinhala = "විශිෂ්ට බුද්ධිමය පිබිදීම, සමාජයේ මුදුන්පත් පිළිගැනීම, දරුවන්ගේ උපත හා දියුණුව, ස්ථාවර මූල්‍ය සමෘද්ධිය.",
                            descriptionEnglish = "Elevation in prestige, academic/professional mastery, joyful milestones with children, and immense spiritual protection.",
                            strategicGuidanceSinhala = "මෙම කාලය තුළ මහා පරිමාණ ආයෝජන සහ ආධ්‍යාත්මික පින්කම්වල උපරිමයෙන් නිරත වන්න.",
                            strategicGuidanceEnglish = "Deploy capital into long-term strategic investments and sponsor spiritual and educational charities."
                        )
                    )
                }
                Planet.VENUS -> {
                    triumphs.add(
                        LifeMilestoneEvent(
                            titleSinhala = "සිකුරු මහ දශාවේ සෞභාග්‍යය සහ සැප සම්පත්",
                            titleEnglish = "Venus Mahadasha Era of Luxury & Love",
                            periodRangeStr = "${dasha.startDateStr} සිට ${dasha.endDateStr} දක්වා",
                            ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                            isTriumph = true,
                            descriptionSinhala = "විවාහ මංගල්‍යය, පවුල් සතුට, වාහන හා අලංකාර නිවාස හිමිකම, කලාත්මක ක්ෂේත්‍රයේ හෝ ව්‍යාපාරයේ ඉහළම ජයග්‍රහණ.",
                            descriptionEnglish = "Matrimonial joy, acquisition of modern residences and luxury vehicles, aesthetic and commercial brilliance.",
                            strategicGuidanceSinhala = "පවුලේ සමගිය සහ සබඳතා ශක්තිමත් කරගනිමින් කලාකාමී සහ වාණිජ ක්ෂේත්‍රවල ඉදිරියට යන්න.",
                            strategicGuidanceEnglish = "Cultivate high aesthetic ventures, nurture partnerships, and enjoy refined domestic bliss."
                        )
                    )
                }
                Planet.MERCURY -> {
                    triumphs.add(
                        LifeMilestoneEvent(
                            titleSinhala = "බුධ මහ දශාවේ බුද්ධිමය හා වාණිජ ජයග්‍රහණ",
                            titleEnglish = "Mercury Mahadasha Commercial & Scholastic Triumph",
                            periodRangeStr = "${dasha.startDateStr} සිට ${dasha.endDateStr} දක්වා",
                            ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                            isTriumph = true,
                            descriptionSinhala = "ව්‍යාපාරික ක්ෂේත්‍රයේ සීඝ්‍ර ව්‍යාප්තිය, විභාග හා උසස් අධ්‍යාපන ජය, ලේඛන හා සන්නිවේදන ප්‍රවීණත්වය.",
                            descriptionEnglish = "Commercial expansion, scholastic laurels, negotiating mastery, and diversified business networks.",
                            strategicGuidanceSinhala = "නව තාක්ෂණය සහ සන්නිවේදන ශිල්පක්‍රම යොදා ගනිමින් නව ව්‍යාපාර ආරම්භ කරන්න.",
                            strategicGuidanceEnglish = "Leverage digital technology, data analysis, and persuasive communication to capture new markets."
                        )
                    )
                }
                Planet.SATURN -> {
                    setbacks.add(
                        LifeMilestoneEvent(
                            titleSinhala = "ශනි මහ දශාවේ අභියෝග සහ විනය පරීක්ෂණ",
                            titleEnglish = "Saturn Mahadasha Crucible of Resilience",
                            periodRangeStr = "${dasha.startDateStr} සිට ${dasha.endDateStr} දක්වා",
                            ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                            isTriumph = false,
                            descriptionSinhala = "අධික වෙහෙස, අනපේක්ෂිත ප්‍රමාදයන්, වගකීම් බර වැඩිවීම සහ සමීපතමයින්ගේ වෙනස්වීම් නිසා මානසික පීඩා ඇතිවිය හැක.",
                            descriptionEnglish = "Demanding responsibilities, delayed recognition, testing of endurance, and potential occupational friction.",
                            strategicGuidanceSinhala = "කලබල නොවී ඉවසීම ප්‍රගුණ කරන්න. අත්හැරීම සහ නොපසුබස්නා උත්සාහය තුළින් අවසානයේ විශිෂ්ට ජයග්‍රහණ ලබයි.",
                            strategicGuidanceEnglish = "Maintain unyielding discipline, avoid shortcuts, honor commitments, and support the working class."
                        )
                    )
                }
                Planet.RAHU -> {
                    setbacks.add(
                        LifeMilestoneEvent(
                            titleSinhala = "රාහු මහ දශාවේ අනපේක්ෂිත පෙරළි සහ මායාවන්",
                            titleEnglish = "Rahu Mahadasha Turbulence & Ambition",
                            periodRangeStr = "${dasha.startDateStr} සිට ${dasha.endDateStr} දක්වා",
                            ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                            isTriumph = false,
                            descriptionSinhala = "හදිසි උඩුයටිකුරු වීම්, රැවටිලිවලට හසුවීමේ අවදානම, විදේශීය ආකර්ෂණය සහ ආවේගශීලී තීරණ නිසා මූල්‍ය පාඩු.",
                            descriptionEnglish = "Volatile highs and lows, illusionary investments, foreign dislocation, and deceptive alliances.",
                            strategicGuidanceSinhala = "හදිසි ධනවත්වීමේ මායාවලින් වළකින්න. නීතිමය උපදෙස් නොමැතිව කිසිදු ලියවිල්ලකට අත්සන් නොතබන්න.",
                            strategicGuidanceEnglish = "Avoid get-rich-quick schemes; verify credentials meticulously before entering novel partnerships."
                        )
                    )
                }
                Planet.KETU -> {
                    setbacks.add(
                        LifeMilestoneEvent(
                            titleSinhala = "කේතු මහ දශාවේ විරාගය සහ හුදකලාව",
                            titleEnglish = "Ketu Mahadasha Detachment & Introspection",
                            periodRangeStr = "${dasha.startDateStr} සිට ${dasha.endDateStr} දක්වා",
                            ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                            isTriumph = false,
                            descriptionSinhala = "ලෞකික කටයුතු කෙරෙහි කලකිරීම, මිතුරන්ගෙන් ඈත්වීම, සුළු සෞඛ්‍ය ගැටළු හෝ ශාරීරික විඩාව.",
                            descriptionEnglish = "Apathy towards worldly ambition, feelings of isolation, physical depletion requiring rejuvenating retreats.",
                            strategicGuidanceSinhala = "ආධ්‍යාත්මික භාවනාවලට යොමු වී මනස සුවපත් කරගන්න. ලෞකික බලාපොරොත්තු සීමා කරන්න.",
                            strategicGuidanceEnglish = "Dedicate time to inner meditation, retreat from toxic social circles, and regenerate vitality."
                        )
                    )
                }
                else -> {}
            }
        }

        val turningPointsSin = "වයස 28 (ස්වාධීන වීම), වයස 36 (දෛවයේ ස්ථාවර පෙරළිය), වයස 48 (මූල්‍ය හා සමාජ මුදුන්පත් වීම) සහ වයස 56 (අධ්‍යාත්මික පරිනතභාවය)."
        val turningPointsEng = "Ages 28 (independence & career launch), 36 (major destiny turning point), 48 (zenith of status and wealth), and 56 (spiritual mastery)."

        return LifeTriumphsAndSetbacks(
            majorTriumphs = triumphs,
            majorSetbacks = setbacks,
            turningPointAgesSinhala = turningPointsSin,
            turningPointAgesEnglish = turningPointsEng
        )
    }

    private fun calculateMeritoriousDeeds(
        lagnaSign: ZodiacSign,
        posMap: Map<Planet, PlanetPosition>,
        houseMap: Map<Int, HouseInfo>
    ): MeritoriousDeedsGuidance {
        val charityListSin = mutableListOf<String>()
        val charityListEng = mutableListOf<String>()

        charityListSin.add("සාංඝික දාන පිරිනැමීම: ආරණ්‍ය සේනාසනවල භාවනානුයෝගී මහා සංඝරත්නය උදෙසා සිව්පසය (චීවර, පිණ්ඩපාත, සේනාසන, ගිලන්පස) පූජා කිරීම.")
        charityListEng.add("Sanghika Dana: Offering the four requisites (robes, almsfood, lodgings, medicines) to meditative monks in forest hermitages.")

        charityListSin.add("සත්ව අභයදානය: මරණයට කැපවූ ගවයින්, එළුවන් ඇතුළු සතුන් ඝාතනයෙන් මුදවා අභය භූමි වෙත යොමු කිරීම (ශනි සහ කුජ අපල දුරු වී ආයුෂ වැඩේ).")
        charityListEng.add("Animal Sanctuary (Abhayadana): Rescuing cattle and vulnerable animals from slaughterhouses, granting longevity and karmic immunity.")

        charityListSin.add("පානීය ජල දානය: ජල හිඟ ප්‍රදේශවල පාසල්වලට හෝ විහාරස්ථානවලට ජල පෙරණ (RO Filters) හෝ නල ළිං ඉදිකර දීම (සඳු සහ සිකුරු ශක්තියට).")
        charityListEng.add("Jala Dana (Water Wells): Installing water filtration systems or tube wells in drought-afflicted regions, strengthening Moon and Venus.")

        charityListSin.add("රෝගීන්ට උපස්ථාන සහ ඖෂධ දානය: පිළිකා රෝහල හෝ දුෂ්කර රෝහල්වල රෝගීන්ට අත්‍යවශ්‍ය ඖෂධ සහ උපකරණ පරිත්‍යාග කිරීම (සූර්ය හා ගුරු ආශිර්වාදයට).")
        charityListEng.add("Bhesajja Dana (Medical Aid): Supplying critical medicines and medical equipment to specialized cancer and rural hospitals.")

        charityListSin.add("ධර්ම දානය: බෞද්ධ සූත්‍ර දේශනා, ත්‍රිපිටක පොත්පත් හෝ දහම් පාසල් පෙළපොත් මුද්‍රණය කර නොමිලේ බෙදාහැරීම (බුධ ග්‍රහයාගේ ප්‍රඥා බලයට).")
        charityListEng.add("Dhamma Dana: Publishing and distributing sacred discourses, Dhamma textbooks to rural Sunday schools, elevating intellect.")

        charityListSin.add("බෝධි රෝපණය සහ පැළ සිටුවීම: සිසිලස ලබාදෙන ඇසතු බෝ රුක් සහ ඖෂධීය ශාක රෝපණය කර රැකබලා ගැනීම.")
        charityListEng.add("Bodhi & Tree Planting: Planting and tending sacred Bodhi saplings and medicinal trees, fostering cosmic harmony.")

        val meritImpactSin = "මෙම උදාර පින්කම් තුළින් ජන්ම කේන්ද්‍රයේ පවතින සියලු ග්‍රහ අපල, පෙර භවයේ අකුසල් සහ මාරක බාධක දුරුවී ජීවිතයේ සෑම අංශයකම සෞභාග්‍යය සහ මනසේ සාමය තහවුරු වේ."
        val meritImpactEng = "These noble acts cleanse past-life karmic encumbrances, pacify malefic planetary transits, and foster radiant peace and resilience."

        val bestDaysSin = "පොහෝ දිනවල, තම උපන් නැකත යෙදෙන දිනවල, හෝ ගුරු (බ්‍රහස්පතින්දා) සහ ශනි (සෙනසුරාදා) දිනවල මෙම පින්කම් කිරීම වඩාත් ප්‍රතිඵලදායකය."
        val bestDaysEng = "Full Moon Poya days, native's birth Nakshatra days, and Thursdays (Jupiter) or Saturdays (Saturn)."

        return MeritoriousDeedsGuidance(
            priorityCharityListSinhala = charityListSin,
            priorityCharityListEnglish = charityListEng,
            karmicMeritImpactSinhala = meritImpactSin,
            karmicMeritImpactEnglish = meritImpactEng,
            bestDaysForDanaSinhala = bestDaysSin,
            bestDaysForDanaEnglish = bestDaysEng
        )
    }

    private fun calculateFamilyGains(
        lagnaSign: ZodiacSign,
        posMap: Map<Planet, PlanetPosition>,
        houseMap: Map<Int, HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): FamilyGainsPrediction {
        val seventhHouse = houseMap[7] ?: houseMap.values.first()
        val eighthHouse = houseMap[8] ?: houseMap.values.first()
        val fifthHouse = houseMap[5] ?: houseMap.values.first()
        val ninthHouse = houseMap[9] ?: houseMap.values.first()

        val seventhLord = seventhHouse.lord
        val fifthLord = fifthHouse.lord

        val gainsSpouseSin = "සහකරු/සහකාරියගේ පැමිණීමත් සමඟ දෛවයේ සුවිශේෂී ධනවත් භාග්‍යයක් විවර වේ. විවාහයෙන් පසු ඉඩකඩම්, වාහන හෝ ඒකාබද්ධ ව්‍යාපාරික ජයග්‍රහණ හිමිවන අතර, සමාජයේ ඉහළ ගෞරවයක් ලඟාකර දීමට සහකරුගේ බුද්ධිය හා මගපෙන්වීම හේතු වේ."
        val gainsSpouseEng = "Matrimony marks a decisive economic leap. Partner brings tangible dowry/real estate, sharp financial acumen, joint enterprise success, and elevated social standing."

        val gainsChildrenSin = "දරුවන් උපත ලැබීමත් සමඟ නිවසේ සිරියාව සහ ධන වාසනාව දෙගුණ වේ. වැඩිහිටි වියේදී දරුවන්ගේ අසීමිත සැලකිල්ල, ගෞරවය සහ ආර්ථික රැකවරණය නොඅඩුව හිමිවන අතර, දරුවන් සමාජයේ ලබන ඉහළ පිළිගැනීම දෙමාපියන්ට මහත් ආඩම්බරයක් ගෙනදෙයි."
        val gainsChildrenEng = "Progeny arrival catalyzes domestic abundance. Children provide affectionate care and filial support in elder years, bringing immense societal honor to the lineage."

        val childrenFutureSin = "දරුවන් උසස් බුද්ධි මට්ටමකින් යුක්ත වන අතර, වෛද්‍ය, ඉංජිනේරු, තාක්ෂණික හෝ නීති/රාජ්‍ය පරිපාලන ක්ෂේත්‍රවල ඉහළම තලයට පැමිණේ. දරුවෙකුට විදේශීය රටක අධ්‍යාපනය හා රැකියා හිමිකර ගැනීමේ ප්‍රබල භාග්‍යයක් පවතී."
        val childrenFutureEng = "Children will excel in intellectual and professional spheres—especially medicine, engineering, digital technology, or judicial/administrative governance—with bright prospects for overseas achievements."

        val timelineSin = listOf(
            "විවාහයෙන් පසු පළමු වසර 3 තුළ: ඒකාබද්ධ දේපළ මිලදී ගැනීම් සහ ආර්ථික පදනම ශක්තිමත් වීම.",
            "දරුවාට වයස 18 - 25 වන කාලය: දරුවන්ගේ අධ්‍යාපන හා වෘත්තීය ජයග්‍රහණ තුළින් මුළු පවුලටම සමාජ ගෞරවය හිමිවීම."
        )
        val timelineEng = listOf(
            "First 3 years post-marriage: Joint acquisition of prime real estate and firm financial consolidation.",
            "When child reaches ages 18 - 25: Child's scholastic triumphs and career milestones elevate family prestige."
        )

        return FamilyGainsPrediction(
            gainsFromSpouseSinhala = gainsSpouseSin,
            gainsFromSpouseEnglish = gainsSpouseEng,
            gainsFromChildrenSinhala = gainsChildrenSin,
            gainsFromChildrenEnglish = gainsChildrenEng,
            childrenFutureProspectsSinhala = childrenFutureSin,
            childrenFutureProspectsEnglish = childrenFutureEng,
            familyProsperityTimelineSinhala = timelineSin,
            familyProsperityTimelineEnglish = timelineEng
        )
    }

    private fun calculateHealth(
        birthYear: Int,
        lagnaSign: ZodiacSign,
        posMap: Map<Planet, PlanetPosition>,
        houseMap: Map<Int, HouseInfo>
    ): HealthPrediction {
        val sixthHouse = houseMap[6] ?: houseMap.values.first()
        val sixthLord = sixthHouse.lord
        val sun = posMap[Planet.SUN]
        val moon = posMap[Planet.MOON]

        val vitalitySin = "ස්වභාවික ශරීර ප්‍රතිශක්තිය සහ ජීව ශක්තිය යහපත් මට්ටමක පවතී. නිසි ව්‍යායාම, පෝෂණීය ආහාර සහ මානසික සැහැල්ලුව පවත්වා ගන්නේ නම් නිරෝගී දීර්ඝායුෂ පහසුවෙන් භුක්ති විඳිය හැක."
        val vitalityEng = "Robust baseline vitality and constitutional resilience. With disciplined nutrition, routine physical activity, and stress management, lasting wellness is ensured."

        val vulnerableSystemsSin = when (lagnaSign) {
            ZodiacSign.ARIES -> listOf("හිස සහ නළල ආශ්‍රිත වේදනා, ඉරුවාරදය", "රුධිර පීඩනය සහ උෂ්ණාධික රෝග", "ඇස්වල වෙහෙස")
            ZodiacSign.TAURUS -> listOf("උගුර, තයිරොයිඩ් ග්‍රන්ථිය ආශ්‍රිත ආසාදන", "ස්වරාලය සහ බෙල්ලේ වේදනා", "අධික සෙම")
            ZodiacSign.GEMINI -> listOf("ස්නායු පද්ධතියේ ආතතිය සහ කාංසාව", "ශ්වසන පද්ධතිය, පෙනහළු සහ බ්‍රොන්කයිටිස්", "අත් සහ උරහිස් වේදනා")
            ZodiacSign.CANCER -> listOf("ආමාශය සහ ආහාර ජීර්ණ පද්ධතියේ ගැටළු (ගැස්ට්‍රයිටිස්)", "පපුවේ සෙම තැන්පත් වීම", "මානසික ආතතිය නිසා ඇතිවන අජීර්ණ")
            ZodiacSign.LEO -> listOf("හෘදය සහ රුධිර සංසරණ පද්ධතිය", "කොඳු ඇට පෙළ සහ පිටකොන්දේ වේදනා", "අධික උෂ්ණය නිසා ඇතිවන විජලනය")
            ZodiacSign.VIRGO -> listOf("ආන්ත්‍රික ආසාදන, බඩවැලේ තුවාල", "ස්නායු දුර්වලතා සහ ආහාර ජීර්ණය අඩපණ වීම", "සමේ අසාත්මිකතා")
            ZodiacSign.LIBRA -> listOf("වකුගඩු සහ මුත්‍ර පද්ධතිය (Kidneys & Urinary system)", "තුනටිය සහ කොඳු ඇට පෙළේ පහළ කොටස (Lower back)", "රුධිරයේ සීනි සහ සමේ වියළි බව")
            ZodiacSign.SCORPIO -> listOf("ප්‍රජනන පද්ධතිය සහ ගුද මාර්ගික රෝග (අර්ශස්)", "රුධිරයේ විෂ සහිත බව", "සන්ධි වේදනා")
            ZodiacSign.SAGITTARIUS -> listOf("අක්මාව සහ මේද පරිවෘත්තිය (Liver & Fat metabolism)", "උකුල් සහ කලවා ආශ්‍රිත වේදනා", "අධික තරබාරු බව")
            ZodiacSign.CAPRICORN -> listOf("දණහිස්, සන්ධි සහ අස්ථි ආශ්‍රිත වාත රෝග (Arthritis)", "දත් සහ විදුරුමස් දුර්වලතා", "සමේ රෝග")
            ZodiacSign.AQUARIUS -> listOf("පාදයේ වළලුකර සහ පාදයේ නහර ගැටගැසීම (Varicose veins)", "ස්නායු ආතතිය සහ නින්ද නොයාම", "හෘද ස්පන්දන අක්‍රමිකතා")
            ZodiacSign.PISCES -> listOf("පාදයේ යටිපතුල් වේදනා සහ ඉදිමුම්", "ප්‍රතිශක්තිකරණ පද්ධතියේ දුර්වලතා", "අසාත්මිකතා සහ සෙම්ප්‍රතිශ්‍යාව")
        }

        val vulnerableSystemsEng = when (lagnaSign) {
            ZodiacSign.ARIES -> listOf("Headaches, migraine tendencies", "Blood pressure fluctuations", "Eye strain")
            ZodiacSign.TAURUS -> listOf("Throat, vocal cord, and thyroid sensitivities", "Cervical neck strain", "Excess phlegm")
            ZodiacSign.GEMINI -> listOf("Nervous exhaustion and anxiety", "Respiratory passages and bronchitis", "Shoulder tension")
            ZodiacSign.CANCER -> listOf("Stomach acidity, gastritis, and digestion", "Chest congestion", "Emotional stress induced ulcers")
            ZodiacSign.LEO -> listOf("Cardiovascular dynamics and heart health", "Spine and upper back posture", "Heat dehydration")
            ZodiacSign.VIRGO -> listOf("Intestinal flora and bowel sensitivity", "Nervous indigestion", "Skin allergies")
            ZodiacSign.LIBRA -> listOf("Kidneys, urinary tract, and fluid filtration", "Lumbar spine / lower back support", "Blood sugar equilibrium")
            ZodiacSign.SCORPIO -> listOf("Reproductive and excretory tracts (hemorrhoids)", "Blood purity", "Pelvic stiffness")
            ZodiacSign.SAGITTARIUS -> listOf("Liver wellness and lipid balance", "Hips and sciatic nerve comfort", "Weight management")
            ZodiacSign.CAPRICORN -> listOf("Knees, joints, and bone density (rheumatism)", "Teeth and calcium balance", "Dry dermis")
            ZodiacSign.AQUARIUS -> listOf("Ankles, lower legs, and varicose veins", "Circulatory rhythm and sleep hygiene", "Nerve tension")
            ZodiacSign.PISCES -> listOf("Lymphatic drainage and feet sensitivity", "Immune system resilience", "Seasonal mucous allergies")
        }

        val ageWindowsSin = listOf(
            "වයස 45 - 48 අතර කාලය: රුධිර පීඩනය, සීනි මට්ටම සහ කොලෙස්ටරෝල් පිළිබඳ වාර්ෂික පරීක්ෂණ කරගත යුතුය.",
            "වයස 60 - 64 අතර කාලය: සන්ධි, ඇස් පෙනීම සහ හෘද සෞඛ්‍යය පිළිබඳ නිසි වෛද්‍ය අධීක්ෂණය අත්‍යවශ්‍ය වේ."
        )
        val ageWindowsEng = listOf(
            "Ages 45 - 48: Recommended for annual lipid panels, metabolic monitoring, and blood pressure screening.",
            "Ages 60 - 64: Horizon requiring dedicated bone density, cardiovascular, and ocular wellness checkups."
        )

        val ayurvedicSin = "දිනපතා ප්‍රමාණවත් තරම් ජලය පානය කරන්න. උදෑසනට බෙලිමල්, රණවරා හෝ ඉරමුසු වැනි ඖෂධීය කැඳ හෝ පාන වර්ග භාවිතය ඉතා යහපත්ය. රාත්‍රී ආහාරය සැහැල්ලුවෙන් ගෙන වේලාසන නින්දට යන්න."
        val ayurvedicEng = "Hydrate abundantly with pure spring water. Incorporate herbal decoctions (Ranawara, Iramusu, Belimal) and early light dinners to support optimal metabolic recovery."

        return HealthPrediction(
            overallVitalitySinhala = vitalitySin,
            overallVitalityEnglish = vitalityEng,
            vulnerableBodySystemsSinhala = vulnerableSystemsSin,
            vulnerableBodySystemsEnglish = vulnerableSystemsEng,
            sensitiveAgeWindowsSinhala = ageWindowsSin,
            sensitiveAgeWindowsEnglish = ageWindowsEng,
            ayurvedicLifestyleAdviceSinhala = ayurvedicSin,
            ayurvedicLifestyleAdviceEnglish = ayurvedicEng
        )
    }
}
