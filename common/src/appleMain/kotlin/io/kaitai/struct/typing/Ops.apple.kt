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
        "ASCII" -> kCFStringEncodingASCII
        "IBM437" -> kCFStringEncodingDOSLatinUS.toUInt()
        "IBM866" -> kCFStringEncodingDOSRussian.toUInt()
        "ISO-8859-1" -> kCFStringEncodingISOLatin1
        "ISO-8859-2" -> kCFStringEncodingISOLatin2.toUInt()
        "ISO-8859-3" -> kCFStringEncodingISOLatin3.toUInt()
        "ISO-8859-4" -> kCFStringEncodingISOLatin4.toUInt()
        "ISO-8859-5" -> kCFStringEncodingISOLatinCyrillic.toUInt()
        "ISO-8859-6" -> kCFStringEncodingISOLatinArabic.toUInt()
        "ISO-8859-7" -> kCFStringEncodingISOLatinGreek.toUInt()
        "ISO-8859-8" -> kCFStringEncodingISOLatinHebrew.toUInt()
        "ISO-8859-9" -> kCFStringEncodingISOLatin5.toUInt()
        "ISO-8859-10" -> kCFStringEncodingISOLatin6.toUInt()
        "ISO-8859-11" -> kCFStringEncodingISOLatinThai.toUInt()
        "ISO-8859-13" -> kCFStringEncodingISOLatin7.toUInt()
        "ISO-8859-14" -> kCFStringEncodingISOLatin8.toUInt()
        "ISO-8859-15" -> kCFStringEncodingISOLatin9.toUInt()
        "ISO-8859-16" -> kCFStringEncodingISOLatin10.toUInt()
        "SJIS" -> kCFStringEncodingShiftJIS.toUInt()
        "UTF-8" -> kCFStringEncodingUTF8
        "UTF-16BE" -> kCFStringEncodingUTF16BE
        "UTF-16LE" -> kCFStringEncodingUTF16LE
        "windows-1250" -> kCFStringEncodingWindowsLatin2.toUInt()
        "windows-1251" -> kCFStringEncodingWindowsCyrillic.toUInt()
        "windows-1252" -> kCFStringEncodingWindowsLatin1
        "windows-1253" -> kCFStringEncodingWindowsGreek.toUInt()
        "windows-1254" -> kCFStringEncodingWindowsLatin5.toUInt()
        "windows-1255" -> kCFStringEncodingWindowsHebrew.toUInt()
        "windows-1256" -> kCFStringEncodingWindowsArabic.toUInt()
        "windows-1257" -> kCFStringEncodingWindowsBalticRim.toUInt()
        "windows-1258" -> kCFStringEncodingWindowsVietnamese.toUInt()
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
