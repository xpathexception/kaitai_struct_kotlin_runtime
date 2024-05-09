package io.kaitai.struct.typing

import kotlin.experimental.and

@OptIn(ExperimentalUnsignedTypes::class)
inline fun ByteArray.minByte(): IntS1 {
    return toUByteArray().min().toByte().toIntS1()
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun ByteArray.maxByte(): IntS1 {
    return toUByteArray().max().toByte().toIntS1()
}

/**
 * Performs modulo operation between two integers: dividend `a`
 * and divisor `b`. Divisor `b` is expected to be positive. The
 * result is always 0 &lt;= x &lt;= b - 1.
 *
 * @receiver dividend
 * @param b divisor
 *
 * @return result
 */
inline infix fun Int.modPositive(b: Int): Int {
    if (b <= 0) throw ArithmeticException("mod divisor <= 0")
    var r = this % b
    if (r < 0) r += b
    return r
}

/**
 * Performs modulo operation between two integers: dividend `a`
 * and divisor `b`. Divisor `b` is expected to be positive. The
 * result is always 0 &lt;= x &lt;= b - 1.
 *
 * @receiver dividend
 * @param b divisor
 *
 * @return result
 */
inline infix fun Long.modPositive(b: Long): Long {
    if (b <= 0) throw ArithmeticException("mod divisor <= 0")
    var r = this % b
    if (r < 0) r += b
    return r
}

inline infix fun Byte.and(other: Short) = this and other.toByte()
inline infix fun Byte.and(other: Int) = this and other.toByte()
inline infix fun Byte.and(other: Long) = this and other.toByte()

inline infix fun Byte.shr(bitCount: Byte) = shr(bitCount.toInt())
inline infix fun Byte.shr(bitCount: Short) = shr(bitCount.toInt())
inline infix fun Byte.shr(bitCount: Int) = toInt().shr(bitCount).toByte()
inline infix fun Byte.shr(bitCount: Long) = shr(bitCount.toInt())

inline infix fun Byte.shl(bitCount: Byte) = shl(bitCount.toInt())
inline infix fun Byte.shl(bitCount: Short) = shl(bitCount.toInt())
inline infix fun Byte.shl(bitCount: Int) = toInt().shl(bitCount).toByte()
inline infix fun Byte.shl(bitCount: Long) = shl(bitCount.toInt())

inline infix fun Short.shr(bitCount: Byte) = shr(bitCount.toInt())
inline infix fun Short.shr(bitCount: Short) = shr(bitCount.toInt())
inline infix fun Short.shr(bitCount: Int) = toInt().shr(bitCount).toShort()
inline infix fun Short.shr(bitCount: Long) = shr(bitCount.toInt())

inline infix fun Short.shl(bitCount: Byte) = shl(bitCount.toInt())
inline infix fun Short.shl(bitCount: Short) = shl(bitCount.toInt())
inline infix fun Short.shl(bitCount: Int) = toInt().shl(bitCount).toShort()
inline infix fun Short.shl(bitCount: Long) = shl(bitCount.toInt())

inline infix fun Int.shr(bitCount: Byte) = shr(bitCount.toInt())
inline infix fun Int.shr(bitCount: Short) = shr(bitCount.toInt())

//inline infix fun Int.shr(bitCount: Int) = shr(bitCount)
inline infix fun Int.shr(bitCount: Long) = shr(bitCount.toInt())

inline infix fun Int.shl(bitCount: Byte) = shl(bitCount.toInt())
inline infix fun Int.shl(bitCount: Short) = shl(bitCount.toInt())

//inline infix fun Int.shl(bitCount: Int) = shl(bitCount)
inline infix fun Int.shl(bitCount: Long) = shl(bitCount.toInt())

inline infix fun Long.shr(bitCount: Byte) = shr(bitCount.toInt())
inline infix fun Long.shr(bitCount: Short) = shr(bitCount.toInt())

//inline infix fun Long.shr(bitCount: Int) = shr(bitCount)
inline infix fun Long.shr(bitCount: Long) = shr(bitCount.toInt())

inline infix fun Long.shl(bitCount: Byte) = shl(bitCount.toInt())
inline infix fun Long.shl(bitCount: Short) = shl(bitCount.toInt())

//inline infix fun Long.shl(bitCount: Int) = shl(bitCount)
inline infix fun Long.shl(bitCount: Long) = shl(bitCount.toInt())