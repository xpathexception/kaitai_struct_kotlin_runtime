package io.kaitai.struct.typing

expect fun ByteArray.decodeToString(
    startIndex: Int = 0,
    endIndex: Int = size,
    throwOnInvalidSequence: Boolean = false,
    encoding: String,
): String
