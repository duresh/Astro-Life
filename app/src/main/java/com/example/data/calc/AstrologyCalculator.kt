package com.example.data.calc

import com.example.data.model.*
import java.util.Calendar
import java.util.Locale
import kotlin.math.*

object AstrologyCalculator {

    fun calculateBirthChart(
        name: String,
        gender: String,
        year: Int,
        month: Int, // 1 to 12
        day: Int, // 1 to 31
        hour: Int, // 0 to 23
        minute: Int, // 0 to 59
        locationName: String,
        latitude: Double,
        longitude: Double,
        timezoneOffset: Double = 5.5
    ): BirthChartResult {
        // Universal Time (UT)
        val decimalTime = hour + (minute / 60.0)
        var utTime = decimalTime - timezoneOffset
        var dayAdjustment = 0
        if (utTime < 0.0) {
            utTime += 24.0
            dayAdjustment = -1
        } else if (utTime >= 24.0) {
            utTime -= 24.0
            dayAdjustment = 1
        }

        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day)
        cal.add(Calendar.DAY_OF_MONTH, dayAdjustment)

        val adjYear = cal.get(Calendar.YEAR)
        val adjMonth = cal.get(Calendar.MONTH) + 1
        val adjDay = cal.get(Calendar.DAY_OF_MONTH)

        // Julian Day
        val jd = computeJulianDay(adjYear, adjMonth, adjDay, utTime)
        val t = (jd - 2451545.0) / 36525.0

        // High-precision Lahiri Ayanamsha (Chitra Paksha)
        // Epoch 2000.0 = 23.856 degrees; yearly precession ~50.29 arcseconds
        val ayanamsha = 23.856 + (1.396 * t)

        // Greenwich Mean Sidereal Time (GMST) & Local Sidereal Time (LST)
        val gmst0 = (280.46061837 + 360.985647366 * (jd - 2451545.0) + 0.000387933 * t * t)
        val gmst = (gmst0 % 360.0 + 360.0) % 360.0
        val lst = ((gmst + longitude) % 360.0 + 360.0) % 360.0

        // Ascendant (Lagna) calculation using standard spherical astronomy
        val epsRad = Math.toRadians(23.439291 - 0.013004 * t)
        val lstRad = Math.toRadians(lst)
        val latRad = Math.toRadians(latitude)

        val sinLst = sin(lstRad)
        val cosLst = cos(lstRad)
        val tanLat = tan(latRad)

        // y = cos(lst), x = -sin(lst)*cos(eps) - tan(lat)*sin(eps)
        val yAsc = cosLst
        val xAsc = -sinLst * cos(epsRad) - tanLat * sin(epsRad)
        var ascDeg = Math.toDegrees(atan2(yAsc, xAsc))
        ascDeg = (ascDeg % 360.0 + 360.0) % 360.0

        // Nirayana Lagna (Subtract Lahiri Ayanamsha)
        val nirayanaLagna = (ascDeg - ayanamsha + 360.0) % 360.0

        val lagnaSign = ZodiacSign.fromLongitude(nirayanaLagna)
        val lagnaDegInSign = (nirayanaLagna % 30.0).toInt()
        val lagnaMinInSign = (((nirayanaLagna % 30.0) - lagnaDegInSign) * 60).toInt()
        val navamshaLagna = calculateNavamshaSign(lagnaSign, nirayanaLagna % 30.0)

        // Calculate planetary positions with geocentric projection
        val planetPositions = calculatePlanetaryPositions(t, ayanamsha, lagnaSign)

        val moonPos = planetPositions.first { it.planet == Planet.MOON }
        val moonSign = moonPos.sign
        val (moonNakshatra, moonPada) = Nakshatra.fromLongitude(moonPos.longitude)

        // Calculate 12 Houses (Equal Sign / Sri Lankan Bhava Chakra)
        val houses = calculateHouses(lagnaSign, planetPositions)

        // Detect Yogas
        val yogas = detectYogas(lagnaSign, moonSign, planetPositions, houses)

        // Vimshottari Dasha with all 9 Bhuktis for each Mahadasha
        val dashaPeriods = VimshottariCalculator.calculateVimshottariPeriods(year, month, day, moonPos.longitude)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentDasha = dashaPeriods.find { it.isCurrent }
            ?: dashaPeriods.find { it.startYear <= currentYear && it.endYear >= currentYear }
            ?: dashaPeriods.firstOrNull()

        // Comprehensive Vimshottari life analysis (Current Dasha/Bhukti, Inauspicious & Caution periods,
        // Doshas, Longevity & Maraka, Past Life, and Prosperity guidance)
        val vimsottariAnalysis = VimshottariCalculator.calculateFullAnalysis(
            birthYear = year,
            birthMonth = month,
            birthDay = day,
            lagnaSign = lagnaSign,
            moonSign = moonSign,
            moonNakshatra = moonNakshatra,
            positions = planetPositions,
            houses = houses,
            dashaPeriods = dashaPeriods
        )

        // Specific Predictions: Spouse and Children
        val marriagePrediction = generateMarriagePrediction(lagnaSign, houses, planetPositions, year, gender)
        val childrenPrediction = generateChildrenPrediction(lagnaSign, houses, planetPositions, year)
        val careerPrediction = generateCareerPrediction(lagnaSign, houses, planetPositions)

        // Comprehensive Life Destiny Analysis (Foreign travel, wealth, property/vehicles,
        // life triumphs & setbacks, meritorious deeds, family gains, and health vitality)
        val lifeDestiny = LifeDestinyCalculator.calculateLifeDestiny(
            birthYear = year,
            lagnaSign = lagnaSign,
            moonSign = moonSign,
            positions = planetPositions,
            houses = houses,
            dashaPeriods = dashaPeriods
        )

        val summarySinhala = generateSummarySinhala(lagnaSign, moonSign, moonNakshatra, yogas)
        val summaryEnglish = generateSummaryEnglish(lagnaSign, moonSign, moonNakshatra, yogas)

        val birthDateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
        val birthTimeStr = String.format(Locale.US, "%02d:%02d", hour, minute)

        return BirthChartResult(
            name = name,
            gender = gender,
            birthDateFormatted = birthDateStr,
            birthTimeFormatted = birthTimeStr,
            locationName = locationName,
            lagnaSign = lagnaSign,
            lagnaDegree = lagnaDegInSign,
            lagnaMinute = lagnaMinInSign,
            moonSign = moonSign,
            moonNakshatra = moonNakshatra,
            moonPada = moonPada,
            navamshaLagnaSign = navamshaLagna,
            planetaryPositions = planetPositions,
            houses = houses,
            yogas = yogas,
            dashaPeriods = dashaPeriods,
            currentDasha = currentDasha,
            marriagePrediction = marriagePrediction,
            childrenPrediction = childrenPrediction,
            careerPrediction = careerPrediction,
            generalSummarySinhala = summarySinhala,
            generalSummaryEnglish = summaryEnglish,
            vimsottariAnalysis = vimsottariAnalysis,
            lifeDestiny = lifeDestiny
        )
    }

    private fun computeJulianDay(year: Int, month: Int, day: Int, utHour: Double): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + (a / 4)
        return (floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5) + (utHour / 24.0)
    }

    private fun calculateNavamshaSign(rashi: ZodiacSign, degInSign: Double): ZodiacSign {
        val pada = (degInSign / (30.0 / 9.0)).toInt().coerceIn(0, 8)
        val startSignId = when (rashi) {
            ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS -> 0 // Aries (Fire)
            ZodiacSign.TAURUS, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN -> 9 // Capricorn (Earth)
            ZodiacSign.GEMINI, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS -> 6 // Libra (Air)
            ZodiacSign.CANCER, ZodiacSign.SCORPIO, ZodiacSign.PISCES -> 3 // Cancer (Water)
        }
        return ZodiacSign.fromId(startSignId + pada)
    }

    private fun calculatePlanetaryPositions(
        t: Double,
        ayanamsha: Double,
        lagnaSign: ZodiacSign
    ): List<PlanetPosition> {
        val positions = mutableListOf<PlanetPosition>()

        // 1. Sun (Earth's parent light)
        val sunMeanLong = (280.46646 + 36000.76983 * t) % 360.0
        val sunMeanAnom = Math.toRadians((357.52911 + 35999.05029 * t) % 360.0)
        val sunCenter = (1.914602 - 0.004817 * t) * sin(sunMeanAnom) + 0.019993 * sin(2 * sunMeanAnom)
        val sunTrueLong = (sunMeanLong + sunCenter + 360.0) % 360.0
        val sunRad = Math.toRadians(sunTrueLong)
        val rEarth = 1.00014 - 0.01671 * cos(sunMeanAnom)

        val sunNirayana = (sunTrueLong - ayanamsha + 360.0) % 360.0
        positions.add(buildPlanetPosition(Planet.SUN, sunNirayana, false, lagnaSign))

        // 2. Moon
        val moonMeanLong = (218.3164477 + 481267.88128 * t) % 360.0
        val moonAnom = Math.toRadians((134.9634 + 477198.8675 * t) % 360.0)
        val elongation = Math.toRadians((297.8502 + 445267.1114 * t) % 360.0)
        val moonPerturb = 6.289 * sin(moonAnom) + 1.274 * sin(2 * elongation - moonAnom) + 0.658 * sin(2 * elongation) - 0.186 * sin(sunMeanAnom)
        val moonTrueLong = (moonMeanLong + moonPerturb + 360.0) % 360.0
        val moonNirayana = (moonTrueLong - ayanamsha + 360.0) % 360.0
        positions.add(buildPlanetPosition(Planet.MOON, moonNirayana, false, lagnaSign))

        // Helper for Geocentric Ecliptic Projection
        // Planet heliocentric coordinates (xp, yp), Earth coordinates (xe, ye) = (-rEarth*cos(sun), -rEarth*sin(sun))
        // dx = xp + rEarth*cos(sun), dy = yp + rEarth*sin(sun)
        fun projectGeocentric(helioLongDeg: Double, rPlanetAU: Double): Double {
            val helioRad = Math.toRadians(helioLongDeg)
            val dx = rPlanetAU * cos(helioRad) + rEarth * cos(sunRad)
            val dy = rPlanetAU * sin(helioRad) + rEarth * sin(sunRad)
            var geoDeg = Math.toDegrees(atan2(dy, dx))
            return (geoDeg % 360.0 + 360.0) % 360.0
        }

        // 3. Mars (කුජ)
        val marsMeanLong = (355.433 + 19141.696 * t) % 360.0
        val marsAnom = Math.toRadians((19.373 + 19139.858 * t) % 360.0)
        val marsEqCenter = 10.691 * sin(marsAnom) + 0.623 * sin(2 * marsAnom)
        val marsHelioLong = (marsMeanLong + marsEqCenter + 360.0) % 360.0
        val rMars = 1.52368 * (1.0 - 0.0934 * cos(marsAnom))
        val marsSayana = projectGeocentric(marsHelioLong, rMars)
        val marsNirayana = (marsSayana - ayanamsha + 360.0) % 360.0
        val marsRetro = (sunTrueLong - marsSayana + 360.0) % 360.0 in 150.0..210.0
        positions.add(buildPlanetPosition(Planet.MARS, marsNirayana, marsRetro, lagnaSign))

        // 4. Mercury (බුධ)
        val merMeanLong = (252.2509 + 149474.0722 * t) % 360.0
        val merAnom = Math.toRadians((174.7948 + 149472.6746 * t) % 360.0)
        val merEqCenter = 23.44 * sin(merAnom) + 2.98 * sin(2 * merAnom)
        val merHelioLong = (merMeanLong + merEqCenter + 360.0) % 360.0
        val rMer = 0.3871 * (1.0 - 0.2056 * cos(merAnom))
        val merSayana = projectGeocentric(merHelioLong, rMer)
        val merNirayana = (merSayana - ayanamsha + 360.0) % 360.0
        val merRetro = cos(merAnom) < -0.5
        positions.add(buildPlanetPosition(Planet.MERCURY, merNirayana, merRetro, lagnaSign))

        // 5. Jupiter (ගුරු)
        val jupMeanLong = (34.351 + 3036.303 * t) % 360.0
        val jupAnom = Math.toRadians((20.020 + 3034.906 * t) % 360.0)
        val jupEqCenter = 5.555 * sin(jupAnom) + 0.168 * sin(2 * jupAnom)
        val jupHelioLong = (jupMeanLong + jupEqCenter + 360.0) % 360.0
        val rJup = 5.2026 * (1.0 - 0.0485 * cos(jupAnom))
        val jupSayana = projectGeocentric(jupHelioLong, rJup)
        val jupNirayana = (jupSayana - ayanamsha + 360.0) % 360.0
        val jupRetro = (sunTrueLong - jupSayana + 360.0) % 360.0 in 120.0..240.0
        positions.add(buildPlanetPosition(Planet.JUPITER, jupNirayana, jupRetro, lagnaSign))

        // 6. Venus (සිකුරු)
        val venMeanLong = (181.9798 + 58519.2130 * t) % 360.0
        val venAnom = Math.toRadians((50.115 + 58517.8039 * t) % 360.0)
        val venEqCenter = 0.776 * sin(venAnom)
        val venHelioLong = (venMeanLong + venEqCenter + 360.0) % 360.0
        val rVen = 0.7233 * (1.0 - 0.0068 * cos(venAnom))
        val venSayana = projectGeocentric(venHelioLong, rVen)
        val venNirayana = (venSayana - ayanamsha + 360.0) % 360.0
        val venRetro = cos(venAnom) < -0.7
        positions.add(buildPlanetPosition(Planet.VENUS, venNirayana, venRetro, lagnaSign))

        // 7. Saturn (ශනි)
        val satMeanLong = (50.077 + 1223.511 * t) % 360.0
        val satAnom = Math.toRadians((317.021 + 1221.551 * t) % 360.0)
        val satEqCenter = 6.358 * sin(satAnom) + 0.220 * sin(2 * satAnom)
        val satHelioLong = (satMeanLong + satEqCenter + 360.0) % 360.0
        val rSat = 9.5549 * (1.0 - 0.0555 * cos(satAnom))
        val satSayana = projectGeocentric(satHelioLong, rSat)
        val satNirayana = (satSayana - ayanamsha + 360.0) % 360.0
        val satRetro = (sunTrueLong - satSayana + 360.0) % 360.0 in 120.0..240.0
        positions.add(buildPlanetPosition(Planet.SATURN, satNirayana, satRetro, lagnaSign))

        // 8. Rahu (True Mean Node)
        val rahuMean = (125.04452 - 1934.136261 * t + 360.0) % 360.0
        val rahuNirayana = (rahuMean - ayanamsha + 360.0) % 360.0
        positions.add(buildPlanetPosition(Planet.RAHU, rahuNirayana, true, lagnaSign))

        // 9. Ketu (180 opposite Rahu)
        val ketuNirayana = (rahuNirayana + 180.0) % 360.0
        positions.add(buildPlanetPosition(Planet.KETU, ketuNirayana, true, lagnaSign))

        return positions
    }

    private fun buildPlanetPosition(
        planet: Planet,
        longitude: Double,
        isRetrograde: Boolean,
        lagnaSign: ZodiacSign
    ): PlanetPosition {
        val sign = ZodiacSign.fromLongitude(longitude)
        val degInSign = (longitude % 30.0).toInt()
        val minInSign = (((longitude % 30.0) - degInSign) * 60).toInt()
        val house = ((sign.id - lagnaSign.id + 12) % 12) + 1
        val dignity = planet.getDignity(sign)
        val (nak, pada) = Nakshatra.fromLongitude(longitude)
        val navSign = calculateNavamshaSign(sign, longitude % 30.0)

        return PlanetPosition(
            planet = planet,
            longitude = longitude,
            sign = sign,
            degreeInSign = degInSign,
            minuteInSign = minInSign,
            house = house,
            isRetrograde = isRetrograde,
            dignity = dignity,
            nakshatra = nak,
            pada = pada,
            navamshaSign = navSign
        )
    }

    private fun calculateHouses(
        lagnaSign: ZodiacSign,
        positions: List<PlanetPosition>
    ): List<HouseInfo> {
        val houses = mutableListOf<HouseInfo>()

        for (h in 1..12) {
            val sign = ZodiacSign.fromId((lagnaSign.id + h - 1) % 12)
            val lord = getSignLord(sign)
            val planetsInHouse = positions.filter { it.house == h }

            val (sigSin, sigEng) = getHouseSignificance(h)
            val (readSin, readEng) = generateHouseReading(h, sign, lord, planetsInHouse)

            val bhavaNameSin = when (h) {
                1 -> "1 වන භාවය (තනු / ලග්නය)"
                2 -> "2 වන භාවය (ධන ස්ථානය)"
                3 -> "3 වන භාවය (සහෝදර / වික්‍රම)"
                4 -> "4 වන භාවය (මාතෘ / බන්ධු)"
                5 -> "5 වන භාවය (පුත්‍ර / බුද්ධි)"
                6 -> "6 වන භාවය (ශත්‍රැ / රෝග)"
                7 -> "7 වන භාවය (කලත්‍ර / විවාහ)"
                8 -> "8 වන භාවය (ආයුෂ / මරණ)"
                9 -> "9 වන භාවය (භාග්‍ය / පීතෘ)"
                10 -> "10 වන භාවය (කර්ම / රැකියා)"
                11 -> "11 වන භාවය (අය / ලාභ)"
                12 -> "12 වන භාවය (වැය / මෝක්ෂ)"
                else -> "$h වන භාවය"
            }

            val bhavaNameEng = when (h) {
                1 -> "1st House (Lagna / Self)"
                2 -> "2nd House (Wealth / Family)"
                3 -> "3rd House (Siblings / Courage)"
                4 -> "4th House (Mother / Assets)"
                5 -> "5th House (Children / Intellect)"
                6 -> "6th House (Enemies / Health)"
                7 -> "7th House (Spouse / Partnership)"
                8 -> "8th House (Longevity / Mystery)"
                9 -> "9th House (Fortune / Spirituality)"
                10 -> "10th House (Career / Fame)"
                11 -> "11th House (Gains / Friends)"
                12 -> "12th House (Expenditure / Moksha)"
                else -> "House $h"
            }

            houses.add(
                HouseInfo(
                    houseNumber = h,
                    bhavaNameSinhala = bhavaNameSin,
                    bhavaNameEnglish = bhavaNameEng,
                    sign = sign,
                    lord = lord,
                    planetsInside = planetsInHouse,
                    significanceSinhala = sigSin,
                    significanceEnglish = sigEng,
                    readingSinhala = readSin,
                    readingEnglish = readEng
                )
            )
        }
        return houses
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

    private fun getHouseSignificance(h: Int): Pair<String, String> {
        return when (h) {
            1 -> Pair("ශරීරය, පෞරුෂය, නිරෝගීභාවය සහ ජීවිතයේ ආරම්භය", "Physical body, self-identity, vital health, appearance")
            2 -> Pair("මුදල්, ධනය රැස්කිරීම, පවුල් සතුට, කථන හැකියාව", "Accumulated wealth, family heritage, speech, eating habits")
            3 -> Pair("ධෛර්යය, සහෝදර සහෝදරියන්, කෙටි ගමන්, උත්සාහය", "Courage, mental willpower, younger siblings, short journeys")
            4 -> Pair("මව, නිවාස, ඉඩකඩම්, වාහන, හෘද සාමය සහ සැප පහසුව", "Mother, home, real estate, vehicles, peace of mind")
            5 -> Pair("දරුවන්, පූර්ව පින්, බුද්ධි මට්ටම, ප්‍රේමය, නිර්මාණශීලිත්වය", "Children, intellect, past life merits, romance, creativity")
            6 -> Pair("රෝගාබාධ, ණයතුරුස්, සතුරන්, නඩුහබ, බාධා ජයගැනීම", "Daily struggles, health ailments, debts, litigations, service")
            7 -> Pair("සහකරු හෝ සහකාරිය, විවාහ දිවිය, ව්‍යාපාර හවුල්කාරිත්වය", "Spouse, marital union, partnerships, public relations")
            8 -> Pair("ආයුෂ, සැඟවුණු රහස්, අනතුරු, උරුමයන්, ආධ්‍යාත්මික පිබිදීම", "Longevity, sudden events, hidden inheritances, transformation")
            9 -> Pair("භාග්‍යය, පියා, ධර්මය, ආගමික නැඹුරුව, විදේශ ගමන්, උසස් අධ්‍යාපනය", "Fortune, father, higher knowledge, pilgrimages, divine grace")
            10 -> Pair("රැකියාව, නම්බුව, සමාජ තත්වය, කීර්තිය, රාජ්‍ය ගෞරවය", "Profession, prestige, authority, executive actions, fame")
            11 -> Pair("ආදායම් මාර්ග, මිතුරන්, සමාජ සබඳතා, බලාපොරොත්තු ඉටුවීම", "Income sources, elder siblings, noble friends, aspirations fulfilled")
            12 -> Pair("වියදම්, විදේශ වාසය, රෝහල්ගතවීම්, නිදහස් වීම, මෝක්ෂය", "Expenditures, foreign settlement, spiritual liberation, dream realm")
            else -> Pair("", "")
        }
    }

    private fun generateHouseReading(
        house: Int,
        sign: ZodiacSign,
        lord: Planet,
        planets: List<PlanetPosition>
    ): Pair<String, String> {
        val pSin = if (planets.isNotEmpty()) planets.joinToString(", ") { "${it.planet.sinhalaName} (${it.dignity.sinhalaName})" } else "ග්‍රහයින් නැත"
        val pEng = if (planets.isNotEmpty()) planets.joinToString(", ") { "${it.planet.englishName} (${it.dignity.englishName})" } else "No planets"

        val sinText = "${sign.sinhalaName} රාශියට අධිපති ${lord.sinhalaName} ග්‍රහයා වේ. මෙම භාවයේ පිහිටි ග්‍රහයින්: $pSin. " +
                when {
                    planets.any { it.dignity == Dignity.EXALTED } -> "උච්ච ග්‍රහ පිහිටීම නිසා මෙම භාවයට අදාළ කාරකත්වයන් උපරිමයෙන් ශක්තිමත් වේ."
                    planets.any { it.dignity == Dignity.DEBILITATED } -> "නීච ග්‍රහ බලපෑමක් ඇති බැවින් ඉවසීමෙන් සහ නිසි වත්පිළිවෙත් තුළින් ජයගත යුතුය."
                    planets.isEmpty() -> "${lord.sinhalaName} ග්‍රහයාගේ පිහිටීම අනුව මෙම ස්ථානය පාලනය වේ."
                    else -> "ශුභ ග්‍රහ බලපෑම් හේතුවෙන් යහපත් ප්‍රතිඵල අත්පත් වේ."
                }

        val engText = "Ruled by ${lord.englishName} in ${sign.englishName}. Residing planets: $pEng. " +
                when {
                    planets.any { it.dignity == Dignity.EXALTED } -> "Exalted planet grants supreme prosperity and power to this sphere."
                    planets.any { it.dignity == Dignity.DEBILITATED } -> "Debilitated placement requires patience and remedies to overcome friction."
                    planets.isEmpty() -> "The results of this house are channeled via the placement of its ruler ${lord.englishName}."
                    else -> "Well-supported planetary dynamics bring favorable growth in this area."
                }

        return Pair(sinText, engText)
    }

    private fun detectYogas(
        lagnaSign: ZodiacSign,
        moonSign: ZodiacSign,
        positions: List<PlanetPosition>,
        houses: List<HouseInfo>
    ): List<YogaInfo> {
        val yogas = mutableListOf<YogaInfo>()
        val posMap = positions.associateBy { it.planet }

        // 1. Ruchaka Yoga (Pancha Mahapurusha - Mars)
        val mars = posMap[Planet.MARS]
        if (mars != null && listOf(1, 4, 7, 10).contains(mars.house) &&
            (mars.sign == ZodiacSign.CAPRICORN || mars.sign == ZodiacSign.ARIES || mars.sign == ZodiacSign.SCORPIO)) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "රුචක යෝගය (පංච මහා පුරුෂ)",
                    nameEnglish = "Ruchaka Yoga (Pancha Mahapurusha)",
                    categorySinhala = "පංච මහා පුරුෂ යෝග",
                    categoryEnglish = "Pancha Mahapurusha Yoga",
                    planetsInvolved = listOf(Planet.MARS),
                    descriptionSinhala = "කුජ කේන්ද්‍රස්ථානයක (1, 4, 7, 10) උච්චව හෝ ස්වක්ෂේත්‍රව පිහිටීමෙන් උදාවන ප්‍රබල යෝගයකි.",
                    descriptionEnglish = "Formed when Mars is situated in an angular house (Kendra) in its exaltation or own sign.",
                    futureImpactSinhala = "අසීමිත නිර්භීතභාවය, නායකත්ව හැකියාව, ඉඩකඩම් සහ දේපළ හිමිකම. රාජ්‍ය හෝ ආරක්ෂක අංශවල ඉහළ නම්බුනාම හිමිවේ.",
                    futureImpactEnglish = "Exceptional courage, leadership prowess, mastery over lands and properties, commanding respect in society.",
                    activationTimeSinhala = "කුජ මහ දශාවේ හෝ අතුරු දශාවලදී පූර්ණ ඵල ලබාදේ.",
                    activationTimeEnglish = "Full fruits activate during Mars Mahadasha and Antardashas."
                )
            )
        }

        // 2. Bhadra Yoga (Mercury)
        val mercury = posMap[Planet.MERCURY]
        if (mercury != null && listOf(1, 4, 7, 10).contains(mercury.house) &&
            (mercury.sign == ZodiacSign.VIRGO || mercury.sign == ZodiacSign.GEMINI)) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "භද්‍ර යෝගය (පංච මහා පුරුෂ)",
                    nameEnglish = "Bhadra Yoga (Pancha Mahapurusha)",
                    categorySinhala = "පංච මහා පුරුෂ යෝග",
                    categoryEnglish = "Pancha Mahapurusha Yoga",
                    planetsInvolved = listOf(Planet.MERCURY),
                    descriptionSinhala = "බුධ ග්‍රහයා කේන්ද්‍රස්ථානයක ස්වක්ෂේත්‍ර හෝ උච්ච බලයෙන් පිහිටීමෙන් හටගනී.",
                    descriptionEnglish = "Formed when Mercury resides in an angle (1, 4, 7, 10) in Gemini or Virgo.",
                    futureImpactSinhala = "විශිෂ්ට බුද්ධිය, වාග් චාතුර්යය, ලේඛන සහ වාණිජ ක්ෂේත්‍රයේ ඉහළම සාර්ථකත්වය. දීර්ඝායුෂ සහ මිහිරි කටහඬ හිමිවේ.",
                    futureImpactEnglish = "Extraordinary intellect, eloquent speech, commercial mastery, and scholastic renown.",
                    activationTimeSinhala = "බුධ මහ දශා කාලය තුළදී බුද්ධිමය ජයග්‍රහණ සහ ධනය හිමිවේ.",
                    activationTimeEnglish = "Activates during Mercury Mahadasha and sub-periods."
                )
            )
        }

        // 3. Hamsa Yoga (Jupiter)
        val jupiter = posMap[Planet.JUPITER]
        if (jupiter != null && listOf(1, 4, 7, 10).contains(jupiter.house) &&
            (jupiter.sign == ZodiacSign.CANCER || jupiter.sign == ZodiacSign.SAGITTARIUS || jupiter.sign == ZodiacSign.PISCES)) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "හංස යෝගය (පංච මහා පුරුෂ)",
                    nameEnglish = "Hamsa Yoga (Pancha Mahapurusha)",
                    categorySinhala = "පංච මහා පුරුෂ යෝග",
                    categoryEnglish = "Pancha Mahapurusha Yoga",
                    planetsInvolved = listOf(Planet.JUPITER),
                    descriptionSinhala = "ගුරු ග්‍රහයා කේන්ද්‍රස්ථානයක උච්චව හෝ ස්වක්ෂේත්‍රව පිහිටීමෙන් නිර්මාණය වේ.",
                    descriptionEnglish = "Jupiter seated in Kendra in Cancer, Sagittarius, or Pisces.",
                    futureImpactSinhala = "ධාර්මික දිවිය, උසස් ඥානය, සමාජයේ මුදුන්පත් ගෞරවය, ආධ්‍යාත්මික සුවය සහ පිරිසිදු චරිතය.",
                    futureImpactEnglish = "Virtuous nature, profound wisdom, reverence by leaders, spiritual distinction.",
                    activationTimeSinhala = "ගුරු මහ දශා කාලය තුළදී පෙර පින් මතු වී ජීවිතය බැබළේ.",
                    activationTimeEnglish = "Flourishes abundantly during Jupiter Mahadasha."
                )
            )
        }

        // 4. Malavya Yoga (Venus)
        val venus = posMap[Planet.VENUS]
        if (venus != null && listOf(1, 4, 7, 10).contains(venus.house) &&
            (venus.sign == ZodiacSign.PISCES || venus.sign == ZodiacSign.TAURUS || venus.sign == ZodiacSign.LIBRA)) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "මාලව්‍ය යෝගය (පංච මහා පුරුෂ)",
                    nameEnglish = "Malavya Yoga (Pancha Mahapurusha)",
                    categorySinhala = "පංච මහා පුරුෂ යෝග",
                    categoryEnglish = "Pancha Mahapurusha Yoga",
                    planetsInvolved = listOf(Planet.VENUS),
                    descriptionSinhala = "සිකුරු ග්‍රහයා කේන්ද්‍රස්ථානයක උච්චව (මීන) හෝ ස්වක්ෂේත්‍රව (වෘෂභ, තුලා) සිටීම.",
                    descriptionEnglish = "Venus in Kendra in Pisces, Taurus, or Libra.",
                    futureImpactSinhala = "සුඛෝපභෝගී ජීවිතය, කලාවට ලැදියාව, රූමත් බව, උසස් වාහන සහ ප්‍රීතිමත් විවාහ දිවිය.",
                    futureImpactEnglish = "Luxurious living, artistic brilliance, attractive aura, vehicle wealth, and bliss.",
                    activationTimeSinhala = "සිකුරු මහ දශාවේදී ජීවිතය සැපවත් වේ.",
                    activationTimeEnglish = "Bestows grandeur during Venus Mahadasha."
                )
            )
        }

        // 5. Sasa Yoga (Saturn)
        val saturn = posMap[Planet.SATURN]
        if (saturn != null && listOf(1, 4, 7, 10).contains(saturn.house) &&
            (saturn.sign == ZodiacSign.LIBRA || saturn.sign == ZodiacSign.CAPRICORN || saturn.sign == ZodiacSign.AQUARIUS)) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "ශශ යෝගය (පංච මහා පුරුෂ)",
                    nameEnglish = "Sasa Yoga (Pancha Mahapurusha)",
                    categorySinhala = "පංච මහා පුරුෂ යෝග",
                    categoryEnglish = "Pancha Mahapurusha Yoga",
                    planetsInvolved = listOf(Planet.SATURN),
                    descriptionSinhala = "ශනි ග්‍රහයා කේන්ද්‍රස්ථානයක උච්චව හෝ ස්වක්ෂේත්‍රව පිහිටීම.",
                    descriptionEnglish = "Saturn in Kendra in Libra, Capricorn, or Aquarius.",
                    futureImpactSinhala = "මහජන නායකත්වය, නොසැලෙන විනය, ස්ථාවර දීර්ඝකාලීන ධනය, දේශපාලන බලය.",
                    futureImpactEnglish = "Masses devotion, formidable resilience, unyielding authority and enduring assets.",
                    activationTimeSinhala = "වයස අවුරුදු 36 න් පසු සහ ශනි මහ දශාවේදී උපරිම ප්‍රතිඵල ලැබේ.",
                    activationTimeEnglish = "Reaches pinnacle after age 36 and during Saturn Mahadasha."
                )
            )
        }

        // 6. Gajakesari Yoga (Jupiter in Kendra from Moon)
        val moon = posMap[Planet.MOON]
        if (jupiter != null && moon != null) {
            val distFromMoon = ((jupiter.sign.id - moon.sign.id + 12) % 12) + 1
            if (listOf(1, 4, 7, 10).contains(distFromMoon)) {
                yogas.add(
                    YogaInfo(
                        nameSinhala = "ගජකේසරි යෝගය",
                        nameEnglish = "Gajakesari Yoga",
                        categorySinhala = "රාජ යෝග",
                        categoryEnglish = "Raja Yoga",
                        planetsInvolved = listOf(Planet.JUPITER, Planet.MOON),
                        descriptionSinhala = "සඳුගෙන් කේන්ද්‍රස්ථානයක (1, 4, 7, 10) ගුරු ග්‍රහයා පිහිටීමෙන් සෑදෙන උතුම් රාජ යෝගයකි.",
                        descriptionEnglish = "Formed when Jupiter is placed in an angle (1st, 4th, 7th, 10th) from the Moon.",
                        futureImpactSinhala = "සිංහයෙකු සේ සතුරන් මැඩපැවැත්වීම, සමාජයේ නොසැලෙන ගෞරවය, කීර්තිය, ආර්ථික සමෘද්ධිය සහ තීක්ෂ්ණ ඥානය.",
                        futureImpactEnglish = "Lion-like fearlessness, defeating rivals, enduring societal prestige and immense wealth.",
                        activationTimeSinhala = "ගුරු සහ සඳු මහ දශා සහ අතුරු දශාවලදී බලවත් ජයග්‍රහණ ලබාදේ.",
                        activationTimeEnglish = "Yields triumphant breakthroughs during Jupiter and Moon periods."
                    )
                )
            }
        }

        // 7. Budhaditya Yoga (Sun + Mercury)
        val sun = posMap[Planet.SUN]
        if (sun != null && mercury != null && sun.house == mercury.house) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "බුධාදිත්‍ය යෝගය",
                    nameEnglish = "Budhaditya Yoga",
                    categorySinhala = "ඥාන / ධන යෝග",
                    categoryEnglish = "Intellectual & Wealth Yoga",
                    planetsInvolved = listOf(Planet.SUN, Planet.MERCURY),
                    descriptionSinhala = "රවි සහ බුධ එකම භාවයේ සංයෝග වීමෙන් ඇතිවන ප්‍රකට ඥාන යෝගයකි.",
                    descriptionEnglish = "Formed by the divine conjunction of Sun and Mercury in the same house.",
                    futureImpactSinhala = "විශිෂ්ට තර්ක ඥානය, විශ්ලේෂණ කුසලතාව, පරිපාලන සහ විභාග ජයග්‍රහණ, රාජ්‍ය අනුග්‍රහය.",
                    futureImpactEnglish = "Sharp analytical intellect, exam excellence, administrative acumen, royal favor.",
                    activationTimeSinhala = "අධ්‍යාපන අවධියේදී සහ රවි-බුධ දශාවන්හිදී මතුවෙයි.",
                    activationTimeEnglish = "Shines during educational pursuits and Sun-Mercury dashas."
                )
            )
        }

        // 8. Chandra-Mangala Yoga (Moon + Mars)
        if (moon != null && mars != null) {
            val dist = ((mars.sign.id - moon.sign.id + 12) % 12) + 1
            if (dist == 1 || dist == 7) {
                yogas.add(
                    YogaInfo(
                        nameSinhala = "චන්ද්‍ර මංගල යෝගය",
                        nameEnglish = "Chandra-Mangala Yoga",
                        categorySinhala = "ධන යෝග",
                        categoryEnglish = "Wealth & Enterprise Yoga",
                        planetsInvolved = listOf(Planet.MOON, Planet.MARS),
                        descriptionSinhala = "සඳු සහ කුජ එකට සිටීම හෝ එකිනෙකාට මුහුණට මුහුණ දැකීමෙන් නිර්මාණය වේ.",
                        descriptionEnglish = "Formed when Moon and Mars are conjoined or in mutual 7th aspect.",
                        futureImpactSinhala = "ව්‍යාපාරික දක්ෂතාව, නොපසුබට උත්සාහය, ඉඩකඩම් ගනුදෙනුවලින් ලැබෙන අතිමහත් ධනය.",
                        futureImpactEnglish = "Formidable entrepreneurial drive, real estate windfall, high wealth generation.",
                        activationTimeSinhala = "කුජ සහ සඳු දශා කාලයන්හිදී මූල්‍යමය සශ්‍රීකත්වය ගෙනදේ.",
                        activationTimeEnglish = "Manifests financial expansion in Moon and Mars periods."
                    )
                )
            }
        }

        // 9. Amala Yoga (Benefic in 10th house)
        val tenthHouse = houses.find { it.houseNumber == 10 }
        if (tenthHouse != null && tenthHouse.planetsInside.any { listOf(Planet.JUPITER, Planet.VENUS, Planet.MERCURY).contains(it.planet) }) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "අමල යෝගය",
                    nameEnglish = "Amala Yoga",
                    categorySinhala = "කීර්ති යෝග",
                    categoryEnglish = "Fame & Honor Yoga",
                    planetsInvolved = tenthHouse.planetsInside.map { it.planet },
                    descriptionSinhala = "10 වන කර්මස්ථානයේ ගුරු, සිකුරු හෝ බුධ වැනි ශුභ ග්‍රහයෙකු තැන්පත් වීම.",
                    descriptionEnglish = "Presence of natural benefics in the 10th house of career.",
                    futureImpactSinhala = "කැලැල් රහිත නිර්මල චරිතය, සමාජයේ අතිමහත් ගෞරවය, උසස් රැකියාව සහ කීර්තිය.",
                    futureImpactEnglish = "Unblemished moral repute, supreme occupational success, respected leadership.",
                    activationTimeSinhala = "වෘත්තීය ජීවිතයේ මධ්‍යම භාගයේදී සහ අදාළ ග්‍රහ දශාවලදී.",
                    activationTimeEnglish = "Surfaces prominently in mid-career and dasha periods."
                )
            )
        }

        // 10. Viparita Raja Yoga (Dusthana lords in Dusthana)
        val sixthLord = houses.find { it.houseNumber == 6 }?.lord
        val eighthLord = houses.find { it.houseNumber == 8 }?.lord
        val twelfthLord = houses.find { it.houseNumber == 12 }?.lord
        val dusthanaLords = listOfNotNull(sixthLord, eighthLord, twelfthLord)

        val anyDusthanaInDusthana = dusthanaLords.any { lord ->
            val p = posMap[lord]
            p != null && listOf(6, 8, 12).contains(p.house)
        }

        if (anyDusthanaInDusthana) {
            yogas.add(
                YogaInfo(
                    nameSinhala = "විපරීත රාජ යෝගය",
                    nameEnglish = "Viparita Raja Yoga",
                    categorySinhala = "අනපේක්ෂිත ජයග්‍රහණ",
                    categoryEnglish = "Sudden Triumph Yoga",
                    planetsInvolved = dusthanaLords.filter { lord ->
                        val p = posMap[lord]
                        p != null && listOf(6, 8, 12).contains(p.house)
                    },
                    descriptionSinhala = "6, 8, 12 යන අසුභ ස්ථානාධිපතියෙකු තවත් අසුභ ස්ථානයක තැන්පත් වීමෙන් නිර්මාණය වේ.",
                    descriptionEnglish = "Lords of difficult houses (6, 8, 12) placed inside 6, 8, or 12.",
                    futureImpactSinhala = "අනපේක්ෂිත විපත්ති සහ බාධක මධ්‍යයෙන් අහඹු ලෙස අතිවිශාල ජයග්‍රහණ හා ධනය ලැබීම.",
                    futureImpactEnglish = "Sudden elevation and wealth emerging out of unexpected twists and rivals' collapse.",
                    activationTimeSinhala = "දුෂ්කර කාල පරිච්ඡේද අවසානයේ අනපේක්ෂිත ලෙස හිමිවේ.",
                    activationTimeEnglish = "Unfolds abruptly at the conclusion of challenging dasha cycles."
                )
            )
        }

        // 11. Neechabhanga Raja Yoga
        val debPlanet = positions.find { it.dignity == Dignity.DEBILITATED }
        if (debPlanet != null) {
            val dispositor = getSignLord(debPlanet.sign)
            val dispPos = posMap[dispositor]
            if (dispPos != null && (listOf(1, 4, 7, 10).contains(dispPos.house) || dispPos.dignity == Dignity.EXALTED)) {
                yogas.add(
                    YogaInfo(
                        nameSinhala = "නීචභංග රාජ යෝගය",
                        nameEnglish = "Neechabhanga Raja Yoga",
                        categorySinhala = "රාජ යෝග",
                        categoryEnglish = "Raja Yoga",
                        planetsInvolved = listOf(debPlanet.planet, dispositor),
                        descriptionSinhala = "නීච වූ ${debPlanet.planet.sinhalaName} ග්‍රහයාගේ නීචත්වය භංග වී ප්‍රබල රාජ යෝගයක් බවට පත්වීම.",
                        descriptionEnglish = "Cancellation of debilitation of ${debPlanet.planet.englishName} transforming into a kingly yoga.",
                        futureImpactSinhala = "ජීවිතයේ මුල් අවධියේ අභියෝග පැවතුනද, පසුකාලීනව අන් සියල්ලන් අභිබවා ඉහළම තලයට පැමිණීම.",
                        futureImpactEnglish = "Rising from humble beginnings or early struggles to extraordinary summit and status.",
                        activationTimeSinhala = "ජීවිතයේ දෙවන භාගයේදී පූර්ණ ඵල ලබාදේ.",
                        activationTimeEnglish = "Delivers magnificent transformation in the second half of life."
                    )
                )
            }
        }

        // 12. Dhana Yoga (1st/2nd/5th/9th/11th lords combination)
        val secondLord = houses.find { it.houseNumber == 2 }?.lord
        val eleventhLord = houses.find { it.houseNumber == 11 }?.lord
        val ninthLord = houses.find { it.houseNumber == 9 }?.lord
        if (secondLord != null && eleventhLord != null) {
            val pos2 = posMap[secondLord]
            val pos11 = posMap[eleventhLord]
            if (pos2 != null && pos11 != null && (pos2.house == pos11.house || listOf(1, 2, 5, 9, 11).contains(pos2.house))) {
                yogas.add(
                    YogaInfo(
                        nameSinhala = "මහා ධන යෝගය",
                        nameEnglish = "Maha Dhana Yoga",
                        categorySinhala = "ධන යෝග",
                        categoryEnglish = "Wealth Yoga",
                        planetsInvolved = listOfNotNull(secondLord, eleventhLord, ninthLord).distinct(),
                        descriptionSinhala = "2 වන ධනස්ථානය සහ 11 වන ලාභස්ථානාධිපතියන්ගේ ශුභ සබඳතාව නිසා උදාවන ධන යෝගයකි.",
                        descriptionEnglish = "Powerful alliance between wealth houses (2nd & 11th).",
                        futureImpactSinhala = "නොසිඳෙන ධන ලාභ, විවිධ ආදායම් මාර්ග, ස්ථිර මූල්‍ය ස්ථාවරත්වය සහ පවුල් සශ්‍රීකත්වය.",
                        futureImpactEnglish = "Continuous influx of prosperity, multiple income streams, financial independence.",
                        activationTimeSinhala = "2 සහ 11 අධිපති ග්‍රහයන්ගේ දශා අන්තර් දශා කාලවලදී.",
                        activationTimeEnglish = "Activates during major financial dasha periods."
                    )
                )
            }
        }

        return yogas
    }

    private fun calculateVimshottariDasha(
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
        val fractionRemaining = (1.0 - fractionElapsed).coerceIn(0.01, 1.0)

        val dashaOrder = listOf(
            Planet.KETU, Planet.VENUS, Planet.SUN, Planet.MOON,
            Planet.MARS, Planet.RAHU, Planet.JUPITER, Planet.SATURN, Planet.MERCURY
        )

        val birthNak = Nakshatra.entries[nakIndex]
        val startPlanet = birthNak.dashaLord
        val startIndex = dashaOrder.indexOf(startPlanet).coerceAtLeast(0)

        val periods = mutableListOf<DashaPeriod>()
        val birthCal = Calendar.getInstance()
        birthCal.set(birthYear, birthMonth - 1, birthDay)

        var currentCal = birthCal.clone() as Calendar

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (i in 0 until 9) {
            val planet = dashaOrder[(startIndex + i) % 9]
            val durationYears = if (i == 0) {
                planet.dashaYears * fractionRemaining
            } else {
                planet.dashaYears.toDouble()
            }

            val startYear = currentCal.get(Calendar.YEAR)
            val startMonth = currentCal.get(Calendar.MONTH) + 1
            val startDay = currentCal.get(Calendar.DAY_OF_MONTH)
            val startFormatted = String.format(Locale.US, "%04d-%02d-%02d", startYear, startMonth, startDay)

            val daysToAdd = (durationYears * 365.25).toInt()
            val endCal = currentCal.clone() as Calendar
            endCal.add(Calendar.DAY_OF_YEAR, daysToAdd)

            val endYear = endCal.get(Calendar.YEAR)
            val endMonth = endCal.get(Calendar.MONTH) + 1
            val endDay = endCal.get(Calendar.DAY_OF_MONTH)
            val endFormatted = String.format(Locale.US, "%04d-%02d-%02d", endYear, endMonth, endDay)

            val isCurrent = startYear <= currentYear && endYear >= currentYear

            val (foreSin, foreEng) = getDashaForecast(planet)

            periods.add(
                DashaPeriod(
                    planet = planet,
                    startYear = startYear,
                    endYear = endYear,
                    startDateStr = startFormatted,
                    endDateStr = endFormatted,
                    durationYears = durationYears,
                    isCurrent = isCurrent,
                    forecastSinhala = foreSin,
                    forecastEnglish = foreEng
                )
            )

            currentCal = endCal
        }

        return periods
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

    private fun generateMarriagePrediction(
        lagnaSign: ZodiacSign,
        houses: List<HouseInfo>,
        positions: List<PlanetPosition>,
        birthYear: Int,
        gender: String
    ): MarriagePrediction {
        val seventhHouse = houses.find { it.houseNumber == 7 } ?: houses[6]
        val seventhLord = seventhHouse.lord
        val seventhLordPos = positions.find { it.planet == seventhLord }
        val seventhLordHouse = seventhLordPos?.house ?: 7
        val planetsIn7 = seventhHouse.planetsInside.map { it.planet }

        val marsPos = positions.find { it.planet == Planet.MARS }
        val hasKujaDosha = marsPos != null && listOf(1, 2, 4, 7, 8, 12).contains(marsPos.house)
        val isCancelled = hasKujaDosha && (
                marsPos?.sign == ZodiacSign.ARIES || marsPos?.sign == ZodiacSign.SCORPIO ||
                marsPos?.sign == ZodiacSign.CAPRICORN ||
                positions.find { it.planet == Planet.JUPITER }?.house == marsPos?.house
        )

        val kujaRemedySin = when {
            !hasKujaDosha -> "කේන්ද්‍රයේ කුජ දෝෂයක් නොමැත. විවාහය සඳහා ඉතා ශුභදායක පිහිටීමකි."
            isCancelled -> "කුජ දෝෂය පැවතියද එය ස්වක්ෂේත්‍ර/ගුරු දෘෂ්ටි බලයෙන් භංග වී ඇති බැවින් දෝෂ බලපෑමක් නැත."
            else -> "කුජ දෝෂය පවතින බැවින් සමාන කුජ බලයක් ඇති සහකරුවෙකු/සහකාරියක තෝරාගැනීම සහ ශ්‍රී මුරුගන් (කතරගම දෙවිඳුන්) වැඳීම යහපත්ය."
        }

        val kujaRemedyEng = when {
            !hasKujaDosha -> "No Kuja (Manglik) Dosha present. Auspicious indicator for marital harmony."
            isCancelled -> "Kuja Dosha is present but successfully cancelled via dignity or Jupiter's aspect."
            else -> "Kuja Dosha active; matching with a compatible Mars chart and Murugan prayers recommended."
        }

        // Spouse personality
        val spousePersonalitySin = when {
            planetsIn7.contains(Planet.JUPITER) -> "ධර්මිෂ්ඨ, උගත්, ගුණගරුක සහ ආගමානුකූල ශ්‍රේෂ්ඨ චරිතයක් හිමි සහකරුවෙකු/සහකාරියක ලැබේ."
            planetsIn7.contains(Planet.VENUS) -> "අතිශය රූපසෝභාවෙන් පිරි, කලාකාමී, මිහිරි කතාබහෙන් යුත් ආදරණීය සහකරුවෙකු/සහකාරියක හිමිවේ."
            planetsIn7.contains(Planet.MERCURY) -> "තරුණ පෙනුමැති, බුද්ධිමත්, සමාජශීලී, හාස්‍ය රසය දන්නා කඩිසර සහකරුවෙකු හිමිවේ."
            planetsIn7.contains(Planet.SUN) -> "ගෞරවනීය පවුලකින් පැවත එන, ආත්ම අභිමානය සහ පාලන බලය සහිත ප්‍රභූ සහකරුවෙකි."
            planetsIn7.contains(Planet.MARS) -> "ක්‍රියාශීලී, ධෛර්යවන්ත, ඉක්මන් තීරණ ගන්නා, එඩිතර පෞරුෂයකින් යුත් සහකරුවෙකි."
            planetsIn7.contains(Planet.SATURN) -> "පරිණත, වගකීම් සහගත, ඉවසීමෙන් කටයුතු කරන, විශ්වාසවන්ත සහකරුවෙකි."
            else -> "${seventhHouse.sign.sinhalaName} රාශියේ ගුණාංග අනුව සහකරු මනා ගතිපැවතුම් ඇති, පවුලට ආදරය කරන අයෙකි."
        }

        val spousePersonalityEng = when {
            planetsIn7.contains(Planet.JUPITER) -> "Virtuous, learned, noble-hearted and spiritually oriented partner."
            planetsIn7.contains(Planet.VENUS) -> "Charming, artistic, affectionate and aesthetically pleasing spouse."
            planetsIn7.contains(Planet.MERCURY) -> "Youthful, intellectual, witty, communicative and sociable companion."
            planetsIn7.contains(Planet.SUN) -> "Dignified, principled, authoritative with high family prestige."
            planetsIn7.contains(Planet.MARS) -> "Dynamic, energetic, daring, ambitious and assertive personality."
            planetsIn7.contains(Planet.SATURN) -> "Mature, disciplined, dutiful, loyal and realistic life partner."
            else -> "Guided by ${seventhHouse.sign.englishName}, loyal, family-focused and emotionally supportive."
        }

        val spouseAppearanceSin = when (seventhHouse.sign) {
            ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS -> "දීප්තිමත් දෑස්, මලල ක්‍රීඩකයෙකු බඳු ශක්තිමත් කඩවසම් සිරුර, ප්‍රසන්න තේජවන්ත පෙනුම."
            ZodiacSign.TAURUS, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN -> "සමබර පෙනුම, පැහැපත් සම, ආකර්ෂණීය මුහුණුවර සහ චාම් ප්‍රසන්න විලාසිතා."
            ZodiacSign.GEMINI, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS -> "උස සිරුර, සිනහමුසු මුහුණ, ආකර්ෂණීය සිනහව සහ සුහදශීලී පෙනුම."
            ZodiacSign.CANCER, ZodiacSign.SCORPIO, ZodiacSign.PISCES -> "සෞම්‍ය සුන්දර මුහුණුවර, ගැඹුරු ප්‍රකාශන සහිත දෑස්, සංවේදී ප්‍රියමනාප රූපය."
        }

        val spouseAppearanceEng = when (seventhHouse.sign) {
            ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS -> "Radiant eyes, athletic build, charismatic and commanding presence."
            ZodiacSign.TAURUS, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN -> "Well-proportioned features, glowing complexion, elegant and poised charm."
            ZodiacSign.GEMINI, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS -> "Tall stature, bright smile, expressive demeanor and polished carriage."
            ZodiacSign.CANCER, ZodiacSign.SCORPIO, ZodiacSign.PISCES -> "Gentle graceful facial symmetry, magnetic expressive eyes, calm beauty."
        }

        val spouseProfessionSin = when (seventhLord) {
            Planet.SUN -> "රාජ්‍ය පරිපාලනය, දේශපාලනය, වෛද්‍ය ක්ෂේත්‍රය හෝ කළමනාකරණය."
            Planet.MOON -> "සත්කාරක සේවාවන්, මහජන සබඳතා, මනෝවිද්‍යාව හෝ ව්‍යාපාර."
            Planet.MARS -> "ඉංජිනේරු විද්‍යාව, ආරක්ෂක අංශ, ඉදිකිරීම් හෝ ව්‍යවසායකත්වය."
            Planet.MERCURY -> "ගිණුම්කරණය, තොරතුරු තාක්ෂණය, ලේඛන කලාව, නීතිය හෝ වෙළඳාම."
            Planet.JUPITER -> "නීතිඥ, ආචාර්ය, බැංකු හා මූල්‍ය, උපදේශන හෝ අධ්‍යාපන ක්ෂේත්‍රය."
            Planet.VENUS -> "මෝස්තර නිර්මාණය, කලාව, සිනමාව, සංචාරක හෝ මූල්‍ය කළමනාකරණය."
            Planet.SATURN -> "කාර්මික, නිෂ්පාදන, කෘෂිකාර්මික, ඉංජිනේරු හෝ පරිපාලන අංශය."
            Planet.RAHU, Planet.KETU -> "තොරතුරු තාක්ෂණය, විදේශ සේවා හෝ පර්යේෂණ ක්ෂේත්‍රය."
        }

        val spouseProfessionEng = when (seventhLord) {
            Planet.SUN -> "Government administration, medicine, public leadership, or executive directorship."
            Planet.MOON -> "Hospitality, public relations, healthcare, nutrition, or creative commerce."
            Planet.MARS -> "Engineering, defense forces, real estate, architecture, or dynamic entrepreneurship."
            Planet.MERCURY -> "Finance, software engineering, journalism, commerce, or analytical advisory."
            Planet.JUPITER -> "Law, professorship, banking, advisory counseling, or educational leadership."
            Planet.VENUS -> "Design, performing arts, luxury branding, hospitality, or corporate finance."
            Planet.SATURN -> "Heavy industry, supply chain, infrastructure development, or civil administration."
            Planet.RAHU, Planet.KETU -> "Information technology, international trade, or specialized research."
        }

        val maritalHarmonySin = if (seventhLordHouse in listOf(1, 4, 5, 7, 9, 10, 11)) {
            "විවාහ දිවිය අතිශයින් සාමකාමී සහ දෙදෙනා අතර මනා අන්‍යෝන්‍ය අවබෝධයක් සහිත සතුටුදායක එකක් වේ."
        } else {
            "සුළු මතභේද ඇතිවිය හැකි බැවින් අන්‍යෝන්‍ය ගෞරවය සහ ඉවසීම පවත්වා ගැනීමෙන් සාර්ථක විවාහ දිවියක් ගොඩනැගේ."
        }

        val maritalHarmonyEng = if (seventhLordHouse in listOf(1, 4, 5, 7, 9, 10, 11)) {
            "Marital union will be harmonious, deeply rooted in mutual affection, respect and joint prosperity."
        } else {
            "Minor occasional differences require open dialogue and emotional patience to sustain lasting bliss."
        }

        val primaryAgeStart = if (seventhLord == Planet.SATURN) 27 else 24
        val primaryAgeEnd = primaryAgeStart + 3
        val secondaryAgeStart = primaryAgeEnd + 1
        val secondaryAgeEnd = secondaryAgeStart + 3

        val primaryYearStart = birthYear + primaryAgeStart
        val primaryYearEnd = birthYear + primaryAgeEnd
        val secondaryYearStart = birthYear + secondaryAgeStart
        val secondaryYearEnd = birthYear + secondaryAgeEnd

        val ageWindowSin = "ප්‍රධාන විවාහ වයස් සීමාව: අවුරුදු $primaryAgeStart - $primaryAgeEnd අතර (දෙවන සුබ අවස්ථාව: අවුරුදු $secondaryAgeStart - $secondaryAgeEnd)."
        val ageWindowEng = "Primary Marriage Age: $primaryAgeStart - $primaryAgeEnd years (Secondary auspicious window: $secondaryAgeStart - $secondaryAgeEnd years)."

        val favorableDatesSin = "සුබ වකවානු: $primaryYearStart සිට $primaryYearEnd දක්වා කාලය තුළ සහ $secondaryYearStart - $secondaryYearEnd වසරවල ගුරු/සිකුරු ගෝචරය තුළදී මංගල යෝග උදාවේ."
        val favorableDatesEng = "Favorable Timelines: Between years $primaryYearStart - $primaryYearEnd, and $secondaryYearStart - $secondaryYearEnd activated under Jupiter/Venus transits."

        return MarriagePrediction(
            seventhHouseSign = seventhHouse.sign,
            seventhLord = seventhLord,
            seventhLordHouse = seventhLordHouse,
            planetsInSeventh = planetsIn7,
            hasKujaDosha = hasKujaDosha,
            isKujaDoshaCancelled = isCancelled,
            kujaDoshaRemedySinhala = kujaRemedySin,
            kujaDoshaRemedyEnglish = kujaRemedyEng,
            spousePersonalitySinhala = spousePersonalitySin,
            spousePersonalityEnglish = spousePersonalityEng,
            spouseAppearanceSinhala = spouseAppearanceSin,
            spouseAppearanceEnglish = spouseAppearanceEng,
            spouseProfessionSinhala = spouseProfessionSin,
            spouseProfessionEnglish = spouseProfessionEng,
            maritalHarmonySinhala = maritalHarmonySin,
            maritalHarmonyEnglish = maritalHarmonyEng,
            marriageAgeWindowsSinhala = ageWindowSin,
            marriageAgeWindowsEnglish = ageWindowEng,
            marriageFavorableDatesSinhala = favorableDatesSin,
            marriageFavorableDatesEnglish = favorableDatesEng
        )
    }

    private fun generateChildrenPrediction(
        lagnaSign: ZodiacSign,
        houses: List<HouseInfo>,
        positions: List<PlanetPosition>,
        birthYear: Int
    ): ChildrenPrediction {
        val fifthHouse = houses.find { it.houseNumber == 5 } ?: houses[4]
        val fifthLord = fifthHouse.lord
        val fifthLordPos = positions.find { it.planet == fifthLord }
        val fifthLordHouse = fifthLordPos?.house ?: 5
        val planetsIn5 = fifthHouse.planetsInside.map { it.planet }

        val jupiterPos = positions.find { it.planet == Planet.JUPITER }
        val jupDignity = jupiterPos?.dignity ?: Dignity.NEUTRAL

        val countSin = when {
            jupDignity == Dignity.EXALTED || fifthLordPos?.dignity == Dignity.EXALTED -> "වාසනාවන්ත දරු සම්පතක් හිමිවේ (දෙදෙනෙකු හෝ තිදෙනෙකු පිළිබඳ පැහැදිලි සලකුණු පවතී)."
            planetsIn5.contains(Planet.JUPITER) || planetsIn5.contains(Planet.VENUS) -> "යහපත් දරු සම්පත් හිමිවන අතර දරුවන් මවුපියන්ට ආශිර්වාදයක් වේ."
            planetsIn5.contains(Planet.SATURN) || planetsIn5.contains(Planet.RAHU) -> "දරු සම්පත තරමක් ප්‍රමාද වී හෝ වැඩිහිටි වයසේදී හිමිවීමේ ඉඩකඩ ඇතත් සතුට උදාවේ."
            else -> "යහපත් නිරෝගී දරුවන් දෙදෙනෙකුගේ ආශිර්වාදය හිමිවන කේන්ද්‍රයකි."
        }

        val countEng = when {
            jupDignity == Dignity.EXALTED || fifthLordPos?.dignity == Dignity.EXALTED -> "Blessed with abundant auspicious progeny (indications of 2 to 3 virtuous children)."
            planetsIn5.contains(Planet.JUPITER) || planetsIn5.contains(Planet.VENUS) -> "Fortunate children who will bring high joy and honor to the family lineage."
            planetsIn5.contains(Planet.SATURN) || planetsIn5.contains(Planet.RAHU) -> "Parenthood may arrive slightly later, bringing mature delight and deep bonding."
            else -> "Auspicious indicators for two healthy, progressive and dutiful children."
        }

        val natureSin = "දරුවන් අධ්‍යාපනයට දක්ෂ, ගුණගරුක, දෙමාපියන්ට කීකරු සහ සමාජයේ පිළිගැනීමක් ලබන දරුවන් බවට පත්වේ. විශේෂයෙන් කුසලතා පූර්ණ අයෙකු බිහිවේ."
        val natureEng = "Children will possess fine intellectual acuity, ethical uprightness, and earn societal distinction in academia or public leadership."

        val favAgeStart = 26
        val favAgeEnd = 31
        val favYearStart = birthYear + favAgeStart
        val favYearEnd = birthYear + favAgeEnd
        val secYearStart = birthYear + 32
        val secYearEnd = birthYear + 36

        val periodSin = "දරු සම්පත් සඳහා වඩාත්ම ශුභදායක වයස් සීමාව: අවුරුදු $favAgeStart - $favAgeEnd අතර ($favYearStart - $favYearEnd වකවානුව). දෙවන සුබ අවස්ථාව: $secYearStart - $secYearEnd."
        val periodEng = "Most favorable parental window: Age $favAgeStart - $favAgeEnd (Years $favYearStart - $favYearEnd). Secondary favorable span: $secYearStart - $secYearEnd."

        val guidanceSin = "ගුරු ග්‍රහයා සහ 5 වැන්නාධිපති ශක්තිමත් බැවින් පින්වත් දරු උපත් සිදුවේ. ගර්භණී සමයේදී සෙත් කවි හා අංගුලිමාල පිරිත ශ්‍රවණය සුබයි."
        val guidanceEng = "Strong Jupiter and 5th house ensure noble progeny. Recitation of protective stanzas and positive mindset bring joyful parenthood."

        return ChildrenPrediction(
            fifthHouseSign = fifthHouse.sign,
            fifthLord = fifthLord,
            fifthLordHouse = fifthLordHouse,
            planetsInFifth = planetsIn5,
            jupiterDignity = jupDignity,
            childrenCountIndicationSinhala = countSin,
            childrenCountIndicationEnglish = countEng,
            childrenNatureSinhala = natureSin,
            childrenNatureEnglish = natureEng,
            favorablePeriodSinhala = periodSin,
            favorablePeriodEnglish = periodEng,
            astrologicalGuidanceSinhala = guidanceSin,
            astrologicalGuidanceEnglish = guidanceEng
        )
    }

    private fun generateCareerPrediction(
        lagnaSign: ZodiacSign,
        houses: List<HouseInfo>,
        positions: List<PlanetPosition>
    ): CareerPrediction {
        val tenthHouse = houses.find { it.houseNumber == 10 } ?: houses[9]
        val tenthLord = tenthHouse.lord
        val tenthLordPos = positions.find { it.planet == tenthLord }
        val tenthLordHouse = tenthLordPos?.house ?: 10
        val planetsIn10 = tenthHouse.planetsInside.map { it.planet }

        val fieldsSin = when (tenthLord) {
            Planet.SUN -> "රාජ්‍ය පරිපාලනය, දේශපාලනය, වෛද්‍ය ක්ෂේත්‍රය, විධායක නිලතල හෝ අධ්‍යක්ෂක ධූර."
            Planet.MOON -> "සන්නිවේදනය, හෝටල් හා සංචාරක, මහජන සබඳතා, මනෝවිද්‍යාව, ද්‍රව ආශ්‍රිත ව්‍යාපාර."
            Planet.MARS -> "ඉංජිනේරු, ඉදිකිරීම්, ආරක්ෂක අංශ, ශල්‍ය වෛද්‍ය, යන්ත්‍ර සූත්‍ර හෝ දේපළ වෙළඳාම."
            Planet.MERCURY -> "තොරතුරු තාක්ෂණය, බැංකු හා මූල්‍ය, ගණකාධිකාරී, මාධ්‍ය, ලේඛන හෝ නීති ක්ෂේත්‍රය."
            Planet.JUPITER -> "අධ්‍යාපන ක්ෂේත්‍රය, මහාචාර්ය, නීතිය, විනිසුරු, ආගමික හා උපදේශන ආයතන."
            Planet.VENUS -> "කලාව, විලාසිතා, සිනමාව, සුඛෝපභෝගී භාණ්ඩ වෙළඳාම, වාස්තු විද්‍යාව හෝ සංචාරක."
            Planet.SATURN -> "කාර්මික, කම්කරු නායකත්ව, කෘෂිකර්ම, යකඩ හා ඛනිජ, මහා පරිමාණ නිෂ්පාදන."
            Planet.RAHU, Planet.KETU -> "විදේශ රටවල රැකියා, ගුවන් සේවා, පර්යේෂණ හෝ මෘදුකාංග සංවර්ධනය."
        }

        val fieldsEng = when (tenthLord) {
            Planet.SUN -> "Government services, executive administration, corporate leadership, medicine."
            Planet.MOON -> "Public relations, psychology, hospitality, maritime trade, food & nutrition."
            Planet.MARS -> "Civil/mechanical engineering, defense leadership, real estate development."
            Planet.MERCURY -> "Software engineering, data analytics, financial auditing, media, journalism."
            Planet.JUPITER -> "Higher education, jurisprudence, judiciary, banking, ethical advisory."
            Planet.VENUS -> "Creative arts, cinema, architecture, luxury branding, diplomatic services."
            Planet.SATURN -> "Infrastructure, heavy manufacturing, mining, agriculture, public governance."
            Planet.RAHU, Planet.KETU -> "Foreign multinational corporations, aviation, research, digital technology."
        }

        val wealthSin = "11 වන අයස්ථානය සහ 2 වන ධනස්ථානය ශක්තිමත්ව පිහිටා ඇති බැවින් ජීවිතයේ මුල් භාගයට වඩා මැදිවියෙන් පසු ස්ථාවර මූල්‍ය ශක්තියක් ගොඩනැගේ."
        val wealthEng = "Well-fortified 2nd and 11th houses promise enduring financial stability and escalating assets especially post mid-30s."

        return CareerPrediction(
            tenthHouseSign = tenthHouse.sign,
            tenthLord = tenthLord,
            tenthLordHouse = tenthLordHouse,
            planetsInTenth = planetsIn10,
            suitableFieldsSinhala = fieldsSin,
            suitableFieldsEnglish = fieldsEng,
            wealthProspectsSinhala = wealthSin,
            wealthProspectsEnglish = wealthEng
        )
    }

    private fun generateSummarySinhala(
        lagna: ZodiacSign,
        moonSign: ZodiacSign,
        moonNak: Nakshatra,
        yogas: List<YogaInfo>
    ): String {
        val yogaNames = if (yogas.isNotEmpty()) yogas.take(3).joinToString(", ") { it.nameSinhala } else "ශුභ ග්‍රහ පිහිටීම්"
        return "ඔබගේ ලග්නය ${lagna.sinhalaName} වන අතර ජන්ම රාශිය ${moonSign.sinhalaName} වේ. උපන් නැකත ${moonNak.sinhalaName} නැකතයි. කේන්ද්‍රයේ $yogaNames වැනි බලවත් යෝග පිහිටා ඇති බැවින් ජීවිතයේ ඉහළ දියුණුවක් සහ සමාජයේ පිළිගැනීමක් ලැබෙනු ඇත."
    }

    private fun generateSummaryEnglish(
        lagna: ZodiacSign,
        moonSign: ZodiacSign,
        moonNak: Nakshatra,
        yogas: List<YogaInfo>
    ): String {
        val yogaNames = if (yogas.isNotEmpty()) yogas.take(3).joinToString(", ") { it.nameEnglish } else "favorable planetary alignments"
        return "Ascendant is in ${lagna.englishName} and Moon sign is ${moonSign.englishName} under ${moonNak.englishName} Nakshatra. Fortified with $yogaNames, this chart indicates steady rise in prestige, resilience, and lasting accomplishment."
    }
}
