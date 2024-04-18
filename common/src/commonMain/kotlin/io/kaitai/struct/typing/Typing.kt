package io.kaitai.struct.typing

typealias IntU1 = Int
typealias IntU2 = Int
typealias IntU4 = Long
typealias IntU8 = Long

typealias IntS1 = Byte
typealias IntS2 = Short
typealias IntS4 = Int
typealias IntS8 = Long

typealias Bits = Long
typealias IntC = Int
typealias FloatC = Double

inline fun IntU1.toIntS1(): IntS1 = toByte()
inline fun IntU1.toIntU8(): IntU8 = toLong()
inline fun IntU2.toIntS2(): IntS2 = toShort()
inline fun IntU4.toIntS4(): IntS4 = toInt()
inline fun IntU4.toIntU8(): IntU8 = toLong()
inline fun IntS1.toIntU1(): IntU1 = toInt()
inline fun IntS1.toIntU8(): IntU8 = toLong()
inline fun IntS2.toIntU2(): IntU2 = toInt()
inline fun IntS2.toIntU8(): IntU8 = toLong()
inline fun IntS4.toIntU4(): IntU4 = toLong()
inline fun IntS4.toIntS8(): IntS8 = toLong()
