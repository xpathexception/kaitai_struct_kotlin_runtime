package io.kaitai.struct

/**
 * Common base class for all structured generated by Kaitai Struct.
 * Stores stream object that this object was parsed from in [._io].
 *
 * @param _io stream object that this KaitaiStruct-based structure was parsed from.
 */
abstract class KaitaiStruct(io: KaitaiStream) {
    open var _io: KaitaiStream = io
        protected set
    open val _parent: KaitaiStruct? = null

    /**
     * KaitaiStruct object that supports reading from a supplied stream object.
     */
    abstract class ReadOnly(io: KaitaiStream) : KaitaiStruct(io) {
        abstract fun _read()
    }

    /**
     * KaitaiStruct object that supports both reading from a given stream
     * object, and writing to a pre-supplied stream object or to a
     * stream object given explicitly. This also defines a few useful
     * shortcut methods.
     */
    abstract class ReadWrite(io: KaitaiStream) : ReadOnly(io) {
        abstract fun _write_Seq()
        abstract fun _check()
        abstract fun _fetchInstances() // FIXME: perhaps move directly into KaitaiStruct

        fun _write() {
            _write_Seq()
            _fetchInstances()
            _io.writeBackChildStreams()
        }

        fun _write(io: KaitaiStream) {
            _io = io
            _write()
        }

        fun _write_Seq(io: KaitaiStream) {
            _io = io
            _write_Seq()
        }
    }
}
