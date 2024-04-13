package io.kaitai.struct.annotations

/**
 * Annotation, that applied to fields, getters or setters that represents instance
 * field from `instances` KSY element.
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
annotation class Instance(
    /**
     * Original identifier (`id` key) from `ksy` file.
     *
     * @return Identifier, that can differ from instance name, if it clash with
     * Java reserved words. Can not be empty
     */
    val id: String,
    /**
     * Documentation string attached to the instance definition, specified in `doc`
     * KSY element.
     *
     * @return Documentation string for an instance. If documentation is missed,
     * returns empty string
     */
    val doc: String,
)
