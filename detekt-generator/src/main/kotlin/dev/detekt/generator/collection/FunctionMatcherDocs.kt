package dev.detekt.generator.collection

/**
 * Centralized documentation for FunctionMatcher syntax used across multiple rules.
 */
object FunctionMatcherDocs {
    const val FUNCTION_MATCHER_DOCS =
        "Methods can be defined without full signature (i.e. `java.time.LocalDate.now`) which will report " +
            "calls of all methods with this name or with full signature " +
            "(i.e. `java.time.LocalDate(java.time.Clock)`) which would report only call " +
            "with this concrete signature. If you want to forbid an extension function like " +
            "`fun String.hello(a: Int)` you should add the receiver parameter as the first parameter like this: " +
            "`hello(kotlin.String, kotlin.Int)`. To forbid constructor calls you need to define them with `<init>`, " +
            "for example `java.util.Date.<init>`. To forbid calls involving type parameters, omit them, for example " +
            "`fun hello(args: Array<Any>)` is referred to as simply `hello(kotlin.Array)`. To forbid calls " +
            "involving varargs for example `fun hello(vararg args: String)` you need to define it like " +
            "`hello(vararg String)`. To forbid methods from the companion object reference the Companion class, for " +
            "example as `TestClass.Companion.hello()` (even if it is marked `@JvmStatic`)."
}
