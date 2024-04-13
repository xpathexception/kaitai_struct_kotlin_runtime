package io.kaitai.struct.annotations

/**
 * Annotation, that applied to fields, getters or setters that represents parameter
 * from `params` KSY element.
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
annotation class Parameter(
    /**
     * Original identifier (`id` key) from `ksy` file.
     *
     * @return Identifier, that can differ from parameter name, if it clash with
     * Java reserved words. Can not be empty
     */
    val id: String,
    /**
     * Index of a parameter in sequence of parameters in the type.
     *
     * @return 0-based index of a parameter in `params` KSY element
     */
    val index: Int,
    /**
     * Documentation string attached to the parameter, specified in `doc`
     * KSY element.
     *
     * @return Documentation string for parameter. If documentation is missed,
     * returns empty string
     */
    val doc: String,
)
