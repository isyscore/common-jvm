package com.isyscore.kotlin.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 如果要改这些字段，必须在程序刚运行时就改，否则jackson可能会出错
 */
var LOCAL_DATE_PATTERN_STR = "yyyy-MM-dd"
var LOCAL_TIME_PATTERN_STR = "HH:mm:ss"
var LOCAL_DATETIME_PATTERN_STR = "yyyy-MM-dd HH:mm:ss"

var LOCAL_DATE_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN_STR)
var LOCAL_TIME_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern(LOCAL_TIME_PATTERN_STR)
var LOCAL_DATETIME_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern(LOCAL_DATETIME_PATTERN_STR)

fun LocalDateTime.fmt(): String = format(LOCAL_DATETIME_PATTERN)
fun LocalDateTime.exportFmt(): String = format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
fun LocalDate.fmt(): String = format(LOCAL_DATE_PATTERN)
fun LocalTime.fmt(): String = format(LOCAL_TIME_PATTERN)