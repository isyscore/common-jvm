@file:Suppress("UNCHECKED_CAST", "unused")

package com.isyscore.kotlin.common

import com.alibaba.druid.pool.DruidDataSourceFactory
import com.isyscore.kotlin.common.KtormColumnSelection.multipleColumns
import com.isyscore.kotlin.common.KtormColumnSelection.singleColumn
import org.ktorm.database.Database
import org.ktorm.database.SqlDialect
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.schema.*
import java.lang.reflect.Field
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.jvm.jvmErasure

val logger = ConsoleLogger(LogLevel.ERROR)

/**
 * 使用 Druid 建立连接池
 */
fun databasePoolOf(driverClass: String, jdbcUrl: String, user: String, password: String, dialect: SqlDialect, logLevel: LogLevel = LogLevel.INFO, validationQuery: String = "select 1;"): Pair<Database?, Throwable?> {
    try {
        val dataSource = DruidDataSourceFactory.createDataSource(
            mapOf(
                "driverClassName" to driverClass,
                "url" to jdbcUrl,
                "username" to user,
                "password" to password,
                "logLevel" to logLevel.name.uppercase(),
                "validationQuery" to validationQuery
            )
        )
        val db = Database.connect(dataSource = dataSource, dialect = dialect, logger = ConsoleLogger(logLevel))
        return db to null
    } catch (e: Throwable) {
        return null to e
    }
}

fun <E : Entity<E>, T> Entity<E>.oneToMany(fieldName: String, loader: () -> List<T>) {
    (this[fieldName] as? T) ?: loader().also { this[fieldName] = it }
}

fun <E : Entity<E>, T> Entity<E>.oneToManyNull(fieldName: String, loader: () -> List<T?>) {
    (this[fieldName] as? T) ?: loader().also { this[fieldName] = it }
}

fun <E : Entity<E>, T> Entity<E>.oneToOne(fieldName: String, loader: () -> T) {
    (this[fieldName] as? T) ?: loader().also { this[fieldName] = it }
}

fun <E : Entity<E>, T> Entity<E>.oneToOneNull(fieldName: String, loader: () -> T?) {
    (this[fieldName] as? T) ?: loader().also { this[fieldName] = it }
}

fun <E : Entity<E>> Table<E>.createEntity(): E = (entityClass?.companionObjectInstance as? Entity.Factory<E>)?.invoke() ?: throw RuntimeException("Table [$this] has not an Entity Class.")

open class ConditionalTable<E : Entity<E>>(tableName: String, alias: String? = null, catalog: String? = null, schema: String? = null, entityClass: KClass<E>? = null) : Table<E>(tableName, alias, catalog, schema, entityClass) {

    companion object {
        val entityExtensionsApi: EntityExtensionsApi = EntityExtensionsApi()

        inline fun <T> exApi(block: EntityExtensionsApi.() -> T): T = entityExtensionsApi.block()
    }

    private val columnConditions = mutableMapOf<String, (E) -> ColumnDeclaring<Boolean>?>()

    inline fun <reified C : Any> Column<C>.conditionOn(crossinline condition: E.(column: Column<C>, value: C?) -> ColumnDeclaring<Boolean>): Column<C> =
        saveColumnCondition { entity ->
            val value = exApi { entity.getColumnValueOrNull(this@conditionOn) }
            entity.condition(this, value as C?)
        }

    inline fun <reified C : Any> Column<C>.conditionNotNullOn(crossinline condition: E.(column: Column<C>, value: C) -> ColumnDeclaring<Boolean>): Column<C> =
        saveColumnCondition { entity ->
            val value = entity.getColumnValueOrThrow(this)
            if (value != null) entity.condition(this, value as C) else null
        }

    fun Entity<*>.getColumnValueOrNull(column: Column<*>): Any? =
        column.binding?.let { b ->
            exApi {
                getColumnValue(b)
            }
        }

    fun Entity<*>.getColumnValueOrThrow(column: Column<*>): Any? {
        val binding = column.binding
        if (binding != null) {
            return exApi { getColumnValue(binding) }
        }
        error("Column $column has no bindings to any entity field.")
    }

    fun <C : Any> Column<C>.saveColumnCondition(condition: (E) -> ColumnDeclaring<Boolean>?): Column<C> {
        // merge by 'and'
        columnConditions.merge(name, condition) { old, curr ->
            { entity ->
                val condition1 = old(entity)
                val condition2 = curr(entity)
                when {
                    condition1 == null && condition2 == null -> null
                    condition1 == null -> condition2
                    condition2 == null -> condition1
                    else -> condition1 and condition2
                }
            }
        }
        return this
    }

    fun asCondition(entity: E): ColumnDeclaring<Boolean>? =
        columnConditions.values.fold<(E) -> ColumnDeclaring<Boolean>?, ColumnDeclaring<Boolean>?>(null) { left, factory ->
            val declaring = factory(entity)
            if (left == null) {
                declaring
            } else {
                if (declaring == null) left else left and declaring
            }
        }
}

inline fun <E : Entity<E>, T : ConditionalTable<E>> EntitySequence<E, T>.filterBy(conditionEntity: E, andThen: ((table: T, condition: ColumnDeclaring<Boolean>) -> ColumnDeclaring<Boolean>) = { _, condition -> condition }): EntitySequence<E, T> =
    sourceTable.asCondition(conditionEntity)?.let { condition -> filter { table -> andThen(table, condition) } } ?: this

inline fun <E : Entity<E>, T : ConditionalTable<E>> Query.whereBy(table: T, conditionEntity: E, andThen: ((condition: ColumnDeclaring<Boolean>) -> ColumnDeclaring<Boolean>) = { it }): Query =
    table.asCondition(conditionEntity)?.let { this.where(andThen(it)) } ?: this

inline fun <E : Entity<E>, T : ConditionalTable<E>> EntitySequence<E, T>.filterByOr(conditionEntity: E, andThen: ((table: T, condition: ColumnDeclaring<Boolean>?) -> ColumnDeclaring<Boolean>?) = { _, condition -> condition }): EntitySequence<E, T> =
    andThen(sourceTable, sourceTable.asCondition(conditionEntity))?.let { condition -> filter { condition } } ?: this

inline fun <E : Entity<E>, T : ConditionalTable<E>> Query.whereByOr(table: T, conditionEntity: E, andThen: ((condition: ColumnDeclaring<Boolean>?) -> ColumnDeclaring<Boolean>?) = { it }): Query =
    andThen(table.asCondition(conditionEntity))?.let { this.where(it) } ?: this

fun <E : Entity<E>, T : Table<E>> EntitySequence<E, T>.save(overrideExisting: Boolean, entity: E, predicate: (T) -> ColumnDeclaring<Boolean>): Int {
    val e = find(predicate)
    return if (e == null) {
        // 如果数据不存在，新增它
        try {
            add(entity)
        } catch (e: Exception) {
            0
        }
    } else {
        // 如果数据已存在
        if (overrideExisting) {
            // 判断是否要覆盖
            try {
                update(entity)
            } catch (e: Exception) {
                0
            }
        } else {
            // 不覆盖时直接返回 0
            0
        }
    }
}

inline fun <E : Any, T : BaseTable<E>> EntitySequence<E, T>.exist(predicate: (T) -> ColumnDeclaring<Boolean>): Boolean = this.filter(predicate).isNotEmpty()

inline fun <E : Any, T : BaseTable<E>> EntitySequence<E, T>.noExist(predicate: (T) -> ColumnDeclaring<Boolean>): Boolean = this.filter(predicate).isEmpty()

fun <E : Any, T : BaseTable<E>> EntitySequence<E, T>.page(page: Int, pageSize: Int): EntitySequence<E, T> = this.withExpression(expression.copy(limit = pageSize, offset = (page - 1) * pageSize))

fun Query.page(page: Int, pageSize: Int): Query = this.limit((pageSize * page - 1), pageSize)

object KtormColumnSelection {

    sealed interface ColumnSelection

    data class SingleColumn internal constructor(val column: ColumnDeclaring<*>) : ColumnSelection

    data class MultipleColumns internal constructor(val columns: Collection<ColumnDeclaring<*>>) : ColumnSelection

    fun singleColumn(column: ColumnDeclaring<*>): SingleColumn = SingleColumn(column)

    fun multipleColumns(vararg column: ColumnDeclaring<*>): MultipleColumns = MultipleColumns(column.asList())

}

class QueryColumnWithCondition<T : BaseTable<*>>(var table: T) {

    var columnSelection: KtormColumnSelection.ColumnSelection? = null
    var condition: (Query.(T) -> Query)? = null

    fun query(vararg columns: ColumnDeclaring<*>, condition: (Query.(T) -> Query)?) {
        this.condition = condition
        columnSelection = when (columns.size) {
            1 -> singleColumn(columns[0])
            else -> multipleColumns(*columns)
        }
    }

}

fun <T : BaseTable<*>> Database.selectFromTable(table: T, block: (QueryColumnWithCondition<T>.(T) -> Unit)? = null): Query {
    val querySource = this.from(table)
    if (block == null) {
        return querySource.select()
    }
    val queryColumWithCondition = QueryColumnWithCondition(table).apply {
        this.block(table)
    }
    val condition = queryColumWithCondition.condition
    val columnSelection = queryColumWithCondition.columnSelection
    if (condition == null) {
        return when (columnSelection) {
            is KtormColumnSelection.SingleColumn -> querySource.select(columnSelection.column)
            is KtormColumnSelection.MultipleColumns -> querySource.select(columnSelection.columns)
            else -> throw IllegalArgumentException("columnSelection is neither SingleColumn nor MultipleColumns")
        }
    }
    return when (columnSelection) {
        is KtormColumnSelection.SingleColumn -> querySource.select(columnSelection.column).condition(table)
        is KtormColumnSelection.MultipleColumns -> querySource.select(columnSelection.columns).condition(table)
        else -> throw IllegalArgumentException("columnSelection is neither SingleColumn nor MultipleColumns")
    }
}


fun <E : Entity<E>> BaseTable<E>.createEntity(row: ResultSet): E {
    val entityClass = this.entityClass ?: error("No entity class configured for table: '$this'")
    val entity = Entity.create(entityClass) as E
    for (column in columns) {
        row.retrieveColumn(column, intoEntity = entity)
    }
    return entity
}

val formatterMonthDay: DateTimeFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .appendLiteral('-')
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .toFormatter()
val formatterYearMonth: DateTimeFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
    .appendLiteral('-')
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .toFormatter()

fun <E : Entity<E>> ResultSet.retrieveColumn(column: Column<*>, intoEntity: E) {
    val binding = column.binding ?: return

    val propName = when (binding) {
        is ReferenceBinding -> binding.onProperty.name
        is NestedBinding -> if (binding.properties.isEmpty()) return else binding.properties.first().name
    }

    try {
        when (column.sqlType) {
            BooleanSqlType -> intoEntity[propName] = this.getBoolean(column.name)
            IntSqlType -> intoEntity[propName] = this.getInt(column.name)
            ShortSqlType -> intoEntity[propName] = this.getShort(column.name)
            LongSqlType -> intoEntity[propName] = this.getLong(column.name)
            FloatSqlType -> intoEntity[propName] = this.getFloat(column.name)
            DoubleSqlType -> intoEntity[propName] = this.getDouble(column.name)
            DecimalSqlType -> intoEntity[propName] = this.getBigDecimal(column.name)
            VarcharSqlType -> intoEntity[propName] = this.getString(column.name)
            BytesSqlType -> intoEntity[propName] = this.getBytes(column.name)
            TimestampSqlType -> intoEntity[propName] = this.getTimestamp(column.name)
            DateSqlType -> intoEntity[propName] = this.getDate(column.name)
            TimeSqlType -> intoEntity[propName] = this.getTime(column.name)
            InstantSqlType -> intoEntity[propName] = this.getTimestamp(column.name)?.toInstant()
            LocalDateTimeSqlType -> intoEntity[propName] = this.getTimestamp(column.name)?.toLocalDateTime()
            LocalDateSqlType -> intoEntity[propName] = this.getDate(column.name)?.toLocalDate()
            LocalTimeSqlType -> intoEntity[propName] = this.getTime(column.name)?.toLocalTime()
            TextSqlType -> intoEntity[propName] = this.getString(column.name)
            BlobSqlType -> intoEntity[propName] = this.getBlob(column.name)?.let {
                try {
                    it.binaryStream.use { s -> s.readBytes() }
                } finally {
                    it.free()
                }
            }
            MonthDaySqlType -> intoEntity[propName] = this.getString(column.name)?.let { MonthDay.parse(it, formatterMonthDay) }
            YearMonthSqlType -> intoEntity[propName] = this.getString(column.name)?.let { YearMonth.parse(it, formatterYearMonth) }
            YearSqlType -> intoEntity[propName] = Year.of(this.getInt(column.name))
            UuidSqlType -> intoEntity[propName] = this.getObject(column.name) as? UUID
        }
    } catch (e: Exception) {
        logger.error("ResultSet.retrieveColumn[${propName}]", e)
    }

}

fun <R> ResultSet.map(block: (ResultSet) -> R): List<R> {
    val list = mutableListOf<R>()
    while (next()) {
        list.add(block(this))
    }
    return list
}

fun <R> ResultSet.useMap(block: (ResultSet) -> R): List<R> {
    val list = mutableListOf<R>()
    this.use { rs ->
        while (rs.next()) {
            list.add(block(rs))
        }
    }
    return list
}

fun ResultSet.getMatchFieldValue(name: String, type: Class<*>): Any? {
    val col = (1..this.metaData.columnCount).find {
        this.metaData.getColumnName(it).conv().lowercase() == name.conv().lowercase()
    } ?: return null
    return when (type) {
        Boolean::class.java -> getBoolean(col)
        Int::class.java -> getInt(col)
        Short::class.java -> getShort(col)
        Long::class.java -> getLong(col)
        Float::class.java -> getFloat(col)
        Double::class.java -> getDouble(col)
        String::class.java -> getString(col)
        BigDecimal::class.java -> getBigDecimal(col)
        ByteArray::class.java -> getBytes(col)
        Timestamp::class.java -> getTimestamp(col)
        Date::class.java -> getDate(col)
        Time::class.java -> getTime(col)
        Instant::class.java -> getTimestamp(col)?.toInstant()
        LocalDateTime::class.java -> getTimestamp(col)?.toLocalDateTime()
        LocalDate::class.java -> getDate(col)?.toLocalDate()
        LocalTime::class.java -> getTime(col)?.toLocalTime()
        MonthDay::class.java -> getString(col)?.let { MonthDay.parse(it, formatterMonthDay) }
        YearMonth::class.java -> getString(col)?.let { YearMonth.parse(it, formatterYearMonth) }
        Year::class.java -> Year.of(getInt(col))
        UUID::class.java -> getObject(col) as? UUID
        else -> null
    }
}

fun <E : Any> ResultSet.setField(entity: E, field: Field) {
    val v = getMatchFieldValue(field.name, field.type)
    try {
        when (field.type) {
            Boolean::class.java -> field.setBoolean(entity, v as Boolean)
            Int::class.java -> field.setInt(entity, v as Int)
            Short::class.java -> field.setShort(entity, v as Short)
            Long::class.java -> field.setLong(entity, v as Long)
            Float::class.java -> field.setFloat(entity, v as Float)
            Double::class.java -> field.setDouble(entity, v as Double)
            else -> field.set(entity, v)
        }
    } catch (e: Exception) {
        logger.error("ResultSet.setField[${field.name}]", e)
    }
}

fun <E : Any> ResultSet.setField(entity: E, field: Field, index: Int) {
    try {
        when (field.type) {
            Boolean::class.java -> field.setBoolean(entity, getBoolean(index))
            Int::class.java -> field.setInt(entity, getInt(index))
            Short::class.java -> field.setShort(entity, getShort(index))
            Long::class.java -> field.setLong(entity, getLong(index))
            Float::class.java -> field.setFloat(entity, getFloat(index))
            Double::class.java -> field.setDouble(entity, getDouble(index))
            String::class.java -> field.set(entity, getString(index))
            BigDecimal::class.java -> field.set(entity, getBigDecimal(index))
            ByteArray::class.java -> field.set(entity, getBytes(index))
            Timestamp::class.java -> field.set(entity, getTimestamp(index))
            Date::class.java -> field.set(entity, getDate(index))
            Time::class.java -> field.set(entity, getTime(index))
            Instant::class.java -> field.set(entity, getTimestamp(index)?.toInstant())
            LocalDateTime::class.java -> field.set(entity, getTimestamp(index)?.toLocalDateTime())
            LocalDate::class.java -> field.set(entity, getDate(index)?.toLocalDate())
            LocalTime::class.java -> field.set(entity, getTime(index)?.toLocalTime())
            MonthDay::class.java -> field.set(entity, getString(index)?.let { MonthDay.parse(it, formatterMonthDay) })
            YearMonth::class.java -> field.set(entity, getString(index)?.let { YearMonth.parse(it, formatterYearMonth) })
            Year::class.java -> field.set(entity, Year.of(getInt(index)))
            UUID::class.java -> field.set(entity, getObject(index) as? UUID)
        }
    } catch (e: Exception) {
        logger.error("ResultSet.setFieldIndexed[${field.name}, ${index}]", e)
    }
}

inline fun <reified E : Any> createEntity(row: ResultSet): E {
    val entity = E::class.java.getDeclaredConstructor().newInstance()
    val fields = E::class.java.declaredFields
    fields.forEach { field ->
        field.isAccessible = true
        row.setField(entity, field)
    }
    return entity
}

inline fun <reified E : Any> createEntitySeq(row: ResultSet): E {
    val entity = E::class.java.getDeclaredConstructor().newInstance()
    val fields = E::class.java.declaredFields
    val cnt = row.metaData.columnCount
    fields.forEachIndexed { index, field ->
        field.isAccessible = true
        if ((index + 1) <= cnt) {
            row.setField(entity, field, index + 1)
        }
    }
    return entity
}

inline fun <reified E : Entity<E>, T> Entity<E>.setFieldValue(field: Field, dc: T) {
    // 找不到字段，退出
    val mem = entityClass.members.find { it is KProperty<*> && it.name.conv().lowercase() == field.name.conv().lowercase() && it.returnType.jvmErasure.java == field.type } ?: return
    // 根据不同的类型设置数据
    try {
        when (field.type) {
            Boolean::class.java -> this[mem.name] = field.getBoolean(dc)
            Int::class.java -> this[mem.name] = field.getInt(dc)
            Short::class.java -> this[mem.name] = field.getShort(dc)
            Long::class.java -> this[mem.name] = field.getLong(dc)
            Float::class.java -> this[mem.name] = field.getFloat(dc)
            Double::class.java -> this[mem.name] = field.getDouble(dc)
            else -> this[mem.name] = field.get(dc)
        }
    } catch (e: Exception) {
        logger.error("Entity.setFieldValue[${field.name}]", e)
    }
}

inline fun <reified E : Entity<E>> instanceEntity(): E {
    val fCompanion = E::class.java.getDeclaredField("Companion")
    val objCompanion = fCompanion.get(E::class.java)
    val mInv = objCompanion::class.java.superclass.getDeclaredMethod("invoke")
    return mInv.invoke(objCompanion) as E
}

inline fun <reified E : Entity<E>> dataClassToEntity(dc: Any): E {
    val fields = dc::class.java.declaredFields
    val entity = instanceEntity<E>()
    fields.forEach { field ->
        field.isAccessible = true
        entity.setFieldValue(field, dc)
    }
    return entity
}

inline fun <reified T : Any> entityToDataClass(entity: Entity<*>): T {
    val t = T::class.java.getDeclaredConstructor().newInstance()
    entityFillIntoDataClass(entity, t)
    return t
}

fun entityFillIntoDataClass(entity: Entity<*>, dc: Any) {
    dc::class.java.declaredFields.forEach { field ->
        field.isAccessible = true
        if (entity.properties.containsKey(field.name)) {
            try {
                field.set(dc, entity.properties[field.name])
            } catch (e: Exception) {
                logger.error("entityFillIntoDataClass[${field.name}]", e)
            }
        }
    }
}