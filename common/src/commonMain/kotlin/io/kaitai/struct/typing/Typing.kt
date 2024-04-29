package io.kaitai.struct.typing

typealias IntU1 = Byte //Byte
typealias IntU2 = Short //Short
typealias IntU4 = Int //Int
typealias IntU8 = Long

typealias IntS1 = Byte
typealias IntS2 = Short
typealias IntS4 = Int
typealias IntS8 = Long

typealias Bits = Long
typealias IntC = Long
typealias FloatC = Double

inline fun Float.toFloatC(): FloatC = toDouble()
inline fun Double.toFloatC(): FloatC = toDouble()

inline fun IntS1.toIntC(): IntC = toLong()
inline fun IntS2.toIntC(): IntC = toLong()
inline fun IntS4.toIntC(): IntC = toLong()
inline fun IntS8.toIntC(): IntC = toLong()

inline fun ULong.toIntC(): IntC = toLong()

// region U to S

inline fun IntU1.toIntS1(): IntS1 = toByte()
inline fun IntU1.toIntS2(): IntS2 = toShort()
inline fun IntU1.toIntS4(): IntS4 = toInt()
inline fun IntU1.toIntS8(): IntS8 = toLong()

inline fun IntU2.toIntS1(): IntS1 = toByte()
inline fun IntU2.toIntS2(): IntS2 = toShort()
inline fun IntU2.toIntS4(): IntS4 = toInt()
inline fun IntU2.toIntS8(): IntS8 = toLong()

inline fun IntU4.toIntS1(): IntS1 = toByte()
inline fun IntU4.toIntS2(): IntS2 = toShort()
inline fun IntU4.toIntS4(): IntS4 = toInt()
inline fun IntU4.toIntS8(): IntS8 = toLong()

inline fun IntU8.toIntS1(): IntS1 = toByte()
inline fun IntU8.toIntS2(): IntS2 = toShort()
inline fun IntU8.toIntS4(): IntS4 = toInt()
inline fun IntU8.toIntS8(): IntS8 = toLong()

//endregion U to S

//region S to U

inline fun IntS1.toIntU1(): IntU1 = toByte()
inline fun IntS1.toIntU2(): IntU2 = toShort()
inline fun IntS1.toIntU4(): IntU4 = toInt()
inline fun IntS1.toIntU8(): IntU8 = toLong()

inline fun IntS2.toIntU1(): IntU1 = toByte()
inline fun IntS2.toIntU2(): IntU2 = toShort()
inline fun IntS2.toIntU4(): IntU4 = toInt()
inline fun IntS2.toIntU8(): IntU8 = toLong()

inline fun IntS4.toIntU1(): IntU1 = toByte()
inline fun IntS4.toIntU2(): IntU2 = toShort()
inline fun IntS4.toIntU4(): IntU4 = toInt()
inline fun IntS4.toIntU8(): IntU8 = toLong()

inline fun IntS8.toIntU1(): IntU1 = toByte()
inline fun IntS8.toIntU2(): IntU2 = toShort()
inline fun IntS8.toIntU4(): IntU4 = toInt()
inline fun IntS8.toIntU8(): IntU8 = toLong()

//endregion S to U

//region S to S

//inline fun IntS1.toIntS1(): IntS1 = toByte()
//inline fun IntS1.toIntS2(): IntS2 = toShort()
//inline fun IntS1.toIntS4(): IntS4 = toInt()
//inline fun IntS1.toIntS8(): IntS8 = toLong()

//inline fun IntS2.toIntS1(): IntS1 = toByte()
//inline fun IntS2.toIntS2(): IntS2 = toShort()
//inline fun IntS2.toIntS4(): IntS4 = toInt()
//inline fun IntS2.toIntS8(): IntS8 = toLong()

//inline fun IntS4.toIntS1(): IntS1 = toByte()
//inline fun IntS4.toIntS2(): IntS2 = toShort()
//inline fun IntS4.toIntS4(): IntS4 = toInt()
//inline fun IntS4.toIntS8(): IntS8 = toLong()

//inline fun IntS8.toIntS1(): IntS1 = toByte()
//inline fun IntS8.toIntS2(): IntS2 = toShort()
//inline fun IntS8.toIntS4(): IntS4 = toInt()
//inline fun IntS8.toIntS8(): IntS8 = toLong()

//endregion S to S

//region U to U

//inline fun IntU1.toIntU1(): IntU1 = toInt()
//inline fun IntU1.toIntU2(): IntU2 = toInt()
//inline fun IntU1.toIntU4(): IntU4 = toLong()
//inline fun IntU1.toIntU8(): IntU8 = toLong()

//inline fun IntU2.toIntU1(): IntU1 = toInt()
//inline fun IntU2.toIntU2(): IntU2 = toInt()
//inline fun IntU2.toIntU4(): IntU4 = toLong()
//inline fun IntU2.toIntU8(): IntU8 = toLong()

//inline fun IntU4.toIntU1(): IntU1 = toInt()
//inline fun IntU4.toIntU2(): IntU2 = toInt()
//inline fun IntU4.toIntU4(): IntU4 = toLong()
//inline fun IntU4.toIntU8(): IntU8 = toLong()

//inline fun IntU8.toIntU1(): IntU1 = toInt()
//inline fun IntU8.toIntU2(): IntU2 = toInt()
//inline fun IntU8.toIntU4(): IntU4 = toLong()
//inline fun IntU8.toIntU8(): IntU8 = toLong()

//endregion U to U
