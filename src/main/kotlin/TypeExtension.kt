@file:Suppress("unused")

package com.isyscore.kotlin.common

import java.io.Serializable

typealias T4<A, B, C, D> = Quadruple<A, B, C, D>
typealias T5<A, B, C, D, E> = Pentuple<A, B, C, D, E>
typealias T6<A, B, C, D, E, F> = Hextuple<A, B, C, D, E, F>
typealias T7<A, B, C, D, E, F, G> = Septuple<A, B, C, D, E, F, G>
typealias T8<A, B, C, D, E, F, G, H> = Octuple<A, B, C, D, E, F, G, H>
typealias T9<A, B, C, D, E, F, G, H, I> = Nonuple<A, B, C, D, E, F, G, H, I>
typealias T10<A, B, C, D, E, F, G, H, I, J> = Decuple<A, B, C, D, E, F, G, H, I, J>
typealias T11<A, B, C, D, E, F, G, H, I, J, K> = Undecuple<A, B, C, D, E, F, G, H, I, J, K>
typealias T12<A, B, C, D, E, F, G, H, I, J, K, L> = Duodecuple<A, B, C, D, E, F, G, H, I, J, K, L>

data class Quadruple<out A, out B, out C, out D>(val first: A, val second: B, val third: C, val forth: D) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth)"
}

fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth)

data class Pentuple<out A, out B, out C, out D, out E>(val first: A, val second: B, val third: C, val forth: D, val fifth: E) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth)"
}

fun <T> Pentuple<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth)

data class Hextuple<out A, out B, out C, out D, out E, out F>(val first: A, val second: B, val third: C, val forth: D, val fifth: E, val sixth: F) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth, $sixth)"
}

fun <T> Hextuple<T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth, sixth)

data class Septuple<out A, out B, out C, out D, out E, out F, out G>(val first: A, val second: B, val third: C, val forth: D, val fifth: E, val sixth: F, val seventh: G) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth, $sixth, $seventh)"
}

fun <T> Septuple<T, T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth, sixth, seventh)

data class Octuple<out A, out B, out C, out D, out E, out F, out G, out H>(val first: A, val second: B, val third: C, val forth: D, val fifth: E, val sixth: F, val seventh: G, val eighth: H) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth, $sixth, $seventh, $eighth)"
}

fun <T> Octuple<T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth, sixth, seventh, eighth)

data class Nonuple<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val first: A, val second: B, val third: C, val forth: D, val fifth: E, val sixth: F, val seventh: G, val eighth: H, val ninth: I) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth, $sixth, $seventh, $eighth, $ninth)"
}

fun <T> Nonuple<T, T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth, sixth, seventh, eighth, ninth)

data class Decuple<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val first: A, val second: B, val third: C, val forth: D, val fifth: E, val sixth: F, val seventh: G, val eighth: H, val ninth: I, val tenth: J) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth, $sixth, $seventh, $eighth, $ninth, $tenth)"
}

fun <T> Decuple<T, T, T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth, sixth, seventh, eighth, ninth, tenth)

data class Undecuple<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(val first: A, val second: B, val third: C, val forth: D, val fifth: E, val sixth: F, val seventh: G, val eighth: H, val ninth: I, val tenth: J, val eleventh: K) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth, $sixth, $seventh, $eighth, $ninth, $tenth, $eleventh)"
}

fun <T> Undecuple<T, T, T, T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh)

data class Duodecuple<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(val first: A, val second: B, val third: C, val forth: D, val fifth: E, val sixth: F, val seventh: G, val eighth: H, val ninth: I, val tenth: J, val eleventh: K, val twelvth: L) : Serializable {
    override fun toString(): String = "($first, $second, $third, $forth, $fifth, $sixth, $seventh, $eighth, $ninth, $tenth, $eleventh, $twelvth)"
}

fun <T> Duodecuple<T, T, T, T, T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelvth)


infix fun <A, B> A.join(that: B): Pair<A, B> = Pair(this, that)
infix fun <A1, A2, A : Pair<A1, A2>, B> A.join(that: B): Triple<A1, A2, B> = Triple(this.first, this.second, that)
infix fun <A1, A2, A3, A : Triple<A1, A2, A3>, B> A.join(that: B): Quadruple<A1, A2, A3, B> = Quadruple(this.first, this.second, this.third, that)
infix fun <A1, A2, A3, A4, A : Quadruple<A1, A2, A3, A4>, B> A.join(that: B): Pentuple<A1, A2, A3, A4, B> = Pentuple(this.first, this.second, this.third, this.forth, that)
infix fun <A1, A2, A3, A4, A5, A : Pentuple<A1, A2, A3, A4, A5>, B> A.join(that: B): Hextuple<A1, A2, A3, A4, A5, B> = Hextuple(this.first, this.second, this.third, this.forth, this.fifth, that)
infix fun <A1, A2, A3, A4, A5, A6, A : Hextuple<A1, A2, A3, A4, A5, A6>, B> A.join(that: B): Septuple<A1, A2, A3, A4, A5, A6, B> = Septuple(this.first, this.second, this.third, this.forth, this.fifth, this.sixth, that)
infix fun <A1, A2, A3, A4, A5, A6, A7, A : Septuple<A1, A2, A3, A4, A5, A6, A7>, B> A.join(that: B): Octuple<A1, A2, A3, A4, A5, A6, A7, B> = Octuple(this.first, this.second, this.third, this.forth, this.fifth, this.sixth, this.seventh, that)
infix fun <A1, A2, A3, A4, A5, A6, A7, A8, A : Octuple<A1, A2, A3, A4, A5, A6, A7, A8>, B> A.join(that: B): Nonuple<A1, A2, A3, A4, A5, A6, A7, A8, B> = Nonuple(this.first, this.second, this.third, this.forth, this.fifth, this.sixth, this.seventh, this.eighth, that)
infix fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A : Nonuple<A1, A2, A3, A4, A5, A6, A7, A8, A9>, B> A.join(that: B): Decuple<A1, A2, A3, A4, A5, A6, A7, A8, A9, B> = Decuple(this.first, this.second, this.third, this.forth, this.fifth, this.sixth, this.seventh, this.eighth, this.ninth, that)
infix fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A : Decuple<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10>, B> A.join(that: B): Undecuple<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> = Undecuple(this.first, this.second, this.third, this.forth, this.fifth, this.sixth, this.seventh, this.eighth, this.ninth, this.tenth, that)
infix fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A : Undecuple<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11>, B> A.join(that: B): Duodecuple<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, B> = Duodecuple(this.first, this.second, this.third, this.forth, this.fifth, this.sixth, this.seventh, this.eighth, this.ninth, this.tenth, this.eleventh, that)