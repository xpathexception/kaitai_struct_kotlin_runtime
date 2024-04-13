package io.kaitai.struct.annotations

/**
 * Annotation, that applied to Kaitai-generated classes. Visualizers can use that
 * annotation to find classes, that contains generated stuff, that should be showed
 * in visualization.
 *
 * @since 0.9
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Generated(
    /**
     * Original identifier (`id` key) from `ksy` file.
     *
     * @return Identifier, that can differ from class name, if it clash with
     * Java reserved words. Can not be empty
     */
    val id: String,
    /**
     * Version of compiler, that generated this class.
     *
     * @return Version string in [semver](https://semver.org/) format
     */
    val version: String,
    /**
     * Class compiled with support of position tracking. That means, that every class
     * has following public fields (in that version of generator):
     * <table>
     * <caption>Position tracking info.</caption>
     * <tr><th>Type</th><th>Field</th><th>Description</th></tr>
     * <tr><td>`Map<String, Integer>`</td><td>`_attrStart`</td>
     * <td>Start offset in the root stream, where [an attribute][SeqItem] or
     * [an instance][Instance] with specified name begins.
     * Used only for attributes/instances, that is not repeated</td>
    </tr> *
     * <tr><td>`Map<String, Integer>`</td><td>`_attrEnd`</td>
     * <td>Start offset in the root stream, where [an attribute][SeqItem] or
     * [an instance][Instance] with specified name ends (exclusive).
     * Used only for attributes/instances, that is not repeated</td>
    </tr> *
     * <tr><td>`Map<String, ? extends List<Integer>>`</td><td>`_arrStart`</td>
     * <td>List with start offset in the root stream, where each array element of
     * repeated [attribute][SeqItem] or [instance][Instance] with
     * specified name begins. Used only for attributes/instances, that is repeated</td>
    </tr> *
     * <tr><td>`Map<String, ? extends List<Integer>>`</td><td>`_arrEnd`</td>
     * <td>List with end offset (exclusive) in the root stream, where each array
     * element of repeated [attribute][SeqItem] or [instance][Instance]
     * with specified name ends. Used only for attributes/instances, that is repeated</td>
    </tr> *
    </table> *
     *
     * @return `true`, if position tracking is enabled and `false` otherwise
     */
    val posInfo: Boolean,
    /**
     * Determines, if instantiation of user classes (related to user-types, defined
     * in `ksy` file) automatically read its content from the stream, or that must
     * be performed manually by calling generated `_read()`, `_readBE()`
     * or `_readLE()` method.
     *
     * @return `true`, if generated `_read()` method invoked automatically
     * by class constructors and `false`, if it must be called explicitly
     */
    val autoRead: Boolean,
    /**
     * Documentation string attached to the type definition, specified in `doc`
     * KSY element.
     *
     * @return Documentation string for a type. If documentation is missed, returns empty string
     */
    val doc: String,
)
