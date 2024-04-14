package io.kaitai.struct

inline infix fun Byte.shr(bitCount: Int): Byte {
    return toInt().shr(bitCount).toByte()
}

inline infix fun Byte.shl(bitCount: Int): Byte {
    return toInt().shl(bitCount).toByte()
}

inline infix fun Short.shr(bitCount: Int): Short {
    return toInt().shr(bitCount).toShort()
}

inline infix fun Short.shl(bitCount: Int): Short {
    return toInt().shl(bitCount).toShort()
}

inline infix fun UByte.shr(bitCount: Int): UByte {
    return toUInt().shr(bitCount).toUByte()
}

inline infix fun UByte.shl(bitCount: Int): UByte {
    return toUInt().shl(bitCount).toUByte()
}

inline infix fun UShort.shr(bitCount: Int): UShort {
    return toUInt().shr(bitCount).toUShort()
}

inline infix fun UShort.shl(bitCount: Int): UShort {
    return toUInt().shl(bitCount).toUShort()
}
