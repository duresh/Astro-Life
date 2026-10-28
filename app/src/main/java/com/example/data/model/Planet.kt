package com.example.data.model

/**
 * 9 Grahas (Planets) in Vedic Astrology (නවාග්‍රහයින්)
 */
enum class Planet(
    val sinhalaName: String,
    val sinhalaShort: String,
    val englishName: String,
    val englishShort: String,
    val exaltedSign: ZodiacSign,
    val debilitatedSign: ZodiacSign,
    val ownSigns: List<ZodiacSign>,
    val dashaYears: Int,
    val natureSinhala: String,
    val natureEnglish: String
) {
    SUN(
        "රවි", "ර", "Sun", "Su",
        ZodiacSign.ARIES, ZodiacSign.LIBRA, listOf(ZodiacSign.LEO),
        6, "ක්‍රූර / ආත්මකාරක", "Malefic / Soul Karaka"
    ),
    MOON(
        "සඳු", "ස", "Moon", "Mo",
        ZodiacSign.TAURUS, ZodiacSign.SCORPIO, listOf(ZodiacSign.CANCER),
        10, "සෞම්‍ය / මාතෘකාරක", "Benefic / Mind Karaka"
    ),
    MARS(
        "කුජ", "කු", "Mars", "Ma",
        ZodiacSign.CAPRICORN, ZodiacSign.CANCER, listOf(ZodiacSign.ARIES, ZodiacSign.SCORPIO),
        7, "පාපී / භූමිකාරක", "Malefic / Energy & Courage"
    ),
    MERCURY(
        "බුධ", "බු", "Mercury", "Me",
        ZodiacSign.VIRGO, ZodiacSign.PISCES, listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO),
        17, "සෞම්‍ය / බුද්ධිකාරක", "Benefic / Intellect Karaka"
    ),
    JUPITER(
        "ගුරු", "ගු", "Jupiter", "Ju",
        ZodiacSign.CANCER, ZodiacSign.CAPRICORN, listOf(ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES),
        16, "ශුභ / පුත්‍ර හා ඥානකාරක", "Great Benefic / Wisdom & Children"
    ),
    VENUS(
        "සිකුරු", "සි", "Venus", "Ve",
        ZodiacSign.PISCES, ZodiacSign.VIRGO, listOf(ZodiacSign.TAURUS, ZodiacSign.LIBRA),
        20, "ශුභ / කලත්‍රකාරක", "Benefic / Spouse & Luxury"
    ),
    SATURN(
        "ශනි", "ශ", "Saturn", "Sa",
        ZodiacSign.LIBRA, ZodiacSign.ARIES, listOf(ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS),
        19, "පාපී / ආයුෂ්කාරක", "Malefic / Longevity & Discipline"
    ),
    RAHU(
        "රාහු", "රා", "Rahu", "Ra",
        ZodiacSign.TAURUS, ZodiacSign.SCORPIO, listOf(ZodiacSign.AQUARIUS),
        18, "පාපී / ඡායාග්‍රහ", "Shadow / Desires & Material"
    ),
    KETU(
        "කේතු", "කේ", "Ketu", "Ke",
        ZodiacSign.SCORPIO, ZodiacSign.TAURUS, listOf(ZodiacSign.SCORPIO),
        7, "පාපී / මෝක්ෂකාරක", "Shadow / Liberation & Detachment"
    );

    fun getDignity(currentSign: ZodiacSign): Dignity {
        return when {
            currentSign == exaltedSign -> Dignity.EXALTED
            currentSign == debilitatedSign -> Dignity.DEBILITATED
            ownSigns.contains(currentSign) -> Dignity.OWN_SIGN
            isFriendSign(currentSign) -> Dignity.FRIEND
            isEnemySign(currentSign) -> Dignity.ENEMY
            else -> Dignity.NEUTRAL
        }
    }

    private fun isFriendSign(sign: ZodiacSign): Boolean {
        return when (this) {
            SUN -> listOf(ZodiacSign.ARIES, ZodiacSign.SCORPIO, ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES, ZodiacSign.CANCER).contains(sign)
            MOON -> listOf(ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.GEMINI, ZodiacSign.VIRGO).contains(sign)
            MARS -> listOf(ZodiacSign.LEO, ZodiacSign.CANCER, ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES).contains(sign)
            MERCURY -> listOf(ZodiacSign.TAURUS, ZodiacSign.LIBRA, ZodiacSign.LEO).contains(sign)
            JUPITER -> listOf(ZodiacSign.ARIES, ZodiacSign.SCORPIO, ZodiacSign.LEO, ZodiacSign.CANCER).contains(sign)
            VENUS -> listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS).contains(sign)
            SATURN -> listOf(ZodiacSign.TAURUS, ZodiacSign.LIBRA, ZodiacSign.GEMINI, ZodiacSign.VIRGO).contains(sign)
            RAHU, KETU -> listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO, ZodiacSign.LIBRA, ZodiacSign.CAPRICORN).contains(sign)
        }
    }

    private fun isEnemySign(sign: ZodiacSign): Boolean {
        return when (this) {
            SUN -> listOf(ZodiacSign.TAURUS, ZodiacSign.LIBRA, ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS).contains(sign)
            MOON -> listOf(ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS).contains(sign)
            MARS -> listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO).contains(sign)
            MERCURY -> listOf(ZodiacSign.CANCER).contains(sign)
            JUPITER -> listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO, ZodiacSign.TAURUS, ZodiacSign.LIBRA).contains(sign)
            VENUS -> listOf(ZodiacSign.CANCER, ZodiacSign.LEO).contains(sign)
            SATURN -> listOf(ZodiacSign.CANCER, ZodiacSign.LEO, ZodiacSign.SCORPIO).contains(sign)
            RAHU, KETU -> listOf(ZodiacSign.CANCER, ZodiacSign.LEO).contains(sign)
        }
    }
}

enum class Dignity(val sinhalaName: String, val englishName: String) {
    EXALTED("උච්ච", "Exalted"),
    OWN_SIGN("ස්වක්ෂේත්‍ර", "Own Sign"),
    FRIEND("මිත්‍ර", "Friendly"),
    NEUTRAL("සම", "Neutral"),
    ENEMY("සතුරු", "Enemy"),
    DEBILITATED("නීච", "Debilitated")
}
