package com.isyscore.kotlin.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val LOCAL_DATE_PATTERN = "yyyy-MM-dd"
const val LOCAL_TIME_PATTERN = "HH:mm:ss"
const val LOCAL_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

fun LocalDateTime.fmt(): String = format(DateTimeFormatter.ofPattern(LOCAL_DATETIME_PATTERN))

fun LocalDateTime.exportFmt(): String = format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

fun LocalDate.fmt(): String = format(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN))
fun LocalTime.fmt(): String = format(DateTimeFormatter.ofPattern(LOCAL_TIME_PATTERN))