package io.kaitai.struct

/**
 * A custom decoder interface. Implementing classes can be called from inside a
 * .ksy file using `process: XXX` syntax.
 *
 *
 * This interface is sufficient for custom processing routines that will only be
 * used from generated format libraries that are read-only (only capable of
 * parsing, not serialization). To support generated source files compiled in
 * `--read-write` mode, implement [CustomProcessor] instead.
 */
interface CustomDecoder {
    /**
     * Decodes a given byte array, according to some custom algorithm
     * (specific to implementing class) and parameters given in the
     * constructor, returning another byte array.
     *
     * This method is used in parsing. Its counterpart is
     * [CustomProcessor.encode], which is used in serialization.
     *
     * @param src source byte array
     * @return decoded byte array
     */
    fun decode(src: ByteArray): ByteArray
}
