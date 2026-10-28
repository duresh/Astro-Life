package com.example.data.model

/**
 * 12 Rashis (Zodiac Signs) in Vedic Astrology
 */
enum class ZodiacSign(
    val id: Int,
    val sinhalaName: String,
    val sinhalaShort: String,
    val englishName: String,
    val sanskritName: String,
    val lordPlanetSinhala: String,
    val lordPlanetEnglish: String,
    val elementSinhala: String,
    val elementEnglish: String,
    val symbolSinhala: String,
    val symbolEnglish: String
) {
    ARIES(0, "මේෂ", "මේ", "Aries", "Mesha", "කුජ", "Mars", "ගිනි", "Fire", "එළුවා", "Ram"),
    TAURUS(1, "වෘෂභ", "වෘ", "Taurus", "Vrishabha", "සිකුරු", "Venus", "පඨවි", "Earth", "ගවයා", "Bull"),
    GEMINI(2, "මිථුන", "මි", "Gemini", "Mithuna", "බුධ", "Mercury", "වායු", "Air", "යුගලය", "Twins"),
    CANCER(3, "කටක", "ක", "Cancer", "Kataka", "සඳු", "Moon", "ජල", "Water", "කකුළුවා", "Crab"),
    LEO(4, "සිංහ", "සිං", "Leo", "Simha", "රවි", "Sun", "ගිනි", "Fire", "සිංහයා", "Lion"),
    VIRGO(5, "කන්‍යා", "කන්", "Virgo", "Kanya", "බුධ", "Mercury", "පඨවි", "Earth", "කන්‍යාව", "Virgin"),
    LIBRA(6, "තුලා", "තු", "Libra", "Thula", "සිකුරු", "Venus", "වායු", "Air", "තරාදිය", "Scales"),
    SCORPIO(7, "වෘශ්චික", "වෘශ්", "Scorpio", "Vrischika", "කුජ", "Mars", "ජල", "Water", "ගෝනුස්සා", "Scorpion"),
    SAGITTARIUS(8, "ධනු", "ධ", "Sagittarius", "Dhanu", "ගුරු", "Jupiter", "ගිනි", "Fire", "දුන්න", "Bow"),
    CAPRICORN(9, "මකර", "ම", "Capricorn", "Makara", "ශනි", "Saturn", "පඨවි", "Earth", "මකරා", "Sea-Goat"),
    AQUARIUS(10, "කුම්භ", "කු", "Aquarius", "Kumbha", "ශනි", "Saturn", "වායු", "Air", "කලස", "Water-Bearer"),
    PISCES(11, "මීන", "මී", "Pisces", "Meena", "ගුරු", "Jupiter", "ජල", "Water", "මත්ස්‍ය", "Fishes");

    companion object {
        fun fromId(id: Int): ZodiacSign = entries[(id % 12 + 12) % 12]
        fun fromLongitude(deg: Double): ZodiacSign {
            val norm = (deg % 360.0 + 360.0) % 360.0
            val idx = (norm / 30.0).toInt().coerceIn(0, 11)
            return fromId(idx)
        }
    }
}
