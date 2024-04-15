package io.kaitai.struct

import okio.*
import kotlin.experimental.xor
import kotlin.jvm.JvmField
import kotlin.math.min

/**
 * KaitaiStream provides implementation of
 * [Kaitai Stream API](https://doc.kaitai.io/stream_api.html)
 * for Java.
 *
 * It provides a wide variety of simple methods to read (parse) binary
 * representations of primitive types, such as integer and floating
 * point numbers, byte arrays and strings, and also provides stream
 * positioning / navigation methods with unified cross-language and
 * cross-toolkit semantics.
 *
 * This is abstract class, which serves as an interface description and
 * a few default method implementations, which are believed to be common
 * for all (or at least most) implementations. Different implementations
 * of this interface may provide way to parse data from local files,
 * in-memory buffers or arrays, remote files, network streams, etc.
 *
 * Typically, end users won't access any of these Kaitai Stream classes
 * manually, but would describe a binary structure format using .ksy language
 * and then would use Kaitai Struct compiler to generate source code in
 * desired target language.  That code, in turn, would use this class
 * and API to do the actual parsing job.
 */
abstract class KaitaiStream : Closeable {
    @JvmField
    protected var bitsLeft: Int = 0
    protected var bits: Long = 0
    protected var bitsLe: Boolean = false

    @JvmField
    protected var bitsWriteMode: Boolean = false
    protected var childStreams: MutableList<KaitaiStream> = ArrayList()

    var writeBackHandler: WriteBackHandler? = null

    @Throws(IOException::class)
    abstract override fun close()

    //region Stream positioning

    /**
     * Get current position of a stream pointer.
     * @return pointer position, number of bytes from the beginning of the stream
     */
    abstract val pos: Long

    /**
     * Get total size of the stream in bytes.
     * @return size of the stream in bytes
     */
    abstract val size: Long

    /**
     * Check if stream pointer is at the end of stream.
     * @return true if we are located at the end of the stream
     */
    abstract val isEof: Boolean

    /**
     * Set stream pointer to designated position (int).
     * @param newPos new position (offset in bytes from the beginning of the stream)
     */
    abstract fun seek(newPos: Int)

    /**
     * Set stream pointer to designated position (long).
     *
     * @param newPos new position (offset in bytes from the beginning of the stream)
     */
    abstract fun seek(newPos: Long)

    //endregion

    //region Reading

    //region Integer numbers

    //region Signed

    abstract fun readS1(): IntS1

    //region Big-endian

    abstract fun readS2be(): IntS2

    abstract fun readS4be(): IntS4

    abstract fun readS8be(): IntS8

    //endregion

    //region Little-endian

    abstract fun readS2le(): IntS2

    abstract fun readS4le(): IntS4

    abstract fun readS8le(): IntS8

    //endregion

    //endregion

    //region Unsigned

    abstract fun readU1(): IntU1

    //region Big-endian

    abstract fun readU2be(): IntU2

    abstract fun readU4be(): IntU4

    abstract fun readU8be(): IntU8

    //endregion

    //region Little-endian

    abstract fun readU2le(): IntU2

    abstract fun readU4le(): IntU4

    abstract fun readU8le(): IntU8

    //endregion

    //endregion

    //endregion

    //region Floating point numbers

    //region Big-endian

    abstract fun readF4be(): Float

    abstract fun readF8be(): Double

    //endregion

    //region Little-endian

    abstract fun readF4le(): Float

    abstract fun readF8le(): Double

    //endregion

    //endregion

    //region Unaligned bit values

    fun alignToByte() {
        bitsLeft = 0
        bits = 0
    }

    fun readBitsIntBe(n: Int): Long {
        // TODO: check if implemented in okio
        bitsWriteMode = false

        var res: Long = 0

        val bitsNeeded = n - bitsLeft
        bitsLeft = -bitsNeeded and 7 // `-bitsNeeded mod 8`

        if (bitsNeeded > 0) {
            // 1 bit  => 1 byte
            // 8 bits => 1 byte
            // 9 bits => 2 bytes
            val bytesNeeded = ((bitsNeeded - 1) / 8) + 1 // `ceil(bitsNeeded / 8)`
            val buf = readBytesNotAligned(bytesNeeded.toLong())
            for (b in buf) {
                // `b` is signed byte, convert to unsigned using the "& 0xff" trick
                res = res shl 8 or (b.toInt() and 0xff).toLong()
            }

            val newBits = res
            res = res ushr bitsLeft or (if (bitsNeeded < 64) bits shl bitsNeeded else 0)
            bits = newBits // will be masked at the end of the function
        } else {
            res = bits ushr -bitsNeeded // shift unneeded bits out
        }

        val mask = (1L shl bitsLeft) - 1 // `bitsLeft` is in range 0..7, so `(1L << 64)` does not have to be considered
        bits = bits and mask

        return res
    }

    fun readBitsIntLe(n: Int): Long {
        // TODO: check if implemented in okio
        bitsWriteMode = false

        var res: Long = 0
        val bitsNeeded = n - bitsLeft

        if (bitsNeeded > 0) {
            // 1 bit  => 1 byte
            // 8 bits => 1 byte
            // 9 bits => 2 bytes
            val bytesNeeded = ((bitsNeeded - 1) / 8) + 1 // `ceil(bitsNeeded / 8)`
            val buf = readBytesNotAligned(bytesNeeded.toLong())
            for (i in 0 until bytesNeeded) {
                // `buf[i]` is signed byte, convert to unsigned using the "& 0xff" trick
                res = res or (((buf[i].toInt() and 0xff).toLong()) shl (i * 8))
            }

            // NB: in Java, bit shift operators on left-hand operand of type `long` work
            // as if the right-hand operand were subjected to `& 63` (`& 0b11_1111`) (see
            // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.19),
            // so `res >>> 64` is equivalent to `res >>> 0` (but we don't want that)
            val newBits = if (bitsNeeded < 64) res ushr bitsNeeded else 0
            res = res shl bitsLeft or bits
            bits = newBits
        } else {
            res = bits
            bits = bits ushr n
        }

        bitsLeft = -bitsNeeded and 7 // `-bitsNeeded mod 8`

        if (n < 64) {
            val mask = (1L shl n) - 1
            res = res and mask
        }
        // if `n == 64`, do nothing
        return res
    }

    //endregion

    //region Byte arrays

    /**
     * Reads designated number of bytes from the stream.
     *
     * @param n number of bytes to read
     * @return read bytes as byte array
     */
    fun readBytes(n: Long): ByteArray {
        alignToByte()
        return readBytesNotAligned(n)
    }

    /**
     * Internal method to read the specified number of bytes from the stream. Unlike
     * [.readBytes], it doesn't align the bit position to the next byte
     * boundary.
     * @param n number of bytes to read
     * @return read bytes as a byte array
     */
    protected abstract fun readBytesNotAligned(n: Long): ByteArray

    /**
     * Reads all the remaining bytes in a stream as byte array.
     * @return all remaining bytes in a stream as byte array
     */
    abstract fun readBytesFull(): ByteArray

    abstract fun readBytesTerm(term: Byte, includeTerm: Boolean, consumeTerm: Boolean, eosError: Boolean): ByteArray

    /**
     * Checks if supplied number of bytes is a valid number of elements for Java
     * byte array: converts it to int, if it is, or throws an exception if it is not.
     *
     * @param n number of bytes for byte array as long
     * @return number of bytes, converted to int
     */
    protected fun toByteArrayLength(n: Long): Int {
        // TODO: java
        require(n <= Int.MAX_VALUE) { "Java byte arrays can be indexed only up to 31 bits, but $n size was requested" }
        require(n >= 0) { "Byte array size can't be negative, but $n size was requested" }
        return n.toInt()
    }

    //endregion

    //endregion

    //region Writing

    protected fun ensureBytesLeftToWrite(n: Long, pos: Long) {
        val bytesLeft = size - pos
        if (n > bytesLeft) {
            throw RuntimeException(
                EOFException("requested to write $n bytes, but only $bytesLeft bytes left in the stream")
            )
        }
    }

    //region Integer numbers

    //region Signed

    /**
     * Writes one signed 1-byte integer.
     */
    abstract fun writeS1(v: IntS1)

    //region Big-endian

    abstract fun writeS2be(v: IntS2)

    abstract fun writeS4be(v: IntS4)

    abstract fun writeS8be(v: IntS8)

    //endregion

    //region Little-endian

    abstract fun writeS2le(v: IntS2)

    abstract fun writeS4le(v: IntS4)

    abstract fun writeS8le(v: IntS8)

    //endregion

    //endregion

    //region Unsigned

    abstract fun writeU1(v: IntU1)

    //region Big-endian

    abstract fun writeU2be(v: IntU2)

    abstract fun writeU4be(v: IntU4)

    abstract fun writeU8be(v: IntU8)

    //endregion

    //region Little-endian

    abstract fun writeU2le(v: IntU2)

    abstract fun writeU4le(v: IntU4)

    abstract fun writeU8le(v: IntU8)

    //endregion

    //endregion

    //endregion

    //region Floating point numbers

    //region Big-endian

    abstract fun writeF4be(v: Float)

    abstract fun writeF8be(v: Double)

    //endregion

    //region Little-endian

    abstract fun writeF4le(v: Float)

    abstract fun writeF8le(v: Double)

    //endregion

    //endregion

    //region Unaligned bit values

    fun writeAlignToByte() {
        if (bitsLeft > 0) {
            var b = bits.toByte()
            if (!bitsLe) {
                b = (b.toInt() shl 8 - bitsLeft).toByte()
            }
            // See https://github.com/kaitai-io/kaitai_struct_python_runtime/blob/704995ac/kaitaistruct.py#L572-L596
            // for an explanation of why we call alignToByte() before
            // writeBytesNotAligned().
            alignToByte()
            writeBytesNotAligned(byteArrayOf(b))
        }
    }

    /*
        Example 1 (bytesToWrite > 0):

        old bitsLeft = 5
            | |          new bitsLeft = 18 mod 8 = 2
           /   \             /\
          |01101xxx|xxxxxxxx|xx......|
           \    \             /
            \    \__ n = 13 _/
             \              /
              \____________/
             bitsToWrite = 18  ->  bytesToWrite = 2

        ---

        Example 2 (bytesToWrite == 0):

           old bitsLeft = 1
                |   |
                 \ /
        |01101100|1xxxxx..|........|
                 / \___/\
                /  n = 5 \
               /__________\
             bitsToWrite = 6  ->  bytesToWrite = 0,
                                  new bitsLeft = 6 mod 8 = 6
     */
    fun writeBitsIntBe(n: Int, value: Long) {
        var value = value
        bitsLe = false
        bitsWriteMode = true

        if (n < 64) {
            val mask = (1L shl n) - 1
            value = value and mask
        }

        // if `n == 64`, do nothing
        val bitsToWrite = bitsLeft + n
        val bytesNeeded = ((bitsToWrite - 1) / 8) + 1 // `ceil(bitsToWrite / 8)`

        // pos() respects the `bitsLeft` field (it returns the stream position
        // as if it were already aligned on a byte boundary), which ensures that
        // we report the same numbers of bytes here as readBitsInt*() methods
        // would.
        ensureBytesLeftToWrite((bytesNeeded - (if (bitsLeft > 0) 1 else 0)).toLong(), pos.toLong())

        val bytesToWrite = bitsToWrite / 8
        bitsLeft = bitsToWrite and 7 // `bitsToWrite mod 8`

        if (bytesToWrite > 0) {
            val buf = ByteArray(bytesToWrite)

            val mask =
                (1L shl bitsLeft) - 1 // `bitsLeft` is in range 0..7, so `(1L << 64)` does not have to be considered
            val newBits = value and mask
            value = value ushr bitsLeft or (if (n - bitsLeft < 64) bits shl (n - bitsLeft) else 0)
            bits = newBits

            for (i in bytesToWrite - 1 downTo 0) {
                buf[i] = (value and 0xffL).toByte()
                value = value ushr 8
            }
            writeBytesNotAligned(buf)
        } else {
            bits = bits shl n or value
        }
    }

    /*
        Example 1 (bytesToWrite > 0):

        n = 13

           old bitsLeft = 5
               | |             new bitsLeft = 18 mod 8 = 2
              /   \                /\
          |xxx01101|xxxxxxxx|......xx|
           \               /      / /
            ---------------       --
                      \           /
                     bitsToWrite = 18  ->  bytesToWrite = 2

        ---

        Example 2 (bytesToWrite == 0):

                  old bitsLeft = 1
                       |   |
                        \ /
        |01101100|..xxxxx1|........|
                   /\___/ \
                  / n = 5  \
                 /__________\
               bitsToWrite = 6  ->  bytesToWrite = 0,
                                    new bitsLeft = 6 mod 8 = 6
     */
    fun writeBitsIntLe(n: Int, value: Long) {
        var value = value
        bitsLe = true
        bitsWriteMode = true

        val bitsToWrite = bitsLeft + n
        val bytesNeeded = ((bitsToWrite - 1) / 8) + 1 // `ceil(bitsToWrite / 8)`

        // pos() respects the `bitsLeft` field (it returns the stream position
        // as if it were already aligned on a byte boundary), which ensures that
        // we report the same numbers of bytes here as readBitsInt*() methods
        // would.
        ensureBytesLeftToWrite((bytesNeeded - (if (bitsLeft > 0) 1 else 0)).toLong(), pos.toLong())

        val bytesToWrite = bitsToWrite / 8
        val oldBitsLeft = bitsLeft
        bitsLeft = bitsToWrite and 7 // `bitsToWrite mod 8`

        if (bytesToWrite > 0) {
            val buf = ByteArray(bytesToWrite)

            val newBits = if (n - bitsLeft < 64) value ushr (n - bitsLeft) else 0
            value = value shl oldBitsLeft or bits
            bits = newBits

            for (i in 0 until bytesToWrite) {
                buf[i] = (value and 0xffL).toByte()
                value = value ushr 8
            }
            writeBytesNotAligned(buf)
        } else {
            bits = bits or (value shl oldBitsLeft)
        }

        val mask = (1L shl bitsLeft) - 1 // `bitsLeft` is in range 0..7, so `(1L << 64)` does not have to be considered
        bits = bits and mask
    }

    //endregion

    //region Byte arrays

    /**
     * Writes given byte array to the stream.
     *
     * @param buf byte array to write
     */
    fun writeBytes(buf: ByteArray) {
        writeAlignToByte()
        writeBytesNotAligned(buf)
    }

    /**
     * Internal method to write the given byte array to the stream. Unlike
     * [writeBytes], it doesn't align the bit position to the next byte
     * boundary.
     *
     * @param buf byte array to write
     */
    protected abstract fun writeBytesNotAligned(buf: ByteArray)

    fun writeBytesLimit(buf: ByteArray, size: Long, term: Byte, padByte: Byte) {
        val len = buf.size
        writeBytes(buf)
        if (len < size) {
            writeS1(term)
            val padLen = size - len - 1
            for (i in 0 until padLen) writeS1(padByte)
        } else {
            require(len <= size) { "Writing $size bytes, but $len bytes were given" }
        }
    }

    fun writeStream(other: KaitaiStream) {
        writeBytes(other.toByteArray())
    }

    //endregion

    //endregion

    //region Misc runtime operations

    /**
     * Reserves next `n` bytes from current stream as a KaitaiStream-compatible substream.
     * Substream has its own pointer and addressing in the range of [0, n) bytes. This
     * stream's pointer is advanced to the position right after this substream.
     *
     * @param n number of bytes to reserve for a substream
     * @return substream covering n bytes from the current position
     */
    abstract fun substream(n: Long): KaitaiStream

    //endregion

    fun toByteArray(): ByteArray {
        return lookupAt(0) { readBytesFull() }
    }

    abstract class WriteBackHandler(protected val pos: Long) {
        fun writeBack(parent: KaitaiStream) {
            parent.seek(pos)
            write(parent)
        }

        protected abstract fun write(parent: KaitaiStream)
    }

    fun addChildStream(child: KaitaiStream) {
        childStreams.add(child)
    }

    fun writeBackChildStreams() {
        writeBackChildStreams(null)
    }

    protected fun writeBackChildStreams(parent: KaitaiStream?) {
        lookup {
            for (child in childStreams) child.writeBackChildStreams(this)
            childStreams.clear()
        }

        if (parent != null) {
            writeBack(parent)
        }
    }

    protected fun writeBack(parent: KaitaiStream) {
        requireNotNull(writeBackHandler) {
            "WriteBackHandler must be set!"
        }.writeBack(parent)
    }

    companion object {
        fun byteArrayCompare(a: ByteArray, b: ByteArray): Int {
            if (a contentEquals b) return 0

            val al = a.size
            val bl = b.size
            val minLen = min(al.toDouble(), bl.toDouble()).toInt()

            for (i in 0 until minLen) {
                val cmp = (a[i].toInt() and 0xff) - (b[i].toInt() and 0xff)
                if (cmp != 0) return cmp
            }

            // Reached the end of at least one of the arrays
            return if (al == bl) {
                0
            } else {
                al - bl
            }
        }

        fun bytesStripRight(bytes: ByteArray, padByte: Byte): ByteArray {
            var newLen = bytes.size

            while (newLen > 0 && bytes[newLen - 1] == padByte) newLen--

            return bytes.copyOf(newLen)
        }

        fun bytesTerminate(bytes: ByteArray, term: Byte, includeTerm: Boolean): ByteArray {
            var newLen = 0
            val maxLen = bytes.size

            while (newLen < maxLen && bytes[newLen] != term) newLen++
            if (includeTerm && newLen < maxLen) newLen++

            return bytes.copyOf(newLen)
        }

        /**
         * Performs a XOR processing with given data, XORing every byte of input with a single
         * given value.
         * @param data data to process
         * @param key value to XOR with
         * @return processed data
         */
        fun processXor(data: ByteArray, key: Byte): ByteArray {
            return ByteArray(data.size) { idx ->
                data[idx] xor key
            }
        }

        /**
         * Performs a XOR processing with given data, XORing every byte of input with a key
         * array, repeating key array many times, if necessary (i.e. if data array is longer
         * than key array).
         *
         * @param data data to process
         * @param key array of bytes to XOR with
         * @return processed data
         */
        fun processXor(data: ByteArray, key: ByteArray): ByteArray {
            return ByteArray(data.size) { idx ->
                data[idx] xor key[idx % key.size]
            }
        }

        /**
         * Performs a circular left rotation shift for a given buffer by a given amount of bits,
         * using groups of groupSize bytes each time. Right circular rotation should be performed
         * using this procedure with corrected amount.
         *
         * @param data source data to process
         * @param amount number of bits to shift by
         * @param groupSize number of bytes per group to shift
         *
         * @return copy of source array with requested shift applied
         */
        fun processRotateLeft(data: ByteArray, amount: Int, groupSize: Int): ByteArray {
            require(groupSize == 1) {
                "Unable to rotate group of $groupSize bytes yet"
            }

            return ByteArray(data.size) { idx ->
                data[idx].rotateLeft(amount)
            }
        }

        private const val ZLIB_BUF_SIZE = 4096L

        /**
         * Performs an unpacking ("inflation") of zlib-compressed data with usual zlib headers.
         *
         * @param data data to unpack
         *
         * @return unpacked data
         *
         * @throws RuntimeException if data can't be decoded
         */
        fun processZlib(data: ByteArray): ByteArray {
            return Buffer().apply {
                write(data)
            }.use { sourceBuffer ->
                sourceBuffer.inflate().use { source ->
                    Buffer().use { target ->
                        do {
                            val readBytes = source.read(target, ZLIB_BUF_SIZE)
                        } while (readBytes == ZLIB_BUF_SIZE)
                        target.readByteArray()
                    }
                }
            }
        }

        fun unprocessZlib(data: ByteArray): ByteArray {
            return Buffer().use { targetBuffer ->
                targetBuffer.deflate().use { target ->
                    Buffer().apply {
                        write(data)
                    }.use { source ->
                        do {
                            target.write(source, ZLIB_BUF_SIZE)
                        } while (!source.exhausted())
                    }
                }
                targetBuffer.readByteArray()
            }
        }
    }

    //region Error

    /**
     * Error that occurs when default endianness should be decided with a
     * switch, but nothing matches (although using endianness expression
     * implies that there should be some positive result).
     */
    class UndecidedEndiannessError : RuntimeException()

    /**
     * Common ancestor for all error originating from Kaitai Struct usage.
     * Stores KSY source path, pointing to an element supposedly guilty of
     * an error.
     */
    open class KaitaiStructError(
        msg: String,
        protected val srcPath: String,
    ) : RuntimeException("$srcPath: $msg")

    /**
     * Common ancestor for all validation failures. Stores pointer to
     * KaitaiStream IO object which was involved in an error.
     */
    open class ValidationFailedError(
        msg: String,
        protected val io: KaitaiStream?,
        srcPath: String,
    ) : KaitaiStructError(
        (if (io != null) "at pos ${io.pos}: " else "") + "validation failed: $msg", srcPath
    )

    /**
     * Signals validation failure: we required "actual" value to be equal to
     * "expected", but it turned out that it's not.
     */
    open class ValidationNotEqualError(
        protected val expected: Any,
        protected val actual: Any,
        io: KaitaiStream?,
        srcPath: String,
    ) : ValidationFailedError(
        "not equal, expected ${expected.asString}, but got ${actual.asString}", io, srcPath
    )

    open class ValidationLessThanError : ValidationFailedError {
        protected val min: Any?
        protected val actual: Any?

        constructor(
            expected: ByteArray,
            actual: ByteArray,
            io: KaitaiStream?,
            srcPath: String,
        ) : super("not in range, min ${expected.asString}, but got ${actual.asString}", io, srcPath) {
            this.min = null
            this.actual = null
        }

        constructor(
            min: Any,
            actual: Any,
            io: KaitaiStream?,
            srcPath: String,
        ) : super("not in range, min $min, but got $actual", io, srcPath) {
            this.min = min
            this.actual = actual
        }
    }

    open class ValidationGreaterThanError : ValidationFailedError {
        protected val max: Any?
        protected val actual: Any?

        constructor(
            expected: ByteArray,
            actual: ByteArray,
            io: KaitaiStream?,
            srcPath: String,
        ) : super(
            "not in range, max ${expected.asString}, but got ${actual.asString}", io, srcPath
        ) {
            this.max = null
            this.actual = null
        }

        constructor(
            max: Any,
            actual: Any,
            io: KaitaiStream?,
            srcPath: String,
        ) : super("not in range, max $max, but got $actual", io, srcPath) {
            this.max = max
            this.actual = actual
        }
    }

    class ValidationNotAnyOfError(
        protected val actual: Any,
        io: KaitaiStream?,
        srcPath: String,
    ) : ValidationFailedError(
        "not any of the list, got $actual", io, srcPath
    )

    open class ValidationExprError(
        protected val actual: Any,
        io: KaitaiStream?,
        srcPath: String,
    ) : ValidationFailedError(
        "not matching the expression, got $actual", io, srcPath
    )

    //endregion
}

private val Any.asString: String
    @OptIn(ExperimentalStdlibApi::class)
    get() = if (this is ByteArray) toHexString(HexFormat.UpperCase) else toString()
