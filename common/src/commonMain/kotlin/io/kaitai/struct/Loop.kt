package io.kaitai.struct

inline fun doWhileWithIndex(
    initialIndex: Int = 0,
    condition: (index: Int) -> Boolean,
    block: (index: Int) -> Unit,
) {
    var index = initialIndex
    do {
        block(index)
        index++
    } while (condition(index))
}

inline fun <reified T> doWhileWithIndex(
    initialIndex: Int = 0,
    condition: (index: Int, item: T) -> Boolean,
    block: (index: Int) -> T,
) {
    var index = initialIndex
    do {
        val item = block(index)
        index++
    } while (condition(index, item))
}
