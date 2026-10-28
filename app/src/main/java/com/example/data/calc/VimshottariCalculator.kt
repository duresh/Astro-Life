package com.example.data.calc

import com.example.data.model.*
import java.util.Calendar
import java.util.Locale

object VimshottariCalculator {

    private val DASHA_ORDER = listOf(
        Planet.KETU, Planet.VENUS, Planet.SUN, Planet.MOON,
        Planet.MARS, Planet.RAHU, Planet.JUPITER, Planet.SATURN, Planet.MERCURY
    )

    /**
     * Calculates all 9 Vimshottari Mahadashas and their respective 9 Bhuktis (81 sub-periods in total)
     * with exact calendar date ranges.
     */
    fun calculateVimshottariPeriods(
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
        moonLongitude: Double
    ): List<DashaPeriod> {
        val spanPerNak = 360.0 / 27.0 // 13.333333 degrees
        val norm = (moonLongitude % 360.0 + 360.0) % 360.0
        val nakIndex = (norm / spanPerNak).toInt().coerceIn(0, 26)
        val posInNak = norm - (nakIndex * spanPerNak)
        val fractionElapsed = posInNak / spanPerNak
        val fractionRemaining = (1.0 - fractionElapsed).coerceIn(0.001, 1.0)

        val birthNak = Nakshatra.entries[nakIndex]
        val startPlanet = birthNak.dashaLord
        val startIndex = DASHA_ORDER.indexOf(startPlanet).coerceAtLeast(0)

        val periods = mutableListOf<DashaPeriod>()
        val birthCal = Calendar.getInstance().apply {
            set(birthYear, birthMonth - 1, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var currentDashaStartCal = birthCal.clone() as Calendar
        val nowCal = Calendar.getInstance()

        for (i in 0 until 9) {
            val majorPlanet = DASHA_ORDER[(startIndex + i) % 9]
            val durationYears = if (i == 0) {
                majorPlanet.dashaYears * fractionRemaining
            } else {
                majorPlanet.dashaYears.toDouble()
            }

            val dashaStartYear = currentDashaStartCal.get(Calendar.YEAR)
            val dashaStartMonth = currentDashaStartCal.get(Calendar.MONTH) + 1
            val dashaStartDay = currentDashaStartCal.get(Calendar.DAY_OF_MONTH)
            val dashaStartFormatted = String.format(Locale.US, "%04d-%02d-%02d", dashaStartYear, dashaStartMonth, dashaStartDay)

            val daysToAdd = (durationYears * 365.25).toInt()
            val dashaEndCal = (currentDashaStartCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, daysToAdd)
            }

            val dashaEndYear = dashaEndCal.get(Calendar.YEAR)
            val dashaEndMonth = dashaEndCal.get(Calendar.MONTH) + 1
            val dashaEndDay = dashaEndCal.get(Calendar.DAY_OF_MONTH)
            val dashaEndFormatted = String.format(Locale.US, "%04d-%02d-%02d", dashaEndYear, dashaEndMonth, dashaEndDay)

            val isCurrentDasha = nowCal.timeInMillis >= currentDashaStartCal.timeInMillis &&
                    nowCal.timeInMillis < dashaEndCal.timeInMillis

            // Calculate all 9 Bhuktis for this Mahadasha
            val bhuktis = calculateBhuktisForDasha(
                majorPlanet = majorPlanet,
                dashaIndex = i,
                fractionRemaining = fractionRemaining,
                dashaStartCal = currentDashaStartCal,
                dashaEndCal = dashaEndCal,
                birthCal = birthCal,
                nowCal = nowCal
            )

            val (foreSin, foreEng) = getDashaForecast(majorPlanet)

            periods.add(
                DashaPeriod(
                    planet = majorPlanet,
                    startYear = dashaStartYear,
                    endYear = dashaEndYear,
                    startDateStr = dashaStartFormatted,
                    endDateStr = dashaEndFormatted,
                    durationYears = durationYears,
                    isCurrent = isCurrentDasha,
                    forecastSinhala = foreSin,
                    forecastEnglish = foreEng,
                    bhuktis = bhuktis
                )
            )

            currentDashaStartCal = dashaEndCal
        }

        return periods
    }

    /**
     * Calculates the 9 Bhuktis (sub-periods) for a given Mahadasha.
     */
    private fun calculateBhuktisForDasha(
        majorPlanet: Planet,
        dashaIndex: Int,
        fractionRemaining: Double,
        dashaStartCal: Calendar,
        dashaEndCal: Calendar,
        birthCal: Calendar,
        nowCal: Calendar
    ): List<BhuktiPeriod> {
        val bhuktis = mutableListOf<BhuktiPeriod>()
        val majorIndexInOrder = DASHA_ORDER.indexOf(majorPlanet).coerceAtLeast(0)

        // For the first Mahadasha at birth, calculate nominal start to accurately position sub-periods
        val isFirstDasha = (dashaIndex == 0)
        val nominalDashaStartCal = if (isFirstDasha) {
            val elapsedDashaYears = majorPlanet.dashaYears * (1.0 - fractionRemaining)
            val elapsedDays = (elapsedDashaYears * 365.25).toInt()
            (birthCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -elapsedDays) }
        } else {
            dashaStartCal.clone() as Calendar
        }

        var runningBhuktiCal = nominalDashaStartCal.clone() as Calendar

        for (k in 0 until 9) {
            val subPlanet = DASHA_ORDER[(majorIndexInOrder + k) % 9]
            val durationMonths = (majorPlanet.dashaYears * subPlanet.dashaYears) / 10.0
            val fullBhuktiDays = ((majorPlanet.dashaYears * subPlanet.dashaYears * 365.25) / 120.0).toInt()

            val nominalStart = runningBhuktiCal.clone() as Calendar
            val nominalEnd = (runningBhuktiCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, fullBhuktiDays)
            }

            // If this is the birth dasha and the sub-period ended before birth, skip it
            if (isFirstDasha && nominalEnd.timeInMillis <= birthCal.timeInMillis) {
                runningBhuktiCal = nominalEnd
                continue
            }

            // Effective start is birth date if it began before birth
            val effectiveStart = if (isFirstDasha && nominalStart.timeInMillis < birthCal.timeInMillis) {
                birthCal.clone() as Calendar
            } else {
                nominalStart
            }

            val bStartYear = effectiveStart.get(Calendar.YEAR)
            val bStartMonth = effectiveStart.get(Calendar.MONTH) + 1
            val bStartDay = effectiveStart.get(Calendar.DAY_OF_MONTH)
            val bStartFormatted = String.format(Locale.US, "%04d-%02d-%02d", bStartYear, bStartMonth, bStartDay)

            val bEndYear = nominalEnd.get(Calendar.YEAR)
            val bEndMonth = nominalEnd.get(Calendar.MONTH) + 1
            val bEndDay = nominalEnd.get(Calendar.DAY_OF_MONTH)
            val bEndFormatted = String.format(Locale.US, "%04d-%02d-%02d", bEndYear, bEndMonth, bEndDay)

            val isCurrentBhukti = nowCal.timeInMillis >= effectiveStart.timeInMillis &&
                    nowCal.timeInMillis < nominalEnd.timeInMillis

            val (effectSin, effectEng) = getBhuktiEffect(majorPlanet, subPlanet)
            val isChallenging = checkIsChallengingBhukti(majorPlanet, subPlanet)

            bhuktis.add(
                BhuktiPeriod(
                    majorPlanet = majorPlanet,
                    subPlanet = subPlanet,
                    startDateStr = bStartFormatted,
                    endDateStr = bEndFormatted,
                    startYear = bStartYear,
                    endYear = bEndYear,
                    durationMonths = durationMonths,
                    isCurrent = isCurrentBhukti,
                    effectSinhala = effectSin,
                    effectEnglish = effectEng,
                    isChallenging = isChallenging
                )
            )

            runningBhuktiCal = nominalEnd
        }

        return bhuktis
    }

    /**
     * Complete Vimshottari life analysis engine including Current periods, Inauspicious times,
     * Dosha analysis, Longevity & Maraka, Past Life, and Prosperity guidance.
     */
    fun calculateFullAnalysis(
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
        lagnaSign: ZodiacSign,
        moonSign: ZodiacSign,
        moonNakshatra: Nakshatra,
        positions: List<PlanetPosition>,
        houses: List<HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): VimshottariDashaAnalysis {
        val currentDashaBhukti = calculateCurrentDashaBhukti(dashaPeriods)
        val inauspiciousPeriods = calculateInauspiciousPeriods(birthYear, lagnaSign, moonSign, houses, dashaPeriods)
        val lifeDoshas = calculateLifeDoshas(lagnaSign, moonSign, positions, houses)
        val longevityMaraka = calculateLongevityAndMaraka(birthYear, lagnaSign, positions, houses, dashaPeriods)
        val pastLifeKarma = calculatePastLifeKarma(lagnaSign, moonSign, positions, houses)
        val prosperityGuidance = calculateProsperityGuidance(lagnaSign, houses)

        return VimshottariDashaAnalysis(
            currentDashaBhukti = currentDashaBhukti,
            inauspiciousPeriods = inauspiciousPeriods,
            lifeDoshas = lifeDoshas,
            longevityMaraka = longevityMaraka,
            pastLifeKarma = pastLifeKarma,
            prosperityGuidance = prosperityGuidance
        )
    }

    private fun calculateCurrentDashaBhukti(dashaPeriods: List<DashaPeriod>): CurrentDashaBhuktiInfo? {
        val now = Calendar.getInstance().timeInMillis
        val activeDasha = dashaPeriods.find { it.isCurrent } ?: dashaPeriods.firstOrNull() ?: return null
        val activeBhukti = activeDasha.bhuktis.find { it.isCurrent }
            ?: activeDasha.bhuktis.firstOrNull()
            ?: BhuktiPeriod(
                majorPlanet = activeDasha.planet,
                subPlanet = activeDasha.planet,
                startDateStr = activeDasha.startDateStr,
                endDateStr = activeDasha.endDateStr,
                startYear = activeDasha.startYear,
                endYear = activeDasha.endYear,
                durationMonths = 12.0,
                isCurrent = true,
                effectSinhala = activeDasha.forecastSinhala,
                effectEnglish = activeDasha.forecastEnglish,
                isChallenging = false
            )

        // Calculate progress percentage of active Bhukti
        val startCal = parseDateToMillis(activeBhukti.startDateStr)
        val endCal = parseDateToMillis(activeBhukti.endDateStr)
        val totalSpan = (endCal - startCal).coerceAtLeast(1L)
        val elapsed = (now - startCal).coerceAtLeast(0L)
        val percentage = ((elapsed.toDouble() / totalSpan.toDouble()) * 100.0).toFloat().coerceIn(0f, 100f)

        val remainingMillis = (endCal - now).coerceAtLeast(0L)
        val remainingDays = (remainingMillis / (1000 * 60 * 60 * 24)).toInt()

        val influenceSin = "${activeDasha.planet.sinhalaName} මහ දශාවේ ${activeBhukti.subPlanet.sinhalaName} අන්තර් දශාව (භුක්තිය) මේ වන විට සක්‍රියව පවතී. " +
                activeBhukti.effectSinhala
        val influenceEng = "${activeDasha.planet.englishName} Mahadasha with ${activeBhukti.subPlanet.englishName} Antardasha (Bhukti) is currently active. " +
                activeBhukti.effectEnglish

        val guidanceSin = when {
            activeBhukti.isChallenging -> "මෙම කාල පරිච්ඡේදය සුළු බාධක සහ අභියෝග සහිත විය හැකි බැවින්, හදිසි තීරණ ගැනීමෙන් වැළකී සන්සුන්ව කටයුතු කරන්න. අදාළ ග්‍රහ ශාන්තිකර්ම සහ බෝධි පූජා පැවැත්වීම ඉතා සුබදායකය."
            else -> "මෙම කාලය නව සැලසුම් ක්‍රියාත්මක කිරීමට සහ වෘත්තීය මෙන්ම පෞද්ගලික අරමුණු සාක්ෂාත් කර ගැනීමට ඉතා හිතකරය. උත්සාහය සහ කැපවීම උපරිමයෙන් යොදවන්න."
        }
        val guidanceEng = when {
            activeBhukti.isChallenging -> "This sub-period calls for patience and mindfulness. Avoid hasty commitments and undertake spiritual grounding and planetary pacifications."
            else -> "Highly productive period for launching important endeavors. Channel your focus toward long-term ambitions and constructive connections."
        }

        return CurrentDashaBhuktiInfo(
            majorPlanet = activeDasha.planet,
            subPlanet = activeBhukti.subPlanet,
            majorStartDateStr = activeDasha.startDateStr,
            majorEndDateStr = activeDasha.endDateStr,
            bhuktiStartDateStr = activeBhukti.startDateStr,
            bhuktiEndDateStr = activeBhukti.endDateStr,
            elapsedPercentage = percentage,
            remainingDays = remainingDays,
            overallInfluenceSinhala = influenceSin,
            overallInfluenceEnglish = influenceEng,
            guidanceSinhala = guidanceSin,
            guidanceEnglish = guidanceEng
        )
    }

    private fun calculateInauspiciousPeriods(
        birthYear: Int,
        lagnaSign: ZodiacSign,
        moonSign: ZodiacSign,
        houses: List<HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): List<InauspiciousPeriod> {
        val list = mutableListOf<InauspiciousPeriod>()

        // 1. Sade Sati / Janma Erashtaka (Saturn transiting 12, 1, 2 from Moon)
        val cycleYears = listOf(birthYear + 26, birthYear + 56, birthYear + 85)
        for ((idx, startYr) in cycleYears.withIndex()) {
            if (startYr > birthYear + 95) continue
            val endYr = startYr + 7
            val ageStart = startYr - birthYear
            val ageEnd = endYr - birthYear
            val cycleName = if (idx == 0) "පළමු / දෙවන වටය" else "${idx + 1} වන වටය"

            list.add(
                InauspiciousPeriod(
                    titleSinhala = "ජන්ම ඒරාෂ්ටකය (ශනි ඒරාෂ්ටක $cycleName)",
                    titleEnglish = "Janma Erashtaka (Sade Sati Cycle ${idx + 1})",
                    categorySinhala = "ශනි ඒරාෂ්ටක අපල",
                    categoryEnglish = "Saturn Sade Sati",
                    dateRangeStr = "$startYr-06 සිට $endYr-11 දක්වා",
                    ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                    severityLevel = "HIGH",
                    warningDescriptionSinhala = "සෙනසුරු සඳු මතින් සහ දෙපසින් ගමන් ගන්නා වසර 7 1/2 ක කාලයයි. මානසික අවපීඩනය, අනපේක්ෂිත වියදම්, ළඟම ඥාතීන්ගේ වියෝව හෝ සෞඛ්‍ය පිරිහීම සහ රැකියා ස්ථානයේ අනපේක්ෂිත අභියෝග මතු විය හැක.",
                    warningDescriptionEnglish = "7.5-year transit of Saturn over natal Moon. Challenges include emotional turbulence, unexpected financial drain, domestic friction, and career pivots.",
                    precautionsSinhala = "අවදානම් සහිත විශාල මූල්‍ය ආයෝජනවලින් සහ ඇපවීම්වලින් වළකින්න. රිය පැදවීමේදී විමසිලිමත් වන්න. ඉවසීමෙන් කටයුතු කරන්න.",
                    precautionsEnglish = "Avoid risky financial speculation, refrain from acting as guarantor, exercise road safety, cultivate inner calm.",
                    remediesSinhala = "සෙනසුරාදා දිනවල බෝධි පූජා පැවැත්වීම, එළඟිතෙල් පහන් 9ක් දැල්වීම, නීලමණ්ඩල යන්ත්‍රය පැළඳීම, කළු තල හා එළවළු මිශ්‍ර බත් කපුටන්ට දන් දීම, ආබාධිත පුද්ගලයින්ට උපකාර කිරීම.",
                    remediesEnglish = "Saturday Bodhi Pooja, lighting ghee lamps, wearing Neelamandala Yantra, feeding crows sesame rice, aiding the disabled."
                )
            )
        }

        // 2. Ashtama Erashtaka (Saturn transiting 8th from Moon)
        val ashtamaStart1 = birthYear + 18
        val ashtamaStart2 = birthYear + 48
        val ashtamaStart3 = birthYear + 77
        listOf(ashtamaStart1, ashtamaStart2, ashtamaStart3).filter { it <= birthYear + 90 }.forEachIndexed { idx, startYr ->
            val endYr = startYr + 2
            val ageStart = startYr - birthYear
            val ageEnd = endYr - birthYear
            list.add(
                InauspiciousPeriod(
                    titleSinhala = "අෂ්ටම ඒරාෂ්ටකය (${idx + 1} වන වටය)",
                    titleEnglish = "Ashtama Erashtaka (Cycle ${idx + 1})",
                    categorySinhala = "අෂ්ටම ඒරාෂ්ටකය",
                    categoryEnglish = "8th House Saturn Transit",
                    dateRangeStr = "$startYr-03 සිට $endYr-09 දක්වා",
                    ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                    severityLevel = "HIGH",
                    warningDescriptionSinhala = "සෙනසුරු 8 වැන්නේ ගමන් ගන්නා වසර 2 1/2 ක කාලයයි. හදිසි ශාරීරික අනතුරු, නිදන්ගත රෝග මතුවීම, නීතිමය ගැටළු සහ සතුරු කරදර ඉස්මතු විය හැක.",
                    warningDescriptionEnglish = "2.5-year transit of Saturn through the 8th house. Risk of health flare-ups, unexpected accidents, litigations, and hidden adversaries.",
                    precautionsSinhala = "දුර බැහැර අනතුරුදායක ගමන් බිමන් සීමා කරන්න. සෞඛ්‍ය පරීක්ෂණ නිසි වේලාවට කරගන්න. රහස් ගනුදෙනුවලින් වළකින්න.",
                    precautionsEnglish = "Avoid perilous journeys, conduct regular health checkups, remain scrupulously transparent in financial affairs.",
                    remediesSinhala = "මහා මෘත්‍යුඤ්ජය මන්ත්‍රය දිනපතා ජප කිරීම, අභිසම්භිධාන පිරිත ශ්‍රවණය, අභයදාන පින්කම් (ගවයින් නිදහස් කිරීම), සෙනසුරාදා නිල් මල් පූජා.",
                    remediesEnglish = "Recitation of Maha Mrityunjaya mantra, Abhisambhidhana protective chants, animal sanctuary liberations, offering blue flowers."
                )
            )
        }

        // 3. Kantaka Erashtaka (Saturn transiting 4th / 10th from Lagna)
        val kantakaYr1 = birthYear + 10
        val kantakaYr2 = birthYear + 39
        val kantakaYr3 = birthYear + 69
        listOf(kantakaYr1, kantakaYr2, kantakaYr3).filter { it <= birthYear + 90 }.forEachIndexed { idx, startYr ->
            val endYr = startYr + 2
            val ageStart = startYr - birthYear
            val ageEnd = endYr - birthYear
            list.add(
                InauspiciousPeriod(
                    titleSinhala = "කණ්ඨක ඒරාෂ්ටකය (${idx + 1} වන වටය)",
                    titleEnglish = "Kantaka Erashtaka (Cycle ${idx + 1})",
                    categorySinhala = "කණ්ඨක ඒරාෂ්ටකය",
                    categoryEnglish = "Kantaka Saturn Transit",
                    dateRangeStr = "$startYr-08 සිට $endYr-12 දක්වා",
                    ageWindowStr = "වයස $ageStart - $ageEnd අතර කාලය",
                    severityLevel = "MEDIUM",
                    warningDescriptionSinhala = "4 වැන්න හෝ 10 වැන්න හරහා සෙනසුරු ගමන් කරන කාලයයි. ගෘහ ජීවිතයේ අසමගිය, වාසස්ථාන ගැටළු, මවගේ සෞඛ්‍ය පිරිහීම හෝ රැකියාවේ ක්ෂණික බාධක ඇතිවිය හැක.",
                    warningDescriptionEnglish = "Transit of Saturn through 4th or 10th houses. Domestic instability, maternal health concerns, or occupational frictions.",
                    precautionsSinhala = "නිවාස සහ දේපළ මිලදී ගැනීමේදී ලියකියවිලි පරීක්ෂා කරගන්න. පවුලේ සාමාජිකයන් සමඟ සුහදව කටයුතු කරන්න.",
                    precautionsEnglish = "Verify property titles thoroughly before purchasing, maintain empathy with family members, avoid workplace disputes.",
                    remediesSinhala = "විෂ්ණු දෙවියන්ට සහ කතරගම දෙවියන්ට භාරහාර වීම, සඳුන් සුවඳ දුම් ඇල්ලීම, මවුපියන්ට ආවතේව කිරීම.",
                    remediesEnglish = "Prayers to Lord Vishnu and Kataragama, burning sandalwood incense, caring for maternal elders."
                )
            )
        }

        // 4. Maraka & Dusthana Dasha/Bhukti periods
        val secondLord = houses.find { it.houseNumber == 2 }?.lord ?: Planet.MARS
        val seventhLord = houses.find { it.houseNumber == 7 }?.lord ?: Planet.MARS
        val eighthLord = houses.find { it.houseNumber == 8 }?.lord ?: Planet.VENUS

        for (dasha in dashaPeriods) {
            val isMarakaMajor = (dasha.planet == secondLord || dasha.planet == seventhLord || dasha.planet == eighthLord || dasha.planet == Planet.RAHU)
            for (bhukti in dasha.bhuktis) {
                val isMarakaSub = (bhukti.subPlanet == secondLord || bhukti.subPlanet == seventhLord || bhukti.subPlanet == eighthLord || bhukti.subPlanet == Planet.KETU)
                if (isMarakaMajor && isMarakaSub && dasha.planet != bhukti.subPlanet) {
                    val ageAtPeriod = bhukti.startYear - birthYear
                    if (ageAtPeriod in 5..90) {
                        list.add(
                            InauspiciousPeriod(
                                titleSinhala = "මාරක සහ දුෂ්ටස්ථාන අපල කාලය (${dasha.planet.sinhalaName} / ${bhukti.subPlanet.sinhalaName})",
                                titleEnglish = "Maraka & Dusthana Period (${dasha.planet.englishName} / ${bhukti.subPlanet.englishName})",
                                categorySinhala = "මාරක / බාධක ග්‍රහ අපල",
                                categoryEnglish = "Maraka Planetary Period",
                                dateRangeStr = "${bhukti.startDateStr} සිට ${bhukti.endDateStr} දක්වා",
                                ageWindowStr = "වයස $ageAtPeriod අවට කාලය",
                                severityLevel = "HIGH",
                                warningDescriptionSinhala = "2, 7, 8 යන මාරක හා දුෂ්ටස්ථානාධිපති ග්‍රහයන්ගේ අන්තර් දශා සබඳතාවයයි. දැඩි සෞඛ්‍ය ගැටළු, හදිසි ශල්‍යකර්ම, අනතුරු සහ සතුරු කරදර පිළිබඳ වැඩි අවධානයක් අවශ්‍ය වේ.",
                                warningDescriptionEnglish = "Critical sub-period involving Maraka (2nd/7th) and Dusthana (8th) lords. Heightened vulnerability to acute health issues, surgeries, or disputes.",
                                precautionsSinhala = "නොසැලකිලිමත් ලෙස ජීවත් වීමෙන් වළකින්න. නිසි වෛද්‍ය ප්‍රතිකාර ලබාගන්න. අධික වේගයෙන් රිය පැදවීමෙන් වළකින්න.",
                                precautionsEnglish = "Prioritize preventive health measures, adhere strictly to medical advice, avoid reckless speed on roads.",
                                remediesSinhala = "මහා මෘත්‍යුඤ්ජය මන්ත්‍රය 108 වරක් ජප කිරීම, රතන සූත්‍ර දේශනාව, ලේ දන්දීමේ පින්කම්වලට දායක වීම, බෝධි පූජා පැවැත්වීම.",
                                remediesEnglish = "Reciting Maha Mrityunjaya mantra 108 times, listening to Ratana Sutta, participating in blood donation drives, Bodhi worship."
                            )
                        )
                    }
                }
            }
        }

        return list.sortedBy { it.dateRangeStr }
    }

    private fun calculateLifeDoshas(
        lagnaSign: ZodiacSign,
        moonSign: ZodiacSign,
        positions: List<PlanetPosition>,
        houses: List<HouseInfo>
    ): List<LifeDoshaInfo> {
        val list = mutableListOf<LifeDoshaInfo>()
        val posMap = positions.associateBy { it.planet }

        // 1. Kuja Dosha (Mars in 1, 2, 4, 7, 8, 12)
        val mars = posMap[Planet.MARS]
        val marsHouse = mars?.house ?: 1
        val hasKuja = listOf(1, 2, 4, 7, 8, 12).contains(marsHouse)
        val isCancelled = hasKuja && (
                mars?.sign == ZodiacSign.ARIES || mars?.sign == ZodiacSign.SCORPIO ||
                        mars?.sign == ZodiacSign.CAPRICORN || mars?.sign == ZodiacSign.LEO ||
                        posMap[Planet.JUPITER]?.house == marsHouse ||
                        posMap[Planet.JUPITER]?.house == (marsHouse + 6) % 12 + 1
                )

        list.add(
            LifeDoshaInfo(
                doshaNameSinhala = "කුජ දෝෂය (භෞම දෝෂය)",
                doshaNameEnglish = "Kuja Dosha (Manglik Dosha)",
                isPresent = hasKuja,
                intensityLevel = when {
                    !hasKuja -> "නැත (Absent)"
                    isCancelled -> "භංග වී ඇත (Cancelled / Ineffective)"
                    listOf(7, 8).contains(marsHouse) -> "ප්‍රබල (Strong)"
                    else -> "මධ්‍යම (Moderate)"
                },
                affectedLifeAreasSinhala = "විවාහය, යුග දිවියේ සාමය, කේන්ති යාම සහ හදිසි තීරණ ගැනීම.",
                affectedLifeAreasEnglish = "Marital harmony, domestic balance, short temperament, hasty impulses.",
                criticalTimeRangeSinhala = "වයස අවුරුදු 28 ට පෙර කාලය සහ කුජ මහ දශා / අතුරු දශා කාලවලදී.",
                criticalTimeRangeEnglish = "Most pronounced before age 28 and during Mars Dasha/Bhukti periods.",
                descriptionSinhala = if (hasKuja) {
                    if (isCancelled) {
                        "කුජ ග්‍රහයා $marsHouse වැන්නේ තැන්පත්ව ඇති නමුත්, ස්වක්ෂේත්‍ර/උච්ච/ගුරු දෘෂ්ටි බලය හේතුවෙන් කුජ දෝෂය සම්පූර්ණයෙන්ම භංග වී සුබ ඵල බවට පත්ව ඇත."
                    } else {
                        "කුජ ග්‍රහයා $marsHouse වැන්නේ පිහිටීම නිසා යුග දිවියේ සුළු මතභේද සහ කේන්ති යාම ඇතිවිය හැක. සහකරු/සහකාරියගේ කේන්ද්‍රයද කුජ දෝෂ සහිත වීම ඉතා සුබදායකය."
                    }
                } else {
                    "කේන්ද්‍රයේ කුජ දෝෂයක් නොමැත. විවාහ ජීවිතයේ සාමය සහ අන්‍යෝන්‍ය අවබෝධය යහපත්ව පවතී."
                },
                descriptionEnglish = if (hasKuja) {
                    if (isCancelled) {
                        "Mars is in House $marsHouse, but due to benefic aspects and favorable sign placement, the negative Manglik effect is nullified into strength."
                    } else {
                        "Mars resides in House $marsHouse. May create passionate friction or strong will in partnerships. Matching with a compatible Manglik chart is favorable."
                    }
                } else {
                    "No Kuja Dosha present. Harmonious and stable partnership dynamics."
                },
                remediesSinhala = "කුජ ශාන්තිකර්ම, අඟහරුවාදා දිනවල රතු මල් පූජා කිරීම, කතරගම දෙවියන්ට භාරවීම, මංගල කටයුතුවලදී නිසි පොරොන්දම් ගැලපීම.",
                remediesEnglish = "Mars pacification rituals, offering red flowers on Tuesdays, vows to deity Skanda/Kataragama, compatible horoscope matching."
            )
        )

        // 2. Kalsarpa Dosha (All planets hemmed between Rahu and Ketu)
        val rahu = posMap[Planet.RAHU]
        val ketu = posMap[Planet.KETU]
        val otherPlanets = listOfNotNull(
            posMap[Planet.SUN], posMap[Planet.MOON], posMap[Planet.MARS],
            posMap[Planet.MERCURY], posMap[Planet.JUPITER], posMap[Planet.VENUS], posMap[Planet.SATURN]
        )

        val isKalsarpa = if (rahu != null && ketu != null && otherPlanets.size == 7) {
            val rLong = rahu.longitude
            val kLong = ketu.longitude
            val (minL, maxL) = if (rLong < kLong) Pair(rLong, kLong) else Pair(kLong, rLong)
            val insideCount = otherPlanets.count { it.longitude in minL..maxL }
            insideCount == 7 || insideCount == 0
        } else false

        list.add(
            LifeDoshaInfo(
                doshaNameSinhala = "කාලසර්ප දෝෂය",
                doshaNameEnglish = "Kala Sarpa Dosha",
                isPresent = isKalsarpa,
                intensityLevel = if (isKalsarpa) "ප්‍රබල (Active)" else "නැත (Absent)",
                affectedLifeAreasSinhala = "ජීවිතයේ මුල් භාගයේ බාධක, වෙහෙසට සරිලන ප්‍රතිඵල ප්‍රමාද වීම, මානසික චංචලභාවය.",
                affectedLifeAreasEnglish = "Early life delays, unrewarded struggles before mid-30s, anxiety, sudden reversals.",
                criticalTimeRangeSinhala = "වයස අවුරුදු 33-36 දක්වා කාලය සහ රාහු-කේතු දශා කාලවලදී.",
                criticalTimeRangeEnglish = "Until ages 33-36 and active Rahu-Ketu periods.",
                descriptionSinhala = if (isKalsarpa) {
                    "සියලුම ග්‍රහයින් රාහු සහ කේතු යන ඡායා ග්‍රහයින් දෙදෙනා අතර සිරවී පිහිටීම නිසා කාලසර්ප දෝෂය හටගනී. මුල් අවධියේ අරගල පැවතියද, වයස 35 න් පසු විශාල දියුණුවක් හා ජයග්‍රහණ හිමිවේ."
                } else {
                    "කේන්ද්‍රයේ කාලසර්ප දෝෂයක් නොමැත. සියලු ග්‍රහයින් නිදහසේ පිහිටා ඇති බැවින් ස්වාධීන දියුණුවක් ලබයි."
                },
                descriptionEnglish = if (isKalsarpa) {
                    "Formed when all major planets are flanked between the lunar nodes Rahu and Ketu. Characterized by intense early struggles yielding immense success after age 35."
                } else {
                    "No Kala Sarpa Dosha present. Planets operate freely with open pathways."
                },
                remediesSinhala = "ශිව වන්දනාව, නාග පූජා, සර්පයින්ට කිසිදු හානියක් නොකිරීම, රාහු-කේතු ග්‍රහ ශාන්තිකර්ම, රිදී නාග රූපයක් ජලයේ පා කිරීම.",
                remediesEnglish = "Worship of Lord Shiva, offering milk at sacred nagas, protecting reptiles, pacification for Rahu and Ketu."
            )
        )

        // 3. Pitru Dosha (Affliction of Sun or 9th house)
        val sun = posMap[Planet.SUN]
        val ninthHouse = houses.find { it.houseNumber == 9 }
        val hasPitru = (sun != null && (sun.house == posMap[Planet.RAHU]?.house || sun.house == posMap[Planet.KETU]?.house || sun.house == posMap[Planet.SATURN]?.house)) ||
                (ninthHouse != null && ninthHouse.planetsInside.any { it.planet == Planet.RAHU || it.planet == Planet.KETU })

        list.add(
            LifeDoshaInfo(
                doshaNameSinhala = "පීතෘ දෝෂය",
                doshaNameEnglish = "Pitru Dosha",
                isPresent = hasPitru,
                intensityLevel = if (hasPitru) "මධ්‍යම (Moderate)" else "නැත (Absent)",
                affectedLifeAreasSinhala = "පිය පාර්ශවයේ සබඳතා, දරුපල ප්‍රමාදය, පරම්පරාවේ දියුණුවට එල්ලවන බාධක.",
                affectedLifeAreasEnglish = "Paternal connections, delays in lineage prosperity, spiritual impediments.",
                criticalTimeRangeSinhala = "සූර්ය හෝ රාහු මහ දශා කාලවලදී සහ පිතෘ කර්ම අතපසු වන අවස්ථාවලදී.",
                criticalTimeRangeEnglish = "During Sun or Rahu cycles and when ancestral rites are neglected.",
                descriptionSinhala = if (hasPitru) {
                    "සූර්යයා හෝ 9 වන පීතෘ භාවය අසුභ ග්‍රහ දෘෂ්ටියට ලක්වීම නිසා පීතෘ දෝෂය මතුවේ. මියගිය මුතුන් මිත්තන්ට පින් අනුමෝදන් කිරීම තුළින් මෙය සම්පූර්ණයෙන්ම සුවපත් වේ."
                } else {
                    "කේන්ද්‍රයේ පීතෘ දෝෂයක් නොමැත. මුතුන් මිත්තන්ගේ ආශිර්වාදය හා පිය පාර්ශවයේ ශක්තිය නොඅඩුව හිමිවේ."
                },
                descriptionEnglish = if (hasPitru) {
                    "Sun or 9th house under nodal/malefic influence indicates unfulfilled ancestral obligations. Dedicating merits to departed ancestors cleanses this completely."
                } else {
                    "No Pitru Dosha present. Strong ancestral blessings and paternal support."
                },
                remediesSinhala = "මියගිය ඥාතීන්ට නිතර පින් අනුමෝදන් කිරීම, දෙමාපියන්ට ගෞරවයෙන් සැලකීම, ඉරිදා දිනවල සූර්ය නමස්කාරය සහ ආදිත්‍ය හෘදය ස්තෝත්‍රය කියවීම.",
                remediesEnglish = "Dedicating merits to departed ancestors, honoring parents, chanting Aditya Hridaya Stotra on Sundays."
            )
        )

        // 4. Guru Chandal Dosha (Jupiter conjunct Rahu)
        val jupiter = posMap[Planet.JUPITER]
        val hasGuruChandal = jupiter != null && rahu != null && jupiter.house == rahu.house

        list.add(
            LifeDoshaInfo(
                doshaNameSinhala = "ගුරු චණ්ඩාල දෝෂය",
                doshaNameEnglish = "Guru Chandal Dosha",
                isPresent = hasGuruChandal,
                intensityLevel = if (hasGuruChandal) "ප්‍රබල (Active)" else "නැත (Absent)",
                affectedLifeAreasSinhala = "උසස් අධ්‍යාපනය, ආගමික විශ්වාස, වැරදි උපදෙස් පිළිපැදීම නිසා සිදුවන මූල්‍ය පාඩු.",
                affectedLifeAreasEnglish = "Higher learning, discernment, ethical conflicts, misjudged financial gambles.",
                criticalTimeRangeSinhala = "ගුරු හෝ රාහු දශා සහ අතුරු දශා කාලවලදී.",
                criticalTimeRangeEnglish = "During Jupiter or Rahu major/minor cycles.",
                descriptionSinhala = if (hasGuruChandal) {
                    "බ්‍රහස්පති සහ රාහු එකම භාවයේ සංයෝග වීමෙන් හටගනී. සාම්ප්‍රදායික රාමුවෙන් බැහැරව සිතන අතර, ආගමික හා මූල්‍ය කටයුතුවලදී දැඩි විනයක් පවත්වා ගත යුතුය."
                } else {
                    "කේන්ද්‍රයේ ගුරු චණ්ඩාල දෝෂයක් නොමැත. බුද්ධිමත් බව, ආගමික ශ්‍රද්ධාව සහ නිවැරදි තීන්දු තීරණ ගැනීමේ හැකියාව ඉහළයි."
                },
                descriptionEnglish = if (hasGuruChandal) {
                    "Conjunction of Jupiter and Rahu fosters unconventional wisdom, skepticism, and occasional financial misjudgments if unguided."
                } else {
                    "No Guru Chandal Dosha present. Sound ethical judgment and deep scholastic inclinations."
                },
                remediesSinhala = "බ්‍රහස්පතින්දා දිනවල කහ මල් පූජා කිරීම, ගුරු දෙගුරුන්ට සැලකීම, විෂ්ණු සහ බ්‍රහස්පති ස්තෝත්‍ර ගායනා කිරීම.",
                remediesEnglish = "Offering yellow flowers on Thursdays, honoring teachers and mentors, chanting Brihaspati mantras."
            )
        )

        // 5. Vish Dosha (Moon conjunct or aspected by Saturn)
        val moon = posMap[Planet.MOON]
        val saturn = posMap[Planet.SATURN]
        val hasVishDosha = moon != null && saturn != null && (moon.house == saturn.house || (saturn.house - moon.house + 12) % 12 == 6)

        list.add(
            LifeDoshaInfo(
                doshaNameSinhala = "විෂ දෝෂය (ශනි-චන්ද්‍ර සංයෝගය)",
                doshaNameEnglish = "Vish Dosha (Saturn-Moon Yoga)",
                isPresent = hasVishDosha,
                intensityLevel = if (hasVishDosha) "සක්‍රිය (Active)" else "නැත (Absent)",
                affectedLifeAreasSinhala = "මානසික ඒකාග්‍රතාවය, තනිකම දැනීම, අනවශ්‍ය බිය සහ කාංසාව.",
                affectedLifeAreasEnglish = "Mental peace, emotional vulnerability, feelings of isolation, overthinking.",
                criticalTimeRangeSinhala = "ශනි හෝ චන්ද්‍ර දශා කාලවලදී සහ අමාවක/පුරපසළොස්වක ආසන්න දිනවලදී.",
                criticalTimeRangeEnglish = "During Saturn or Moon periods and near new/full moon phases.",
                descriptionSinhala = if (hasVishDosha) {
                    "චන්ද්‍රයා සහ සෙනසුරු එකට පිහිටීම හෝ සෘජු දෘෂ්ටි හෙලීම නිසා මනසට නිතර දුක සහ තනිකම දැනිය හැක. භාවනා ක්‍රම තුළින් මනස ප්‍රබෝධමත් කරගත යුතුය."
                } else {
                    "කේන්ද්‍රයේ විෂ දෝෂයක් නොමැත. මානසික ශක්තිය, ප්‍රීතිමත් මනෝභාවය සහ ජීවිතයට මුහුණදීමේ ධෛර්යය උසස් මට්ටමක පවතී."
                },
                descriptionEnglish = if (hasVishDosha) {
                    "Saturn's contact with the Moon can produce melancholic reflections and introspection. Mindful meditation transforms this into spiritual depth."
                } else {
                    "No Vish Dosha present. Balanced emotional well-being and psychological resilience."
                },
                remediesSinhala = "සඳුදා දිනවල කිරි ආහාර පූජා කිරීම, මෛත්‍රී භාවනාව ප්‍රගුණ කිරීම, ඊශ්වර දෙවියන්ට කිරි අභිෂේකය, සිත සන්සුන් කරන ආගමික වතාවත්.",
                remediesEnglish = "Offering milk rice on Mondays, Metta meditation practice, milk ablutions to Lord Shiva."
            )
        )

        return list
    }

    private fun calculateLongevityAndMaraka(
        birthYear: Int,
        lagnaSign: ZodiacSign,
        positions: List<PlanetPosition>,
        houses: List<HouseInfo>,
        dashaPeriods: List<DashaPeriod>
    ): LongevityMarakaInfo {
        val eighthHouse = houses.find { it.houseNumber == 8 } ?: houses[7]
        val eighthLord = eighthHouse.lord
        val posMap = positions.associateBy { it.planet }

        val lagnaLord = getSignLord(lagnaSign)
        val lagnaLordPos = posMap[lagnaLord]
        val saturnPos = posMap[Planet.SATURN]

        // Classical Ayurdaya determination:
        val isLagnaLordStrong = lagnaLordPos != null && (listOf(1, 4, 5, 7, 9, 10).contains(lagnaLordPos.house) || lagnaLordPos.dignity == Dignity.EXALTED || lagnaLordPos.dignity == Dignity.OWN_SIGN)
        val isSaturnFavorable = saturnPos != null && saturnPos.house != 6 && saturnPos.house != 12

        val ayurCategorySin: String
        val ayurCategoryEng: String
        if (isLagnaLordStrong && isSaturnFavorable) {
            ayurCategorySin = "දීර්ඝ ආයුෂ (වසර 75 - 90+ දක්වා පූර්ණ ආයුර්දාය)"
            ayurCategoryEng = "Deergha Ayu (Long Lifespan: 75 - 90+ years)"
        } else {
            ayurCategorySin = "මධ්‍ය ආයුෂ (වසර 65 - 75 දක්වා මධ්‍යම ආයුර්දාය)"
            ayurCategoryEng = "Madhya Ayu (Medium Lifespan: 65 - 75 years)"
        }

        val eighthLordSin = "8 වන ආයුෂ භාවය ${eighthHouse.sign.sinhalaName} වන අතර එහි අධිපති ${eighthLord.sinhalaName} වේ. ආයුෂස්ථානය ශුභ දෘෂ්ටියෙන් යුක්ත බැවින් නිරෝගීභාවය ආරක්ෂා කර ගැනීමට උපකාරී වේ."
        val eighthLordEng = "8th House of Longevity is in ${eighthHouse.sign.englishName} ruled by ${eighthLord.englishName}. Well-fortified placements shield vitality and promote resilience."

        val saturnFactorSin = "ආයුෂ කාරක ශනි ග්‍රහයා ${saturnPos?.sign?.sinhalaName ?: ""} රාශියේ ${saturnPos?.house ?: 0} වැන්නේ තැන්පත්ව ඇති බැවින් ස්වභාවිකව ජීවිතයේ පැවැත්මට හා නොපසුබස්නා උත්සාහයට ශක්තිය සපයයි."
        val saturnFactorEng = "Saturn, as karaka of longevity, is situated in ${saturnPos?.sign?.englishName ?: ""} in House ${saturnPos?.house ?: 0}, bestowing stamina and enduring bodily resistance."

        val secondLord = houses.find { it.houseNumber == 2 }?.lord ?: Planet.MARS
        val seventhLord = houses.find { it.houseNumber == 7 }?.lord ?: Planet.MARS
        val marakaSin = "2 වන භාවය (ධන) සහ 7 වන භාවය (කලත්‍ර) ජ්‍යොතිෂයේ මාරක ස්ථාන ලෙස හැඳින්වේ. මෙම කේන්ද්‍රයේ 2 අධිපති ${secondLord.sinhalaName} සහ 7 අධිපති ${seventhLord.sinhalaName} මාරක කාරකත්වය උසුලයි."
        val marakaEng = "Houses 2 and 7 are the primary Maraka (transition/critical) houses. In this chart, ${secondLord.englishName} and ${seventhLord.englishName} govern these transitional points."

        // Calculate caution age brackets based on Dasha-Bhukti
        val cautionListSin = mutableListOf<String>()
        val cautionListEng = mutableListOf<String>()

        val candidatePeriods = listOf(
            Pair(birthYear + 45, birthYear + 48),
            Pair(birthYear + 62, birthYear + 65),
            Pair(birthYear + 76, birthYear + 79)
        )

        for ((startY, endY) in candidatePeriods) {
            val matchingDasha = dashaPeriods.find { it.startYear <= startY && it.endYear >= endY } ?: dashaPeriods.firstOrNull()
            val dashaNameSin = matchingDasha?.planet?.sinhalaName ?: "අපල"
            val dashaNameEng = matchingDasha?.planet?.englishName ?: "Challenging"
            val ageStart = startY - birthYear
            val ageEnd = endY - birthYear

            cautionListSin.add("වයස $ageStart - $ageEnd අතර කාලය ($startY - $endY): $dashaNameSin මහ දශා කාලයේ මාරක අතුරු දශා බලපෑම. හෘදය, රුධිර පීඩනය සහ හදිසි අනතුරු පිළිබඳ වෛද්‍ය පරීක්ෂණ සහ ආරක්ෂාව අත්‍යවශ්‍ය වේ.")
            cautionListEng.add("Ages $ageStart - $ageEnd ($startY - $endY): $dashaNameEng Mahadasha sensitive transition phase. Recommended for cardiovascular health checks and prudent lifestyle.")
        }

        val remedySin = "මහා මෘත්‍යුඤ්ජය මන්ත්‍රය දිනපතා උදෑසන 21 වරක් ජප කිරීම, අභිසම්භිධාන පිරිත ශ්‍රවණය කිරීම, මරණයට කැපවූ ගවයින් ඇතුළු සතුන් නිදහස් කිරීම (අභයදාන), ලේ දන්දීම, සහ සෙත් කවි ගායනා කිරීම ආයුෂ වර්ධනයට අතිශයින්ම බලවත්ය."
        val remedyEng = "Reciting Maha Mrityunjaya Mantra 21 times daily, listening to Abhisambhidhana protective chant, animal life-saving (Abhayadana), donating blood, and Bodhi tree water offerings strengthen vital longevity."

        return LongevityMarakaInfo(
            ayurdayaCategorySinhala = ayurCategorySin,
            ayurdayaCategoryEnglish = ayurCategoryEng,
            eighthHouseLordStatusSinhala = eighthLordSin,
            eighthHouseLordStatusEnglish = eighthLordEng,
            saturnLongevityFactorSinhala = saturnFactorSin,
            saturnLongevityFactorEnglish = saturnFactorEng,
            marakaHousesAnalysisSinhala = marakaSin,
            marakaHousesAnalysisEnglish = marakaEng,
            cautionLifeBracketsSinhala = cautionListSin,
            cautionLifeBracketsEnglish = cautionListEng,
            lifeExtendingRemediesSinhala = remedySin,
            lifeExtendingRemediesEnglish = remedyEng
        )
    }

    private fun calculatePastLifeKarma(
        lagnaSign: ZodiacSign,
        moonSign: ZodiacSign,
        positions: List<PlanetPosition>,
        houses: List<HouseInfo>
    ): PastLifeKarmaInfo {
        val ninthHouse = houses.find { it.houseNumber == 9 } ?: houses[8]
        val posMap = positions.associateBy { it.planet }
        val ketu = posMap[Planet.KETU]
        val rahu = posMap[Planet.RAHU]

        val ninthSign = ninthHouse.sign
        val (identitySin, identityEng) = when (ninthSign) {
            ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS -> Pair(
                "පෙර භවයේදී දැහැමි පාලකයෙකු, ආරක්ෂක නායකයෙකු හෝ ආගමික සිද්ධස්ථාන රැකගත් උදාර විරුවෙකු ලෙස ජනතාවට සේවය කර ඇත.",
                "In your previous incarnation, you served as a righteous leader, protector of sacred sanctuaries, or valorous chieftain."
            )
            ZodiacSign.TAURUS, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN -> Pair(
                "පෙර භවයේදී සාර්ථක වෙළඳ ව්‍යාපාරිකයෙකු, ඉඩම් හිමියෙකු හෝ ආර්ථික සම්පත් කළමනාකරණය කළ දානපතියෙකු ලෙස ජීවත්ව ඇත.",
                "In your previous life, you were an enterprising merchant, prosperous land steward, or generous patron supporting community welfare."
            )
            ZodiacSign.GEMINI, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS -> Pair(
                "පෙර භවයේදී ප්‍රඥාවන්ත ගුරුවරයෙකු, ලේඛකයෙකු, වෛද්‍යවරයෙකු හෝ නීති උපදේශකයෙකු ලෙස ශාස්ත්‍රීය සේවාවක් ඉටුකර ඇත.",
                "In your past life, you were an enlightened scholar, educator, physician, or philosophical writer guiding minds toward truth."
            )
            ZodiacSign.CANCER, ZodiacSign.SCORPIO, ZodiacSign.PISCES -> Pair(
                "පෙර භවයේදී ගුප්ත විද්‍යා ගවේෂකයෙකු, අධ්‍යාත්මික තපස්වරයෙකු හෝ සිද්ධස්ථාන ඇසුරේ භාවනායෝගීව විසූ පින්වතෙකු ලෙස කටයුතු කර ඇත.",
                "In your previous birth, you were a dedicated mystic, meditative recluse, or spiritual healer immersed in higher realms."
            )
        }

        val goodMeritSin = "9 වන භාග්‍යස්ථානය සහ 5 වන පුණ්‍යස්ථානය මඟින් පෙන්නුම් කරන්නේ පෙර කළ මහා පුණ්‍යකර්ම, දානමාන සහ සිල්වත් ජීවිතයයි. එම පූර්ව පින නිසා මේ භවයේදී අනතුරුවලින් ගැලවීමේ හැකියාව, සහජ බුද්ධිය සහ උසස් පවුල් පරිසරයක් හිමිව ඇත."
        val goodMeritEng = "Fortified 9th (Bhagya) and 5th (Punya) houses indicate substantial accumulated karmic merit from past charity and virtue, shielding you from mortal perils and granting innate intuition."

        val debtSin = "පෙර භවයේදී ඇතැම් සබඳතාවලදී හෝ මූල්‍ය වගකීම්වලදී ඉටුකිරීමට නොහැකි වූ පොරොන්දු කිහිපයක් මෙලොවදී කර්ම ණය ලෙස පියවීමට සිදුවේ. සමීපතමයින් වෙනුවෙන් කැපවී කටයුතු කළද ඇතැම්විට කළගුණ නොසලකන්නේ මේ හේතුවෙනි."
        val debtEng = "Past karmic debts manifest as emotional obligations where you often sacrifice deeply for others without commensurate gratitude in return."

        val ketuLessonSin = "කේතු ග්‍රහයා ${ketu?.house ?: 0} වැන්නේ පිහිටීමෙන් පෙන්නුම් කරන්නේ පෙර භවයන්හිදී ඔබ දැනටමත් ප්‍රගුණ කර සම්පූර්ණ කර ඇති අධ්‍යාත්මික නිදහසයි. ලෞකික සම්පත් කෙරෙහි අසීමිතව නොඇලී සිටීමේ හැකියාව මේ තුළින් උදා වේ."
        val ketuLessonEng = "Ketu in House ${ketu?.house ?: 0} signifies past-life spiritual mastery. You possess inherent detachment and deep instinctual wisdom."

        val rahuMissionSin = "රාහු ග්‍රහයා ${rahu?.house ?: 0} වැන්නේ පිහිටීමෙන් පෙන්නුම් කරන්නේ මේ භවයේදී ඔබ අත්පත් කරගත යුතු නව අත්දැකීම් සහ ජයගත යුතු ලෞකික අභිප්‍රායන්ය. සමාජයේ සුවිශේෂී පිළිගැනීමක් ලැබීම මෙහි අරමුණයි."
        val rahuMissionEng = "Rahu in House ${rahu?.house ?: 0} points to the soul's current evolutionary hunger: mastering worldly prominence and breaking through uncharted boundaries."

        val evolutionAdviceSin = "අන් අයට නොමසුරුව උපකාර කරන්න, නමුත් ප්‍රතිඋපකාර බලාපොරොත්තු නොවන්න. දිනපතා සිත සංවර කරගැනීමේ භාවනාවක යෙදෙමින් පෙර භවයේ පුණ්‍ය ශක්තිය මතු කරගන්න."
        val evolutionAdviceEng = "Extend altruistic service without attachment to reward. Daily mindfulness practices will awaken the latent spiritual merit earned across past births."

        return PastLifeKarmaInfo(
            pastLifeIdentitySinhala = identitySin,
            pastLifeIdentityEnglish = identityEng,
            accumulatedGoodMeritSinhala = goodMeritSin,
            accumulatedGoodMeritEnglish = goodMeritEng,
            pastLifeKarmaDebtSinhala = debtSin,
            pastLifeKarmaDebtEnglish = debtEng,
            ketuSpiritualLessonSinhala = ketuLessonSin,
            ketuSpiritualLessonEnglish = ketuLessonEng,
            rahuSoulMissionSinhala = rahuMissionSin,
            rahuSoulMissionEnglish = rahuMissionEng,
            soulEvolutionAdviceSinhala = evolutionAdviceSin,
            soulEvolutionAdviceEnglish = evolutionAdviceEng
        )
    }

    private fun calculateProsperityGuidance(
        lagnaSign: ZodiacSign,
        houses: List<HouseInfo>
    ): ProsperityGuidanceInfo {
        val (gemSin, gemEng, metalSin, metalEng) = when (lagnaSign) {
            ZodiacSign.ARIES -> Quadruple("රතු පබළු (Red Coral)", "Red Coral (Praval)", "රත්‍රං හෝ තඹ ලෝහයෙන් වෙදැඟිල්ලට", "Ring finger mounted in gold or copper")
            ZodiacSign.TAURUS -> Quadruple("සුදු නිල් හෝ දියමන්ති (White Sapphire / Diamond)", "White Sapphire or Diamond", "රිදී හෝ ප්ලැටිනම් ලෝහයෙන් මැදඟිල්ලට හෝ සුලැඟිල්ලට", "Middle or little finger mounted in silver or platinum")
            ZodiacSign.GEMINI -> Quadruple("පච්ච (Emerald)", "Emerald (Marakata)", "රත්‍රං ලෝහයෙන් සුලැඟිල්ලට", "Little finger mounted in gold")
            ZodiacSign.CANCER -> Quadruple("මුතු හෝ චන්ද්‍රකාන්ත (Pearl / Moonstone)", "Natural Pearl or Moonstone", "රිදී ලෝහයෙන් සුලැඟිල්ලට", "Little finger mounted in silver")
            ZodiacSign.LEO -> Quadruple("පද්මරාග (Ruby)", "Ruby (Manikya)", "රත්‍රං ලෝහයෙන් වෙදැඟිල්ලට", "Ring finger mounted in gold")
            ZodiacSign.VIRGO -> Quadruple("පච්ච (Emerald)", "Emerald (Panna)", "රත්‍රං හෝ පස්ලෝ ලෝහයෙන් සුලැඟිල්ලට", "Little finger mounted in gold or panchalogha")
            ZodiacSign.LIBRA -> Quadruple("සුදු නිල් හෝ දියමන්ති (White Sapphire / Diamond)", "White Sapphire or Diamond", "රිදී හෝ ප්ලැටිනම් ලෝහයෙන් වෙදැඟිල්ලට", "Ring finger mounted in silver or platinum")
            ZodiacSign.SCORPIO -> Quadruple("රතු පබළු හෝ කහ පුෂ්පරාග (Red Coral / Yellow Sapphire)", "Red Coral or Yellow Sapphire", "රත්‍රං හෝ තඹ ලෝහයෙන් වෙදැඟිල්ලට", "Ring finger mounted in gold or copper")
            ZodiacSign.SAGITTARIUS -> Quadruple("කහ පුෂ්පරාග (Yellow Sapphire)", "Yellow Sapphire (Pushparaga)", "රත්‍රං ලෝහයෙන් වෙදැඟිල්ලට හෝ දබරැඟිල්ලට", "Index or ring finger mounted in gold")
            ZodiacSign.CAPRICORN -> Quadruple("නිල් කැට (Blue Sapphire)", "Blue Sapphire (Indraneela)", "රිදී හෝ සුදු රත්තරන් ලෝහයෙන් මැදඟිල්ලට", "Middle finger mounted in silver or white gold")
            ZodiacSign.AQUARIUS -> Quadruple("නිල් කැට (Blue Sapphire)", "Blue Sapphire (Indraneela)", "රිදී හෝ පස්ලෝ ලෝහයෙන් මැදඟිල්ලට", "Middle finger mounted in silver or panchalogha")
            ZodiacSign.PISCES -> Quadruple("කහ පුෂ්පරාග (Yellow Sapphire)", "Yellow Sapphire", "රත්‍රං ලෝහයෙන් දබරැඟිල්ලට", "Index finger mounted in gold")
        }

        val (luckyColsSin, luckyColsEng, unluckyColsSin, unluckyColsEng) = when (lagnaSign) {
            ZodiacSign.ARIES, ZodiacSign.SCORPIO -> Quadruple(
                listOf("රතු", "කහ", "තැඹිලි"), listOf("Red", "Yellow", "Orange"),
                listOf("කළු", "තද කොළ"), listOf("Black", "Dark Green")
            )
            ZodiacSign.TAURUS, ZodiacSign.LIBRA -> Quadruple(
                listOf("සුදු", "ලා නිල්", "ක්‍රීම් පාට"), listOf("White", "Light Blue", "Cream"),
                listOf("කහ", "තද රතු"), listOf("Yellow", "Dark Red")
            )
            ZodiacSign.GEMINI, ZodiacSign.VIRGO -> Quadruple(
                listOf("කොළ", "සුදු", "ලා කහ"), listOf("Green", "White", "Pale Yellow"),
                listOf("රතු", "තැඹිලි"), listOf("Red", "Orange")
            )
            ZodiacSign.CANCER -> Quadruple(
                listOf("සුදු", "රිදී", "ක්‍රීම්"), listOf("White", "Silver", "Cream"),
                listOf("කළු", "තද නිල්"), listOf("Black", "Dark Blue")
            )
            ZodiacSign.LEO -> Quadruple(
                listOf("රන්වන්", "තැඹිලි", "රතු"), listOf("Gold", "Orange", "Red"),
                listOf("කළු", "අළු"), listOf("Black", "Grey")
            )
            ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> Quadruple(
                listOf("කහ", "රන්වන්", "ලා රෝස"), listOf("Yellow", "Gold", "Light Pink"),
                listOf("කළු", "තද නිල්"), listOf("Black", "Dark Blue")
            )
            ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> Quadruple(
                listOf("තද නිල්", "කළු", "දම්"), listOf("Dark Blue", "Black", "Violet"),
                listOf("රතු", "කහ"), listOf("Red", "Yellow")
            )
        }

        val (luckyNums, directionSin, directionEng) = when (lagnaSign) {
            ZodiacSign.ARIES, ZodiacSign.SCORPIO -> Triple(listOf(9, 1, 3), "දකුණු සහ නැගෙනහිර දිශාව", "South and East")
            ZodiacSign.TAURUS, ZodiacSign.LIBRA -> Triple(listOf(6, 5, 8), "ගිනිකොන සහ වයඹ දිශාව", "South-East and North-West")
            ZodiacSign.GEMINI, ZodiacSign.VIRGO -> Triple(listOf(5, 3, 1), "උතුරු සහ ඊසාන දිශාව", "North and North-East")
            ZodiacSign.CANCER -> Triple(listOf(2, 7, 9), "වයඹ සහ උතුරු දිශාව", "North-West and North")
            ZodiacSign.LEO -> Triple(listOf(1, 4, 9), "නැගෙනහිර දිශාව", "East")
            ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> Triple(listOf(3, 9, 1), "ඊසාන සහ උතුරු දිශාව", "North-East and North")
            ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> Triple(listOf(8, 6, 5), "බස්නාහිර දිශාව", "West")
        }

        val (deitySin, deityEng) = when (lagnaSign) {
            ZodiacSign.ARIES, ZodiacSign.SCORPIO -> Pair(
                "කතරගම දෙවියන් (ස්කන්ධ කුමාර) සහ ශ්‍රී මහා විෂ්ණු දෙවියන් වන්දනා කිරීම අතිශය සුබයි.",
                "Devotion to Lord Skanda/Kataragama and Lord Vishnu brings profound protection."
            )
            ZodiacSign.TAURUS, ZodiacSign.LIBRA -> Pair(
                "පත්තිනි මෑණියන් සහ ලක්ෂ්මී මෑණියන් වන්දනා කිරීමෙන් සෞභාග්‍යය හා පවුල් සාමය හිමිවේ.",
                "Veneration of Goddess Lakshmi and Goddess Pattini brings wealth and domestic joy."
            )
            ZodiacSign.GEMINI, ZodiacSign.VIRGO -> Pair(
                "ශ්‍රී මහා විෂ්ණු දෙවියන් සහ ගණ දෙවියන් වන්දනා කිරීමෙන් බුද්ධිය සහ ධනය වර්ධනය වේ.",
                "Prayers to Lord Vishnu and Lord Ganesha boost intellect and commercial triumphs."
            )
            ZodiacSign.CANCER -> Pair(
                "ශිව දෙවියන් සහ පත්තිනි මෑණියන් වන්දනා කිරීමෙන් මනසේ සතුට සහ නිරෝගීභාවය ලැබේ.",
                "Devotion to Lord Shiva and Goddess Pattini brings serenity and well-being."
            )
            ZodiacSign.LEO -> Pair(
                "සූර්ය දිව්‍ය රාජයා සහ කතරගම දෙවියන් වන්දනා කිරීමෙන් කීර්තිය සහ බලය වර්ධනය වේ.",
                "Solar reverence and devotion to Lord Kataragama elevate authority and honor."
            )
            ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> Pair(
                "බුද්ධ වන්දනාව මූලික කර ගනිමින් බ්‍රහස්පති දෙවියන් සහ ශ්‍රී මහා විෂ්ණු දෙවියන් වන්දනා කිරීම.",
                "Buddha worship complemented with prayers to Brihaspati and Lord Vishnu."
            )
            ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> Pair(
                "ශ්‍රී මහා විෂ්ණු දෙවියන් සහ සෙනසුරු අපල සමනය පිණිස ඊශ්වර දෙවියන් වන්දනා කිරීම.",
                "Devotion to Lord Vishnu and Lord Shiva to harmonize Saturn's cosmic discipline."
            )
        }

        val ritualsSin = listOf(
            "දිනපතා උදෑසන බුද්ධ වන්දනාව සහ මෛත්‍රී භාවනාව සිදුකර දවස ආරම්භ කිරීම.",
            "සෑම සතියකම එක් වරක් හෝ බෝධි පූජාවක් තබා තම ලග්නයට අදාළ ශාන්තිකර්ම ඉටුකිරීම.",
            "අවශ්‍යතා ඇති අයට, විශේෂයෙන් ගුරුවරුන්ට සහ අසරණ අයට නිතර දානමාන පිරිනැමීම.",
            "වැදගත් ගිවිසුම් සහ මූල්‍ය කටයුතු ආරම්භයේදී තම ජයග්‍රාහී $directionSin මුහුණලා කටයුතු කිරීම.",
            "නිවසේ හෝ ව්‍යාපාරික ස්ථානයේ ඊසාන දිශාව පිරිසිදුව තබාගෙන ආලෝකය ලබාදීම."
        )
        val ritualsEng = listOf(
            "Commence each day with serene mindfulness and loving-kindness meditation.",
            "Perform a weekly Bodhi tree worship offering suited to your planetary period.",
            "Extend charity to teachers, students, and underprivileged souls regularly.",
            "Face your auspicious direction ($directionEng) when executing pivotal decisions.",
            "Keep the North-East corner of your living/working space impeccably clean and luminous."
        )

        return ProsperityGuidanceInfo(
            luckyGemstoneSinhala = gemSin,
            luckyGemstoneEnglish = gemEng,
            gemstoneInstructionsSinhala = metalSin,
            gemstoneInstructionsEnglish = metalEng,
            luckyColorsSinhala = luckyColsSin,
            luckyColorsEnglish = luckyColsEng,
            unluckyColorsSinhala = unluckyColsSin,
            unluckyColorsEnglish = unluckyColsEng,
            luckyNumbers = luckyNums,
            auspiciousDirectionsSinhala = directionSin,
            auspiciousDirectionsEnglish = directionEng,
            personalDeityWorshipSinhala = deitySin,
            personalDeityWorshipEnglish = deityEng,
            dailySuccessRitualsSinhala = ritualsSin,
            dailySuccessRitualsEnglish = ritualsEng
        )
    }

    private fun getBhuktiEffect(major: Planet, sub: Planet): Pair<String, String> {
        return when (Pair(major, sub)) {
            Pair(Planet.SATURN, Planet.SATURN) -> Pair(
                "අධික වැඩකටයුතු, නොපසුබස්නා උත්සාහය, වෘත්තීය පදනම ශක්තිමත් වීම, ඉවසීමෙන් කටයුතු කළ යුතු කාලයකි.",
                "Rigorous industry, consolidation of career foundations, requiring perseverance and discipline."
            )
            Pair(Planet.SATURN, Planet.MERCURY) -> Pair(
                "බුද්ධිමය ජයග්‍රහණ, වාණිජ වාසි, නව ආයෝජන සාර්ථක වීම, මිත්‍ර සබඳතා වර්ධනය වේ.",
                "Commercial gains, intellectual breakthroughs, profitable negotiations, supportive alliances."
            )
            Pair(Planet.SATURN, Planet.KETU) -> Pair(
                "ආධ්‍යාත්මික පිබිදීම, අනවශ්‍ය බැඳීම් අත්හැරීම, සුළු සෞඛ්‍ය ගැටළු හෝ ශාරීරික විඩාව.",
                "Spiritual awakening, philosophical detachment, occasional fatigue requiring restorative rest."
            )
            Pair(Planet.SATURN, Planet.VENUS) -> Pair(
                "විවාහ මංගල්‍ය, පවුල් සතුට, නව වාහන හෝ දේපළ මිලදී ගැනීම, සැප පහසුකම් වැඩිවීම.",
                "Matrimonial prospects, domestic bliss, acquisition of vehicles or real estate, refined pleasures."
            )
            Pair(Planet.SATURN, Planet.SUN) -> Pair(
                "පියා සමඟ හෝ උසස් නිලධාරීන් සමඟ සුළු මතභේද, රජයේ කටයුතුවලදී විමසිලිමත් විය යුතුය.",
                "Friction with authorities or paternal figures; requires caution in state dealings."
            )
            Pair(Planet.SATURN, Planet.MOON) -> Pair(
                "මනසේ නොසන්සුන්තාවය, චිත්ත පීඩා, මවගේ සෞඛ්‍යය පිළිබඳ සැලකිලිමත් විය යුතුය.",
                "Emotional vulnerability, restless mind, heightened care needed for maternal well-being."
            )
            Pair(Planet.SATURN, Planet.MARS) -> Pair(
                "කේන්ති යාම, හදිසි අනතුරු පිළිබඳ අවධානය, ඉඩකඩම් ආරවුල් හෝ නීතිමය ගැටළු වලින් ප්‍රවේශම් වන්න.",
                "High-strung energy, prone to impatience, property contention, vigilance against mishaps."
            )
            Pair(Planet.SATURN, Planet.RAHU) -> Pair(
                "අනපේක්ෂිත විපත් හෝ හදිසි පෙරළි, විදේශ ගමන්, රැකියා ස්ථානයේ ක්ෂණික වෙනස්වීම්.",
                "Sudden dramatic turns, foreign voyages, unexpected occupational realignments."
            )
            Pair(Planet.SATURN, Planet.JUPITER) -> Pair(
                "ධර්ම ඥානය, සමාජ ගෞරවය, දරුවන්ගේ දියුණුව, දීර්ඝකාලීන බාධක ජයගැනීමේ ශක්තිය.",
                "Expansion of wisdom, high social esteem, auspicious milestones for children, overcoming hardships."
            )
            Pair(Planet.JUPITER, Planet.JUPITER) -> Pair(
                "විශිෂ්ට භාග්‍යය, දරුවන් ලැබීම, අධ්‍යාපන සාර්ථකත්වය, ධන ධාන්‍ය සම්පත් වර්ධනය.",
                "Supreme auspiciousness, birth of children, scholastic renown, flourishing assets."
            )
            Pair(Planet.JUPITER, Planet.VENUS) -> Pair(
                "සුඛෝපභෝගී ජීවිතය, කලාත්මක ජය, මංගල යෝග, නොසිඳෙන ආදායම් මාර්ග.",
                "Affluent comforts, creative acclaim, marital celebrations, prosperous avenues."
            )
            Pair(Planet.MERCURY, Planet.MERCURY) -> Pair(
                "ව්‍යාපාරික දියුණුව, නව දැනුම අත්පත් කර ගැනීම, සන්නිවේදන සාර්ථකත්වය.",
                "Commercial prosperity, acquisition of innovative insights, communication eloquence."
            )
            else -> Pair(
                "${major.sinhalaName} ග්‍රහයාගේ ප්‍රධාන කාරකත්වයන් ${sub.sinhalaName} ග්‍රහයාගේ අනුග්‍රහය තුළින් ක්‍රියාත්මක වන කාලයකි.",
                "Core significations of ${major.englishName} channeled actively via ${sub.englishName}."
            )
        }
    }

    private fun checkIsChallengingBhukti(major: Planet, sub: Planet): Boolean {
        if (major == Planet.SATURN && (sub == Planet.MARS || sub == Planet.RAHU || sub == Planet.SUN)) return true
        if (major == Planet.MARS && (sub == Planet.SATURN || sub == Planet.RAHU)) return true
        if (major == Planet.RAHU && (sub == Planet.MARS || sub == Planet.SUN || sub == Planet.KETU)) return true
        if (major == Planet.SUN && (sub == Planet.SATURN || sub == Planet.RAHU)) return true
        return false
    }

    private fun getDashaForecast(planet: Planet): Pair<String, String> {
        return when (planet) {
            Planet.KETU -> Pair(
                "ආධ්‍යාත්මික නැඹුරුව, අනවශ්‍ය බැඳීම්වලින් මිදීම, සංචාර සහ ගුප්ත විද්‍යා ලැදියාව.",
                "Spiritual evolution, introspection, detachment from illusions, inner discovery."
            )
            Planet.VENUS -> Pair(
                "සැප පහසුව, විවාහය, ප්‍රේමය, කලාකාමී දියුණුව, නව වාහන සහ ආර්ථික සමෘද්ධිය.",
                "Luxury, romance, matrimony, artistic achievements, vehicular acquisitions, refined joy."
            )
            Planet.SUN -> Pair(
                "නම්බුව, රාජ්‍ය ගෞරවය, පීතෘ සබඳතා, නායකත්වය සහ ආත්ම විශ්වාසයේ පිබිදීම.",
                "Executive authority, fatherly blessings, vitality, public reputation, regal favor."
            )
            Planet.MOON -> Pair(
                "මනසේ සතුට, මවගේ ආශිර්වාදය, ජනප්‍රියත්වය, සංචාර සහ සෞම්‍ය ජීවන පරිසරය.",
                "Emotional fulfillment, mother's guidance, societal popularity, travel, peace of mind."
            )
            Planet.MARS -> Pair(
                "ධෛර්යය, ඉඩකඩම් සහ නිවාස හිමිකම, ක්‍රීඩා සහ තාක්ෂණික ජයග්‍රහණ, තීරණාත්මක බලය.",
                "Courageous initiatives, property acquisition, technical/engineering conquest, vigor."
            )
            Planet.RAHU -> Pair(
                "විදේශ ගමන්, තාක්ෂණික දියුණුව, අසීමිත බලාපොරොත්තු සහ හදිසි උසස්වීම්.",
                "Foreign voyages, material ambitions, technological breakthroughs, sudden elevations."
            )
            Planet.JUPITER -> Pair(
                "ධර්ම ඥානය, දරුවන් ලැබීම, උසස් අධ්‍යාපනය, ධනය, ගුරු ආශිර්වාදය සහ මංගල යෝග.",
                "Profound wisdom, children birth, educational brilliance, wealth accumulation, spiritual grace."
            )
            Planet.SATURN -> Pair(
                "නොපසුබට උත්සාහය, වෘත්තීය ස්ථාවරත්වය, කර්ම ඵල විඳීම, විනය සහ මහජන ප්‍රසාදය.",
                "Hard-earned resilience, unshakeable career mastery, discipline, karmic rewards."
            )
            Planet.MERCURY -> Pair(
                "ව්‍යාපාරික ජය, ලේඛන හා සන්නිවේදන කුසලතාව, විභාග ජයග්‍රහණ සහ මූල්‍ය වර්ධනය.",
                "Commercial prosperity, communication mastery, academic triumphs, lucrative agreements."
            )
        }
    }

    private fun getSignLord(sign: ZodiacSign): Planet {
        return when (sign) {
            ZodiacSign.ARIES, ZodiacSign.SCORPIO -> Planet.MARS
            ZodiacSign.TAURUS, ZodiacSign.LIBRA -> Planet.VENUS
            ZodiacSign.GEMINI, ZodiacSign.VIRGO -> Planet.MERCURY
            ZodiacSign.CANCER -> Planet.MOON
            ZodiacSign.LEO -> Planet.SUN
            ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> Planet.JUPITER
            ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> Planet.SATURN
        }
    }

    private fun parseDateToMillis(dateStr: String): Long {
        return try {
            val parts = dateStr.split("-")
            val y = parts[0].toInt()
            val m = parts[1].toInt()
            val d = parts[2].toInt()
            Calendar.getInstance().apply {
                set(y, m - 1, d, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } catch (_: Exception) {
            Calendar.getInstance().timeInMillis
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
