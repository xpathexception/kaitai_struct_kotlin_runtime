package io.kaitai.struct

inline infix fun <T : Comparable<T>> T?.lt(other: T?): Boolean? {
    return if (this == null || other == null) null else this < other
}

inline infix fun <T : Comparable<T>> T?.lte(other: T?): Boolean? {
    return when {
        this == null && other == null -> true
        this == null || other == null -> null
        else -> this <= other
    }
}

inline infix fun <T : Comparable<T>> T?.gt(other: T?): Boolean? {
    return if (this == null || other == null) null else this > other
}

inline infix fun <T : Comparable<T>> T?.gte(other: T?): Boolean? {
    return when {
        this == null && other == null -> true
        this == null || other == null -> null
        else -> this >= other
    }
}
