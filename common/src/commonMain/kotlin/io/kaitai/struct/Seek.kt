package io.kaitai.struct

inline fun <reified T> KaitaiStream.lookupAt(offset: Long, crossinline block: () -> T): T {
    val rememberPosition = pos
    seek(offset)
    return block().also { seek(rememberPosition) }
}

inline fun <reified T> KaitaiStream.lookup(crossinline block: () -> T): T {
    val rememberPosition = pos
    return block().also { seek(rememberPosition) }
}
