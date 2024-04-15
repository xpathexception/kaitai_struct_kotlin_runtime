package io.kaitai.struct

interface StructRoot<T> {
    val _root: T
}

interface StructChild<T> : StructRoot<T?> {
    override val _root: T?
}
