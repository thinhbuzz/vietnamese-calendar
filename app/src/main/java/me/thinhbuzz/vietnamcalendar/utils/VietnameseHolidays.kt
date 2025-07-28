package me.thinhbuzz.vietnamcalendar.utils

import android.content.Context
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import me.thinhbuzz.vietnamcalendar.data.HolidayDataLoader
import me.thinhbuzz.vietnamcalendar.data.HolidayInfo

data class Holiday(
    val name: String,
    val date: LocalDate,
    val isLunar: Boolean = false,
    val description: String = ""
)

object VietnameseHolidays {
    private var dataLoader: HolidayDataLoader? = null
    
    fun initialize(context: Context) {
        dataLoader = HolidayDataLoader.getInstance(context)
    }
    
    suspend fun getSolarHolidaysAsync(year: Int): List<Holiday> {
        // Try to use data loader if available
        dataLoader?.let { loader ->
            return try {
                val holidays = loader.getHolidaysForYear(year)
                holidays
                    .filter { !it.isLunar }
                    .map { convertToKotlinHoliday(it) }
            } catch (e: Exception) {
                // Fall back to hardcoded data
                getDefaultSolarHolidays(year)
            }
        }
        
        // Fallback to hardcoded data
        return getDefaultSolarHolidays(year)
    }
    
    fun getSolarHolidays(year: Int): List<Holiday> {
        // Fallback to hardcoded data for non-suspend calls
        return getDefaultSolarHolidays(year)
    }
    
    private fun getDefaultSolarHolidays(year: Int): List<Holiday> {
        return listOf(
            Holiday(
                name = "Tết Dương lịch",
                date = LocalDate(year, 1, 1),
                description = "New Year's Day"
            ),
            Holiday(
                name = "Ngày thành lập Đảng Cộng sản Việt Nam",
                date = LocalDate(year, 2, 3),
                description = "Communist Party Foundation Day"
            ),
            Holiday(
                name = "Ngày Giải phóng miền Nam",
                date = LocalDate(year, 4, 30),
                description = "Reunification Day"
            ),
            Holiday(
                name = "Ngày Quốc tế Lao động",
                date = LocalDate(year, 5, 1),
                description = "International Labor Day"
            ),
            Holiday(
                name = "Ngày Quốc khánh",
                date = LocalDate(year, 9, 2),
                description = "National Day"
            )
        )
    }
    
    suspend fun getLunarHolidaysAsync(year: Int): List<Holiday> {
        // Try to use data loader if available
        dataLoader?.let { loader ->
            return try {
                val holidays = loader.getHolidaysForYear(year)
                holidays
                    .filter { it.isLunar }
                    .map { convertToKotlinHoliday(it) }
            } catch (e: Exception) {
                // Fall back to hardcoded data
                getDefaultLunarHolidays(year)
            }
        }
        
        // Fallback to hardcoded data
        return getDefaultLunarHolidays(year)
    }
    
    fun getLunarHolidays(year: Int): List<Holiday> {
        // Fallback to hardcoded data for non-suspend calls
        return getDefaultLunarHolidays(year)
    }
    
    private fun getDefaultLunarHolidays(year: Int): List<Holiday> {
        val lunarHolidays = mutableListOf<Holiday>()
        
        // Tết Nguyên Đán (Lunar New Year) - 1st day of 1st lunar month
        val tet = LunarCalendarConverter.convertLunar2Solar(1, 1, year, false)
        if (tet.third == year || tet.third == year - 1) {
            lunarHolidays.add(
                Holiday(
                    name = "Tết Nguyên Đán",
                    date = LocalDate(tet.third, tet.second, tet.first),
                    isLunar = true,
                    description = "Vietnamese New Year"
                )
            )
        }
        
        // Tết Nguyên Đán holidays (2nd, 3rd day)
        val tet2 = LunarCalendarConverter.convertLunar2Solar(2, 1, year, false)
        if (tet2.third == year || tet2.third == year - 1) {
            lunarHolidays.add(
                Holiday(
                    name = "Mùng 2 Tết",
                    date = LocalDate(tet2.third, tet2.second, tet2.first),
                    isLunar = true,
                    description = "2nd day of Tet"
                )
            )
        }
        
        val tet3 = LunarCalendarConverter.convertLunar2Solar(3, 1, year, false)
        if (tet3.third == year || tet3.third == year - 1) {
            lunarHolidays.add(
                Holiday(
                    name = "Mùng 3 Tết",
                    date = LocalDate(tet3.third, tet3.second, tet3.first),
                    isLunar = true,
                    description = "3rd day of Tet"
                )
            )
        }
        
        // Giỗ Tổ Hùng Vương - 10th day of 3rd lunar month
        val hungVuong = LunarCalendarConverter.convertLunar2Solar(10, 3, year, false)
        if (hungVuong.third == year) {
            lunarHolidays.add(
                Holiday(
                    name = "Giỗ Tổ Hùng Vương",
                    date = LocalDate(hungVuong.third, hungVuong.second, hungVuong.first),
                    isLunar = true,
                    description = "Hung Kings' Temple Festival"
                )
            )
        }
        
        // Tết Đoan Ngọ - 5th day of 5th lunar month
        val doanNgo = LunarCalendarConverter.convertLunar2Solar(5, 5, year, false)
        if (doanNgo.third == year) {
            lunarHolidays.add(
                Holiday(
                    name = "Tết Đoan Ngọ",
                    date = LocalDate(doanNgo.third, doanNgo.second, doanNgo.first),
                    isLunar = true,
                    description = "Killing Inner Insects Festival"
                )
            )
        }
        
        // Rằm tháng Giêng - 15th day of 1st lunar month
        val ramThangGieng = LunarCalendarConverter.convertLunar2Solar(15, 1, year, false)
        if (ramThangGieng.third == year) {
            lunarHolidays.add(
                Holiday(
                    name = "Rằm tháng Giêng",
                    date = LocalDate(ramThangGieng.third, ramThangGieng.second, ramThangGieng.first),
                    isLunar = true,
                    description = "First Full Moon Festival"
                )
            )
        }
        
        // Vu Lan - 15th day of 7th lunar month
        val vuLan = LunarCalendarConverter.convertLunar2Solar(15, 7, year, false)
        if (vuLan.third == year) {
            lunarHolidays.add(
                Holiday(
                    name = "Lễ Vu Lan",
                    date = LocalDate(vuLan.third, vuLan.second, vuLan.first),
                    isLunar = true,
                    description = "Ghost Festival"
                )
            )
        }
        
        // Tết Trung Thu - 15th day of 8th lunar month
        val trungThu = LunarCalendarConverter.convertLunar2Solar(15, 8, year, false)
        if (trungThu.third == year) {
            lunarHolidays.add(
                Holiday(
                    name = "Tết Trung Thu",
                    date = LocalDate(trungThu.third, trungThu.second, trungThu.first),
                    isLunar = true,
                    description = "Mid-Autumn Festival"
                )
            )
        }
        
        // Tết Ông Táo - 23rd day of 12th lunar month
        val ongTao = LunarCalendarConverter.convertLunar2Solar(23, 12, year - 1, false)
        if (ongTao.third == year) {
            lunarHolidays.add(
                Holiday(
                    name = "Tết Ông Táo",
                    date = LocalDate(ongTao.third, ongTao.second, ongTao.first),
                    isLunar = true,
                    description = "Kitchen God Day"
                )
            )
        }
        
        return lunarHolidays
    }
    
    suspend fun getAllHolidaysSuspend(year: Int): List<Holiday> {
        // Try to use data loader if available
        dataLoader?.let { loader ->
            return try {
                val holidays = loader.getHolidaysForYear(year)
                holidays.map { convertToKotlinHoliday(it) }
            } catch (e: Exception) {
                // Fall back to hardcoded data
                getSolarHolidaysAsync(year) + getLunarHolidaysAsync(year)
            }
        }
        
        return getSolarHolidaysAsync(year) + getLunarHolidaysAsync(year)
    }
    
    fun getAllHolidays(year: Int): List<Holiday> {
        // Fallback to hardcoded data for non-suspend calls
        return getSolarHolidays(year) + getLunarHolidays(year)
    }
    
    suspend fun isHolidaySuspend(date: LocalDate): Holiday? {
        // Try to use data loader if available
        dataLoader?.let { loader ->
            return try {
                val holiday = loader.getHoliday(date.toJavaLocalDate())
                holiday?.let { convertToKotlinHoliday(it) }
            } catch (e: Exception) {
                // Fall back to hardcoded data
                val holidays = getAllHolidaysSuspend(date.year)
                holidays.find { it.date == date }
            }
        }
        
        val holidays = getAllHolidaysSuspend(date.year)
        return holidays.find { it.date == date }
    }
    
    fun isHoliday(date: LocalDate): Holiday? {
        // Fallback to hardcoded data for non-suspend calls
        val holidays = getAllHolidays(date.year)
        return holidays.find { it.date == date }
    }
    
    private fun convertToKotlinHoliday(holiday: HolidayInfo): Holiday {
        return Holiday(
            name = holiday.name,
            date = holiday.date.toKotlinLocalDate(),
            isLunar = holiday.isLunar,
            description = holiday.description
        )
    }
}