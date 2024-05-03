package io.kaitai.struct.typing

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.CoreFoundation.*
import platform.Foundation.CFBridgingRelease

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.decodeToString(
    startIndex: Int,
    endIndex: Int,
    throwOnInvalidSequence: Boolean,
    encoding: String,
): String {
    val cfEncoding = when (encoding) {
        "UTF-8" -> kCFStringEncodingUTF8
        "ISO-8859-1" -> kCFStringEncodingISOLatin1
        "ASCII" -> kCFStringEncodingASCII
        "UTF-16BE" -> kCFStringEncodingUTF16BE
        "UTF-16LE" -> kCFStringEncodingUTF16LE
        else -> null
    }

    if (cfEncoding == null) return decodeToString(startIndex, endIndex, throwOnInvalidSequence)

    val size = endIndex - startIndex

    return memScoped {
        CFBridgingRelease(
            CFStringCreateWithBytes(
                alloc = null,
                bytes = this@decodeToString.drop(startIndex).take(size).toByteArray().asUByteArray().toCValues(),
                numBytes = size.toLong(),
                encoding = cfEncoding,
                isExternalRepresentation = true
            )
        ) as String
    }
}
