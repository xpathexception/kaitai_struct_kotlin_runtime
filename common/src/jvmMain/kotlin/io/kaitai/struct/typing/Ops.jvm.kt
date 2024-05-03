package io.kaitai.struct.typing

actual fun ByteArray.decodeToString(
    startIndex: Int,
    endIndex: Int,
    throwOnInvalidSequence: Boolean,
    encoding: String,
): String {
    val charset = when (encoding) {
        "ISO-8859-1" -> Charsets.ISO_8859_1
        "ASCII" -> Charsets.US_ASCII
        "UTF-16BE" -> Charsets.UTF_16BE
        "UTF-16LE" -> Charsets.UTF_16LE
        "UTF-8" -> Charsets.UTF_8
        else -> null
    }

    return if (charset != null) {
        String(this, charset)
    } else {
        decodeToString(startIndex, endIndex, throwOnInvalidSequence)
    }
}