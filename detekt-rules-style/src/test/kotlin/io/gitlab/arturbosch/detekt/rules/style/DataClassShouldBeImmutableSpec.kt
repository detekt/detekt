package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DataClassShouldBeImmutableSpec {
    val subject = DataClassShouldBeImmutable(Config.empty)

    @Test
    fun `reports mutable variable in primary constructor`() {
        val code = "data class C(var i: Int)"
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports mutable property in class body`() {
        val code = """
            data class C(val i: Int) {
                var s: String? = null
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports mutable private property in class body`() {
        val code = """
            data class C(val i: Int) {
                var s: String = ""
                    private set
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports lateinit property in class body`() {
        val code = """
            data class C(val i: Int) {
                lateinit var s: String
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report readonly variable in primary constructor`() {
        val code = "data class C(val i: Int)"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report readonly property in class body`() {
        val code = """
            data class C(val i: Int) {
                val s: String? = null
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report lazy property in class body`() {
        val code = """
            data class C(val i: Int) {
                val s: String by lazy { "" }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report mutable variables in non-data classes`() {
        val code = """
            class C(var i: Int) {
                val s: String by lazy { "" }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
