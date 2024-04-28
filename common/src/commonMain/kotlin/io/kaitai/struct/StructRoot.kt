package io.kaitai.struct

interface StructRoot<T> {
    val _root: T
    fun _root(): T = _root
}

interface StructChild<T> : StructRoot<T?> {
    override val _root: T?
    override fun _root(): T = requireNotNull(_root)
}
