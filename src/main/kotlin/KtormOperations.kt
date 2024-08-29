package com.isyscore.kotlin.common

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.*
import org.ktorm.schema.*

infix fun String.like(expr: ColumnDeclaring<String>): BinaryExpression<Boolean> =
    BinaryExpression(BinaryExpressionType.LIKE, ArgumentExpression(this, SqlType.of<String>()!!), expr.asExpression(), BooleanSqlType)

infix fun String.notLike(expr: ColumnDeclaring<String>): BinaryExpression<Boolean> =
    BinaryExpression(BinaryExpressionType.NOT_LIKE, ArgumentExpression(this, SqlType.of<String>()!!), expr.asExpression(), BooleanSqlType)

inline infix fun <reified T : Any> T.inList(query: Query): InListExpression =
    InListExpression(left = ArgumentExpression(this, SqlType.of<T>()!!), query = query.expression)

inline infix fun <reified T : Any> T.inList(list: Collection<T>): InListExpression =
    InListExpression(left = ArgumentExpression(this, SqlType.of<T>()!!), values = list.map { ArgumentExpression(it, SqlType.of<T>()!!) })

inline infix fun <reified T : Any> T.notInList(query: Query): InListExpression =
    InListExpression(left = ArgumentExpression(this, SqlType.of<T>()!!), query = query.expression, notInList = true)

inline infix fun <reified T : Any> T.notInList(list: Collection<T>): InListExpression =
    InListExpression(left = ArgumentExpression(this, SqlType.of<T>()!!), values = list.map { ArgumentExpression(it, SqlType.of<T>()!!) }, notInList = true)

val argumentTrue = ArgumentExpression(true, BooleanSqlType)
val argumentFalse = ArgumentExpression(false, BooleanSqlType)

val alwaysTrue: ColumnDeclaring<Boolean> = BinaryExpression(BinaryExpressionType.EQUAL, argumentTrue, argumentTrue, BooleanSqlType)
val alwaysFalse: ColumnDeclaring<Boolean> = BinaryExpression(BinaryExpressionType.EQUAL, argumentTrue, argumentFalse, BooleanSqlType)

operator fun ColumnDeclaring<Boolean>.plus(c: ColumnDeclaring<Boolean>): ColumnDeclaring<Boolean> = this and c

operator fun BinaryExpression<Boolean>.plus(c: ColumnDeclaring<Boolean>): BinaryExpression<Boolean> = this and c

operator fun ColumnDeclaring<Boolean>.minus(c: ColumnDeclaring<Boolean>): ColumnDeclaring<Boolean> = this or c

operator fun BinaryExpression<Boolean>.minus(c: ColumnDeclaring<Boolean>): BinaryExpression<Boolean> = this or c

fun <T : Any> BinaryExpression<T>.expr(): ColumnDeclaring<T> = this

fun <T : Any> UnaryExpression<T>.expr(): ColumnDeclaring<T> = this

fun BetweenExpression.expr(): ColumnDeclaring<Boolean> = this

fun InListExpression.expr(): ColumnDeclaring<Boolean> = this

fun ExistsExpression.expr(): ColumnDeclaring<Boolean> = this

fun <T : Any> CastingExpression<T>.expr(): ColumnDeclaring<T> = this

private const val TEMP_TABLE = "__tmp__"

class TmpTable : BaseTable<Any>(TEMP_TABLE) {
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): Any {
        return createEntity(row, withReferences)
    }
}

val tmpTable = TmpTable()

inline fun <reified T : Any> column(name: String, table: BaseTable<*>? = null): Column<T> =
    Column(tmpTable, if (table == null) name.unconv() else "${table.tableName}_${name.unconv()}", sqlType = SqlType.of<T>()!!)

fun Database.from(query: Query): QuerySource {
    return QuerySource(this, tmpTable, (query.expression as SelectExpression))
}

fun QuerySource.tempSelect(vararg columns: ColumnDeclaring<*>): Query {
    val declarations = columns.map { it.asAliasedExpression() }
    return Query(database, SelectExpression(columns = declarations, from = expression, tableAlias = TEMP_TABLE))
}

fun QuerySource.tempSelect(columns: Collection<ColumnDeclaring<*>>): Query {
    val declarations = columns.map { it.asAliasedExpression() }
    return Query(database, SelectExpression(columns = declarations, from = expression, tableAlias = TEMP_TABLE))
}

internal fun <T : Any> ColumnDeclaring<T>.asAliasedExpression(): ColumnDeclaringExpression<T> = when (this) {
    is ColumnDeclaringExpression -> this
    is Column -> this.aliased(label)
    else -> this.aliased(null)
}

inline fun <reified T : Any> T?.asArg(): ArgumentExpression<T> = ArgumentExpression(this, SqlType.of<T>()!!)