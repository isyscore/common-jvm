@file:Suppress("DEPRECATION")

package com.isyscore.kotlin.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
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

lateinit var objMapper: ObjectMapper

private fun initJacksonMapper() {
    objMapper = ObjectMapper().apply {
        registerModule(KtormModule())
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule().apply {
            addDeserializer(LocalDate::class.java, LocalDateDeserializer(LOCAL_DATE_PATTERN))
            addSerializer(LocalDate::class.java, LocalDateSerializer(LOCAL_DATE_PATTERN))
            addDeserializer(LocalTime::class.java, LocalTimeDeserializer(LOCAL_TIME_PATTERN))
            addSerializer(LocalTime::class.java, LocalTimeSerializer(LOCAL_TIME_PATTERN))
            addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(LOCAL_DATETIME_PATTERN))
            addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(LOCAL_DATETIME_PATTERN))
        })
        configure(SerializationFeature.INDENT_OUTPUT, true)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
        configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
        configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false)

        configure(JsonReadFeature.ALLOW_YAML_COMMENTS.mappedFeature(), true)
        configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true)
        configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
        configure(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature(), true)
        configure(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true)
        configure(JsonReadFeature.ALLOW_MISSING_VALUES.mappedFeature(), true)

        setDefaultLeniency(true)
        setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
            indentObjectsWith(DefaultIndenter("  ", "\n"))
        })
        setSerializationInclusion(JsonInclude.Include.ALWAYS)
        enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        deactivateDefaultTyping()
    }
}

fun checkObjMapper() {
    if (!::objMapper.isInitialized) {
        initJacksonMapper()
    }
}

inline fun <reified T> T.toJson(): String {
    checkObjMapper()
    return objMapper.writeValueAsString(this)
}

inline fun <reified T> String.toObj(): T {
    checkObjMapper()
    return objMapper.readValue(this, object : TypeReference<T>() {})
}

