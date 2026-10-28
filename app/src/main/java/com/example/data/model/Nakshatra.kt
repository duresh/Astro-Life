package com.example.data.model

/**
 * 27 Nakshatras (Lunar Mansions - නැකත්)
 */
enum class Nakshatra(
    val id: Int,
    val sinhalaName: String,
    val englishName: String,
    val dashaLord: Planet
) {
    ASWIDA(0, "අස්විද", "Ashwini", Planet.KETU),
    BERANA(1, "බෙරණ", "Bharani", Planet.VENUS),
    KETHI(2, "කැති", "Krittika", Planet.SUN),
    REHENA(3, "රෙහෙණ", "Rohini", Planet.MOON),
    MUWASIRASA(4, "මුවසිරස", "Mrigashira", Planet.MARS),
    ADA(5, "අද", "Ardra", Planet.RAHU),
    PUNAVASA(6, "පුනාවස", "Punarvasu", Planet.JUPITER),
    PUSA(7, "පුෂ", "Pushya", Planet.SATURN),
    ASLISA(8, "අස්ලිස", "Ashlesha", Planet.MERCURY),
    MA(9, "මා", "Magha", Planet.KETU),
    PUVAPAL(10, "පුවපල්", "Purva Phalguni", Planet.VENUS),
    UTHRAPAL(11, "උත්‍රපල්", "Uttara Phalguni", Planet.SUN),
    HATHA(12, "හත", "Hasta", Planet.MOON),
    SITHA(13, "සිත", "Chitra", Planet.MARS),
    SA(14, "සා", "Swati", Planet.RAHU),
    VISA(15, "විසා", "Vishakha", Planet.JUPITER),
    ANURA(16, "අනුර", "Anuradha", Planet.SATURN),
    DETA(17, "දෙට", "Jyeshtha", Planet.MERCURY),
    MULA(18, "මුල", "Mula", Planet.KETU),
    PUVAPALA(19, "පුවසල", "Purva Ashadha", Planet.VENUS),
    UTHRASALA(20, "උත්‍රසල", "Uttara Ashadha", Planet.SUN),
    SUWANA(21, "සුවණ", "Shravana", Planet.MOON),
    DENATHA(22, "දෙනට", "Dhanishta", Planet.MARS),
    SIYAWASA(23, "සියාවස", "Shatabhisha", Planet.RAHU),
    PUVAPUTUPA(24, "පුවපුටුප", "Purva Bhadrapada", Planet.JUPITER),
    UTHRAPUTUPA(25, "උත්‍රපුටුප", "Uttara Bhadrapada", Planet.SATURN),
    REVATHI(26, "රේවතී", "Revati", Planet.MERCURY);

    companion object {
        fun fromLongitude(deg: Double): Pair<Nakshatra, Int> {
            val norm = (deg % 360.0 + 360.0) % 360.0
            val span = 360.0 / 27.0 // 13.333333 degrees
            val idx = (norm / span).toInt().coerceIn(0, 26)
            val posInNak = norm - (idx * span)
            val padaSpan = span / 4.0 // 3.333333 degrees
            val pada = (posInNak / padaSpan).toInt() + 1
            return Pair(entries[idx], pada.coerceIn(1, 4))
        }
    }
}
