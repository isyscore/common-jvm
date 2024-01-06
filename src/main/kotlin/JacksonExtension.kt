package com.isyscore.kotlin.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.ktorm.jackson.KtormModule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val objMapper = ObjectMapper().apply {
    registerModule(KtormModule())
    registerModule(KotlinModule.Builder().build())
    registerModule(JavaTimeModule().apply {
        addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN)))
        addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN)))
        addDeserializer(LocalTime::class.java, LocalTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_TIME_PATTERN)))
        addSerializer(LocalTime::class.java, LocalTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_TIME_PATTERN)))
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATETIME_PATTERN)))
        addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_DATETIME_PATTERN)))
    })
    configure(SerializationFeature.INDENT_OUTPUT, true)
    setDefaultLeniency(true)
    setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
        indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
        indentObjectsWith(DefaultIndenter("  ", "\n"))
    })
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
}

inline fun <reified T> T.toJson(): String = objMapper.writeValueAsString(this)
inline fun <reified T> String.toObj(): T = objMapper.readValue(this, object : TypeReference<T>() {})

