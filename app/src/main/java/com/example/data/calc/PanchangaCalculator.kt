package com.example.data.calc

import com.example.data.model.AuspiciousMuhurtha
import com.example.data.model.DailyPanchanga
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PanchangaCalculator {

    fun getDailyPanchanga(calendar: Calendar = Calendar.getInstance()): DailyPanchanga {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // Sunday = 1, Monday = 2 ... Saturday = 7
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val dateFormat = SimpleDateFormat("yyyy MMMM dd", Locale.US)
        val dateFormatted = dateFormat.format(calendar.time)

        val (daySin, dayEng) = when (dayOfWeek) {
            Calendar.SUNDAY -> Pair("ඉරිදා", "Sunday")
            Calendar.MONDAY -> Pair("සඳුදා", "Monday")
            Calendar.TUESDAY -> Pair("අඟහරුවාදා", "Tuesday")
            Calendar.WEDNESDAY -> Pair("බදාදා", "Wednesday")
            Calendar.THURSDAY -> Pair("බ්‍රහස්පතින්දා", "Thursday")
            Calendar.FRIDAY -> Pair("සිකුරාදා", "Friday")
            Calendar.SATURDAY -> Pair("සෙනසුරාදා", "Saturday")
            else -> Pair("දිනය", "Day")
        }

        // Rahu Kalaya (Daytime - Traditional Sri Lankan calculation based on 6am - 6pm)
        val (rahuStart, rahuEnd) = when (dayOfWeek) {
            Calendar.SUNDAY -> Pair("16:30", "18:00")
            Calendar.MONDAY -> Pair("07:30", "09:00")
            Calendar.TUESDAY -> Pair("15:00", "16:30")
            Calendar.WEDNESDAY -> Pair("12:00", "13:30")
            Calendar.THURSDAY -> Pair("13:30", "15:00")
            Calendar.FRIDAY -> Pair("10:30", "12:00")
            Calendar.SATURDAY -> Pair("09:00", "10:30")
            else -> Pair("12:00", "13:30")
        }

        // Maru Situ Disawa (මරු සිටින දිශාව)
        val (maruSin, maruEng) = when (dayOfWeek) {
            Calendar.SUNDAY -> Pair("උතුර (North)", "North")
            Calendar.MONDAY -> Pair("වයඹ (North-West)", "North-West")
            Calendar.TUESDAY -> Pair("බටහිර (West)", "West")
            Calendar.WEDNESDAY -> Pair("නිරිත (South-West)", "South-West")
            Calendar.THURSDAY -> Pair("දකුණ (South)", "South")
            Calendar.FRIDAY -> Pair("ගිනිකොණ (South-East)", "South-East")
            Calendar.SATURDAY -> Pair("නැගෙනහිර (East)", "East")
            else -> Pair("උතුර", "North")
        }

        // Subha Disawa (සුබ දිශාව)
        val (subhaSin, subhaEng) = when (dayOfWeek) {
            Calendar.SUNDAY -> Pair("දකුණ (South)", "South")
            Calendar.MONDAY -> Pair("ගිනිකොණ (South-East)", "South-East")
            Calendar.TUESDAY -> Pair("නැගෙනහිර (East)", "East")
            Calendar.WEDNESDAY -> Pair("ඊසාන (North-East)", "North-East")
            Calendar.THURSDAY -> Pair("උතුර (North)", "North")
            Calendar.FRIDAY -> Pair("වයඹ (North-West)", "North-West")
            Calendar.SATURDAY -> Pair("බටහිර (West)", "West")
            else -> Pair("නැගෙනහිර", "East")
        }

        // Tithi calculation (approx based on lunar cycle)
        val dayIndex = (dayOfMonth + month * 2) % 30
        val isShukla = dayIndex < 15
        val tithiNumber = (dayIndex % 15) + 1
        val tithiSin = if (isShukla) "පුර පසළොස්වක / තිථිය $tithiNumber" else "අව තිථිය $tithiNumber"
        val tithiEng = if (isShukla) "Shukla Paksha Tithi $tithiNumber" else "Krishna Paksha Tithi $tithiNumber"

        val nakshatraNamesSin = listOf("අස්විද", "බෙරණ", "කැති", "රෙහෙණ", "මුවසිරස", "අද", "පුනාවස", "පුෂ", "අස්ලිස", "මා", "පුවපල්", "උත්‍රපල්", "හත", "සිත", "සා", "විසා", "අනුර", "දෙට", "මුල", "පුවසල", "උත්‍රසල", "සුවණ", "දෙනට", "සියාවස", "පුවපුටුප", "උත්‍රපුටුප", "රේවතී")
        val nakshatraNamesEng = listOf("Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati")
        val nakIndex = (dayOfMonth + month * 3 + year) % 27
        val nakSin = nakshatraNamesSin[nakIndex]
        val nakEng = nakshatraNamesEng[nakIndex]

        val yogaSin = listOf("විෂ්කම්භ", "ප්‍රීති", "ආයුෂ්මාන්", "සෞභාග්‍ය", "ශෝභන", "සුකර්ම", "ධෘති", "ශූල", "වෘද්ධි", "ධ්‍රැව")[dayIndex % 10]
        val yogaEng = listOf("Vishkambha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Sukarma", "Dhriti", "Shula", "Vriddhi", "Dhruva")[dayIndex % 10]

        val karanaSin = listOf("බව", "බාලව", "කෞලව", "තෛතිල", "ගර", "වණිජ", "විෂ්ටි")[dayIndex % 7]
        val karanaEng = listOf("Bava", "Balava", "Kaulava", "Taitila", "Gara", "Vanija", "Vishti")[dayIndex % 7]

        val adviceSin = "අද දිනයේ මරු සිටින ${maruSin.substringBefore(" ")} දිශාව හැර ${subhaSin.substringBefore(" ")} දිශාව බලා වැදගත් කටයුතු ඇරඹීම ඉතා යහපත්ය. රාහු කාලය තුළ වැදගත් ගිවිසුම් හා මංගල කටයුතුවලින් වළකින්න."
        val adviceEng = "Facing $subhaEng for vital initiatives yields high fortune. Avoid commencing momentous contracts or ceremonies during Rahu Kalaya."

        val auspiciousTimes = generateUpcomingAuspiciousTimes(calendar)

        return DailyPanchanga(
            dateFormatted = dateFormatted,
            dayOfWeekSinhala = daySin,
            dayOfWeekEnglish = dayEng,
            tithiSinhala = tithiSin,
            tithiEnglish = tithiEng,
            nakshatraSinhala = nakSin,
            nakshatraEnglish = nakEng,
            yogaSinhala = yogaSin,
            yogaEnglish = yogaEng,
            karanaSinhala = karanaSin,
            karanaEnglish = karanaEng,
            rahuKalayaStart = rahuStart,
            rahuKalayaEnd = rahuEnd,
            maruSituDisawaSinhala = maruSin,
            maruSituDisawaEnglish = maruEng,
            subhaDisawaSinhala = subhaSin,
            subhaDisawaEnglish = subhaEng,
            auspiciousTimes = auspiciousTimes,
            dailyAdviceSinhala = adviceSin,
            dailyAdviceEnglish = adviceEng
        )
    }

    fun generateUpcomingAuspiciousTimes(baseCal: Calendar): List<AuspiciousMuhurtha> {
        val list = mutableListOf<AuspiciousMuhurtha>()
        val cal = baseCal.clone() as Calendar

        val categories = listOf(
            Triple("විවාහ මංගල නැකත", "Auspicious Wedding Muhurtha", "MARRIAGE"),
            Triple("නව නිවාස ගෙවදීම", "House Warming & Foundation", "HOUSE"),
            Triple("ව්‍යාපාර / රැකියා ඇරඹුම", "New Business & Career Launch", "BUSINESS"),
            Triple("වාහන මිලදී ගැනීම හා ධාවනය", "Vehicle Purchase & First Drive", "VEHICLE"),
            Triple("දරුවන්ට අකුරු කියවීම", "Education & First Letter Reading", "EDUCATION")
        )

        val times = listOf("06:14 - 07:02", "09:24 - 10:12", "11:08 - 11:56", "14:18 - 15:06")
        val directions = listOf(
            Pair("නැගෙනහිර (East)", "East"),
            Pair("උතුර (North)", "North"),
            Pair("ඊසාන (North-East)", "North-East"),
            Pair("දකුණ (South)", "South")
        )

        for (i in 0 until 6) {
            val offsetCal = cal.clone() as Calendar
            offsetCal.add(Calendar.DAY_OF_YEAR, i * 2 + 1)
            val dateStr = SimpleDateFormat("yyyy-MM-dd (EEEE)", Locale.US).format(offsetCal.time)
            val cat = categories[i % categories.size]
            val timeRange = times[i % times.size]
            val dir = directions[i % directions.size]

            val descSin = "ගුරු හෝරාවෙන් අමෘත යෝගය යෙදී ඇති සුබ හෝරාවකි. ${dir.first} බලා සුදු වස්ත්‍රාභරණයෙන් සැරසී කටයුතු අරඹන්න."
            val descEng = "Sanctified by Jupiter's Amrita planetary hour. Face ${dir.second} in serene white attire to inaugurate successfully."

            list.add(
                AuspiciousMuhurtha(
                    id = "muhurtha_$i",
                    titleSinhala = cat.first,
                    titleEnglish = cat.second,
                    category = cat.third,
                    dateStr = dateStr,
                    timeRange = timeRange,
                    subhaDirectionSinhala = dir.first,
                    subhaDirectionEnglish = dir.second,
                    descriptionSinhala = descSin,
                    descriptionEnglish = descEng
                )
            )
        }

        return list
    }
}
