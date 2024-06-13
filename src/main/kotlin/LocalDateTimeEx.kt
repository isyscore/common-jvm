package com.isyscore.kotlin.common

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * 如果要改这些字段，必须在程序刚运行时就改，否则jackson可能会出错
 */
var LOCAL_DATE_PATTERN_STR = "yyyy-MM-dd"
var LOCAL_TIME_PATTERN_STR = "HH:mm:ss"
var LOCAL_DATETIME_PATTERN_STR = "yyyy-MM-dd HH:mm:ss"

var LOCAL_DATE_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN_STR)
var LOCAL_TIME_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern(LOCAL_TIME_PATTERN_STR)
var LOCAL_DATETIME_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern(LOCAL_DATETIME_PATTERN_STR)

val DATETIME_FORMATTER_TZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

fun LocalDateTime.fmt(): String = format(LOCAL_DATETIME_PATTERN)
fun LocalDateTime.exportFmt(): String = format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
fun LocalDate.fmt(): String = format(LOCAL_DATE_PATTERN)
fun LocalTime.fmt(): String = format(LOCAL_TIME_PATTERN)

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
fun String.toLocalTime(): LocalTime = LocalTime.parse(this, DateTimeFormatter.ISO_TIME)
fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)

fun String.toLocalDateOrNull(): LocalDate? = try {
    LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
} catch (_: Exception) {
    null
}

fun String.toLocalTimeOrNull(): LocalTime? = try {
    LocalTime.parse(this, DateTimeFormatter.ISO_TIME)
} catch (_: Exception) {
    null
}

fun String.toLocalDateTimeOrNull(): LocalDateTime? = try {
    LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
} catch (_: Exception) {
    null
}

fun String.toLocalDateTimeRangeOrNull(): Pair<LocalDateTime, LocalDateTime>? = try {
    val ld = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
    val st = LocalDateTime.parse("${ld.year}-${ld.monthValue.toString().padStart(2, '0')}-${ld.dayOfMonth.toString().padStart(2, '0')}T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
    val ed = LocalDateTime.parse("${ld.year}-${ld.monthValue.toString().padStart(2, '0')}-${ld.dayOfMonth.toString().padStart(2, '0')}T23:59:59", DateTimeFormatter.ISO_DATE_TIME)
    st to ed
} catch (e: Exception) {
    null
}

fun Int.toLocalDateRecentRange(): Pair<LocalDateTime, LocalDateTime> {
    val ld = LocalDate.now().minusDays(toLong())
    val st = LocalDateTime.parse("${ld.year}-${ld.monthValue.toString().padStart(2, '0')}-${ld.dayOfMonth.toString().padStart(2, '0')}T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
    val ed = LocalDateTime.now()
    return st to ed
}

fun LocalDateTime.between(other: LocalDateTime, u: TimeUnit): Long {
    val d = Duration.between(this, other)
    return when(u) {
        TimeUnit.NANOSECONDS -> d.toNanos()
        TimeUnit.MILLISECONDS -> d.toMillis()
        TimeUnit.SECONDS -> d.seconds
        TimeUnit.MINUTES -> d.toMinutes()
        TimeUnit.HOURS -> d.toHours()
        TimeUnit.DAYS -> d.toDays()
        else -> throw IllegalArgumentException("specified TimeUnit not supported.")
    }
}