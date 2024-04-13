package io.kaitai.struct

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
    (if (io != null) "at pos ${io.pos()}: " else "") + "validation failed: $msg", srcPath
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

private val Any.asString: String
    @OptIn(ExperimentalStdlibApi::class)
    get() = if (this is ByteArray) toHexString(HexFormat.UpperCase) else toString()
