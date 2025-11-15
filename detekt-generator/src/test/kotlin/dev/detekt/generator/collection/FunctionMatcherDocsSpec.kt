package dev.detekt.generator.collection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FunctionMatcherDocsSpec {

    @Test
    fun `FUNCTION_MATCHER_DOCS contains required keywords`() {
        // GIVEN/WHEN
        val docs = FunctionMatcherDocs.FUNCTION_MATCHER_DOCS

        // THEN
        assertThat(docs)
            .contains("Methods can be defined")
            .contains("full signature")
            .contains("extension function")
            .contains("<init>")
            .contains("type parameters")
            .contains("vararg")
            .contains("companion object")
    }

    @Test
    fun `FUNCTION_MATCHER_DOCS matches existing documentation exactly`() {
        // GIVEN
        val expectedDocs = """Methods can be defined without full signature (i.e. `java.time.LocalDate.now`) which will report calls of all methods with this name or with full signature (i.e. `java.time.LocalDate(java.time.Clock)`) which would report only call with this concrete signature. If you want to forbid an extension function like `fun String.hello(a: Int)` you should add the receiver parameter as the first parameter like this: `hello(kotlin.String, kotlin.Int)`. To forbid constructor calls you need to define them with `<init>`, for example `java.util.Date.<init>`. To forbid calls involving type parameters, omit them, for example `fun hello(args: Array<Any>)` is referred to as simply `hello(kotlin.Array)`. To forbid calls involving varargs for example `fun hello(vararg args: String)` you need to define it like `hello(vararg String)`. To forbid methods from the companion object reference the Companion class, for example as `TestClass.Companion.hello()` (even if it is marked `@JvmStatic`)."""

        // WHEN
        val actual = FunctionMatcherDocs.FUNCTION_MATCHER_DOCS

        // THEN
        assertThat(actual.replace("\\s+".toRegex(), " ").trim())
            .isEqualTo(expectedDocs.replace("\\s+".toRegex(), " ").trim())
    }
}
