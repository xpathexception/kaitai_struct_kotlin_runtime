package io.kaitai.struct

/**
 * A custom encoder/decoder interface. Implementing classes can be called from
 * inside a .ksy file using `process: XXX` syntax.
 *
 * Custom processing classes which need to be used from .ksy files that will be
 * compiled in `--read-write` mode should implement this interface. For
 * generated format libraries that are read-only (only capable of parsing, not
 * serialization), it's enough to implement [CustomDecoder].
 */
interface CustomProcessor : CustomDecoder {
    /**
     * Encodes a given byte array, according to some custom algorithm (specific
     * to implementing class) and parameters given in the constructor, returning
     * another byte array.
     *
     *
     * This method is used in serialization. The inverse operation is
     * [.decode], which must return the same byte array as
     * `src` when given the encoded byte array returned by this method.
     *
     * @param src source byte array
     * @return encoded byte array
     */
    fun encode(src: ByteArray): ByteArray
}
