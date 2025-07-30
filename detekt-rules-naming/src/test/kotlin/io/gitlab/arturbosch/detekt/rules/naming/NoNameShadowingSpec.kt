package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NoNameShadowingSpec(val env: KotlinEnvironmentContainer) {
    val subject = NoNameShadowing(Config.empty)

    @Test
    fun `report shadowing variable`() {
        val code = """
            fun test(i: Int) {
                val i = 1
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement().hasMessage("Name shadowed: i")
        assertThat(findings).hasStartSourceLocation(2, 9)
    }

    @Test
    fun `report shadowing destructuring declaration entry`() {
        val code = """
            fun test(j: Int) {
                val (j, _) = 1 to 2
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement().hasMessage("Name shadowed: j")
    }

    @Test
    fun `report shadowing lambda parameter`() {
        val code = """
            fun test(k: Int) {
                listOf(1).map { k ->
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement().hasMessage("Name shadowed: k")
    }

    @Test
    fun `report shadowing nested lambda 'it' parameter`() {
        val code = """
            fun test() {
                listOf(1).forEach {
                    listOf(2).forEach { it ->
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement().hasMessage("Name shadowed: it")
    }

    @Test
    fun `does not report when implicit 'it' parameter isn't used`() {
        val code = """
            fun test() {
                listOf(1).forEach {
                    listOf(2).forEach {
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report not shadowing variable`() {
        val code = """
            fun test(i: Int) {
                val j = i
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report not shadowing nested lambda implicit 'it' parameter`() {
        val code = """
            fun test() {
                listOf(1).forEach { i ->
                    listOf(2).forEach {
                        println(it)
                    }
                }
                "".run {
                    listOf(2).forEach {
                        println(it)
                    }
                }
                listOf("").let { list ->
                    list.map { it + "x" }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
