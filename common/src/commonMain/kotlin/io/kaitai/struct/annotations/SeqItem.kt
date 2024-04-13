package io.kaitai.struct.annotations

/**
 * Annotation, that applied to fields, getters or setters that represents an attribute
 * from `seq` KSY element.
 *
 * @since 0.9
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class SeqItem(
    /**
     * Original identifier (`id` key) from `ksy` file.
     *
     * @return Identifier, that can differ from field name, if it clash with
     * Java reserved words. Empty string, if attribute was unnamed
     */
    val id: String,
    /**
     * Index of an attribute in sequence of attributes in the type.
     *
     * @return 0-based index of an attribute in `seq` KSY element
     */
    val index: Int,
    /**
     * Documentation string attached to the attribute, specified in `doc`
     * KSY element.
     *
     * @return Documentation string for and attribute. If documentation is missed,
     * returns empty string
     */
    val doc: String,
)
