package io.kaitai.struct

sealed class ConsistencyError(
    val id: String,
    val actual: Any,
    val expected: Any,
) : RuntimeException("Check failed: $id, expected: $expected, actual: $actual") {
    class SizeMismatch(id: String, actual: Long, expected: Long) : ConsistencyError(id, actual, expected)
}
