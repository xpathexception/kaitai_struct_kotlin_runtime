package io.kaitai.struct

internal typealias IntU1 = Int
internal typealias IntU2 = Int
internal typealias IntU4 = Long
internal typealias IntU8 = Long

internal typealias IntS1 = Byte
internal typealias IntS2 = Short
internal typealias IntS4 = Int
internal typealias IntS8 = Long

internal typealias Bits = Long
internal typealias IntC = Int
internal typealias FloatC = Double

internal inline fun IntU1.toIntS1(): IntS1 = toByte()
internal inline fun IntU2.toIntS2(): IntS2 = toShort()
internal inline fun IntU4.toIntS4(): IntS4 = toInt()
internal inline fun IntS1.toIntU1(): IntU1 = toInt()
internal inline fun IntS2.toIntU2(): IntU2 = toInt()
internal inline fun IntS4.toIntU4(): IntU4 = toLong()
