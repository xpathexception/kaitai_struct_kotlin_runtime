package io.kaitai.struct

import okio.*
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

class OkioKaitaiStream : KaitaiStream {
    private val handle: FileHandle

    private val source: Source
    private val sourceBuffer: BufferedSource

    private val sink: Sink
    private val sinkBuffer: BufferedSink

    constructor(fileName: String) : super() {
        handle = FileSystem.SYSTEM.openReadOnly(fileName.toPath(true))

        source = handle.source()
        sourceBuffer = source.buffer()

        sink = handle.sink()
        sinkBuffer = sink.buffer()
    }

    constructor() {
        handle = FakeFileSystem().openReadWrite("in-memory".toPath())

        source = handle.source()
        sourceBuffer = source.buffer()

        sink = handle.sink()
        sinkBuffer = sink.buffer()
    }

    constructor(data: ByteArray) : this() {
        writeBytes(data)
        seek(0)
    }

    fun asRoBuffer(): Source {
        return handle.source().buffer()
    }

    override fun substream(n: Long): KaitaiStream {
        TODO("Not yet implemented")
    }

    override fun close() {
        sourceBuffer.close()
        source.close()

        sinkBuffer.close()
        sink.close()

        handle.close()
    }

    //region Stream positioning

    /**
     * Check if stream pointer is at the end of stream.
     *
     * @return true if we are located at the end of the stream
     */
    override val isEof: Boolean get() = !(!sourceBuffer.exhausted() || (!bitsWriteMode && bitsLeft > 0))

    /**
     * Set stream pointer to designated position (Long).
     * @param newPos new position (offset in bytes from the beginning of the stream)
     */
    override fun seek(newPos: Long) {
        if (newPos > Int.MAX_VALUE) {
            throw IllegalArgumentException("Java ByteBuffer can't be seeked past Integer.MAX_VALUE")
        }

        if (bitsWriteMode) {
            writeAlignToByte()
        } else {
            alignToByte()
        }

        handle.reposition(source, newPos)
    }

    /**
     * Set stream pointer to designated position (Int).
     *
     * @param newPos new position (offset in bytes from the beginning of the stream)
     */
    override fun seek(newPos: Int) {
        seek(newPos.toLong())
    }

    /**
     * Get current position of a stream pointer.
     * @return pointer position, number of bytes from the beginning of the stream
     */
    override val pos: Long
        get() {
            return handle.position(source) + (if ((bitsWriteMode && bitsLeft > 0)) 1 else 0)
        }

    /**
     * Get total size of the stream in bytes.
     * @return size of the stream in bytes
     */
    override val size: Long
        get() {
            return handle.size()
        }

    //endregion

    //region Reading

    //region Integer numbers

    //region Signed

    override fun readS1(): Byte {
        alignToByte()
        return sourceBuffer.readByte()
    }

    //region Big-endian

    override fun readS2be(): Short {
        alignToByte()
        return sourceBuffer.readShort()
    }

    override fun readS4be(): Int {
        alignToByte()
        return sourceBuffer.readInt()
    }

    override fun readS8be(): Long {
        alignToByte()
        return sourceBuffer.readLong()
    }

    //endregion

    //region Little-endian

    override fun readS2le(): Short {
        alignToByte()
        return sourceBuffer.readShortLe()
    }

    override fun readS4le(): Int {
        alignToByte()
        return sourceBuffer.readIntLe()
    }

    override fun readS8le(): Long {
        alignToByte()
        return sourceBuffer.readLongLe()
    }

    //endregion

    //endregion

    //region Unsigned

    override fun readU1(): Byte {
        return readS1()
    }

    //region Big-endian

    override fun readU2be(): Short {
        return readS2be()
    }

    override fun readU4be(): Int {
        return readS4be()
    }

    override fun readU8be(): Long {
        return readS8be()
    }

    //endregion

    //region Little-endian

    override fun readU2le(): Short {
        return readS2le()
    }

    override fun readU4le(): Int {
        return readS4le()
    }

    override fun readU8le(): Long {
        return readS8le()
    }

    //endregion

    //endregion

    //endregion

    //region Floating point numbers

    //region Big-endian

    override fun readF4be(): Float {
        return Float.fromBits(readS4be())
    }

    override fun readF8be(): Double {
        return Double.fromBits(readS8be())
    }

    //endregion

    //region Little-endian

    override fun readF4le(): Float {
        return Float.fromBits(readS4le())
    }

    override fun readF8le(): Double {
        return Double.fromBits(readS8le())
    }

    //endregion

    //endregion

    //region Byte arrays

    /**
     * Internal method to read the specified number of bytes from the stream. Unlike
     * [readBytes], it doesn't align the bit position to the next byte
     * boundary.
     *
     * @param n number of bytes to read
     * @return read bytes as a byte array
     */
    override fun readBytesNotAligned(n: Long): ByteArray {
        // TODO: do we need to peek?
        return sourceBuffer.readByteArray(toByteArrayLength(n).toLong())
    }

    /**
     * Reads all the remaining bytes in a stream as byte array.
     *
     * @return all remaining bytes in a stream as byte array
     */
    override fun readBytesFull(): ByteArray {
        // TODO: do we need to peek?
        alignToByte()
        return sourceBuffer.readByteArray()
    }

    override fun readBytesTerm(
        term: Byte,
        includeTerm: Boolean,
        consumeTerm: Boolean,
        eosError: Boolean,
    ): ByteArray {
        alignToByte()

        val buf = Buffer()
        while (true) {
            if (sourceBuffer.exhausted()) {
                if (eosError) {
                    throw RuntimeException("End of stream reached, but no terminator $term found")
                } else {
                    return buf.readByteArray()
                }
            }

            seek(pos - 1)

            val c = sourceBuffer.readByte()
            if (c == term) {
                if (includeTerm) buf.writeByte(c.toInt())
                if (!consumeTerm) handle.reposition(source, handle.position(source) - 1)
                return buf.readByteArray()
            }
            buf.writeByte(c.toInt())
        }
    }

    //endregion

    //endregion

    //region Writing

    //region Integer numbers

    //region Signed

    /**
     * Writes one signed 1-byte integer.
     */
    override fun writeS1(v: Byte) {
        writeAlignToByte()
        sinkBuffer.writeByte(v.toInt())
    }

    //region Big-endian

    override fun writeS2be(v: Short) {
        writeAlignToByte()
        sinkBuffer.writeShort(v.toInt())
    }

    override fun writeS4be(v: Int) {
        writeAlignToByte()
        sinkBuffer.writeInt(v)
    }

    override fun writeS8be(v: Long) {
        writeAlignToByte()
        sinkBuffer.writeLong(v)
    }

    //endregion

    //region Little-endian

    override fun writeS2le(v: Short) {
        writeAlignToByte()
        sinkBuffer.writeShortLe(v.toInt())
    }

    override fun writeS4le(v: Int) {
        writeAlignToByte()
        sinkBuffer.writeIntLe(v)
    }

    override fun writeS8le(v: Long) {
        writeAlignToByte()
        sinkBuffer.writeLongLe(v)
    }

    //endregion

    //endregion

    //region Unsigned

    override fun writeU1(v: Byte) {
        writeS1(v)
    }

    //region Big-endian

    override fun writeU2be(v: Short) {
        writeS2be(v)
    }

    override fun writeU4be(v: Int) {
        writeS4be(v)
    }

    override fun writeU8be(v: Long) {
        writeS8be(v)
    }

    //endregion

    //region Little-endian

    override fun writeU2le(v: Short) {
        writeS2le(v)
    }

    override fun writeU4le(v: Int) {
        writeS4le(v)
    }

    override fun writeU8le(v: Long) {
        writeS8le(v)
    }

    //endregion

    //endregion

    //endregion

    //region Floating point numbers

    //region Big-endian

    override fun writeF4be(v: Float) {
        // TODO: check raw bits
        writeS4be(v.toBits())
    }

    override fun writeF8be(v: Double) {
        // TODO: check raw bits
        writeS8be(v.toBits())
    }

    //endregion

    //region Little-endian

    override fun writeF4le(v: Float) {
        // TODO: check raw bits
        writeS4le(v.toBits())
    }

    override fun writeF8le(v: Double) {
        // TODO: check raw bits
        writeS8le(v.toBits())
    }

    //endregion

    //endregion

    //region Unaligned bit values

    //endregion

    //region Byte arrays

    /**
     * Internal method to write the given byte array to the stream. Unlike
     * [writeBytes], it doesn't align the bit position to the next byte
     * boundary.
     *
     * @param buf byte array to write
     */
    override fun writeBytesNotAligned(buf: ByteArray) {
        sinkBuffer.write(buf)
        sinkBuffer.flush()
    }

    //endregion

    //endregion
}
