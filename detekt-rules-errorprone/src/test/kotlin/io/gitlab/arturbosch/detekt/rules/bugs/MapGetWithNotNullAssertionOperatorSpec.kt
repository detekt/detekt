package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MapGetWithNotNullAssertionOperatorSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = MapGetWithNotNullAssertionOperator(Config.empty)

    @Test
    fun `reports map get operator function with not null assertion when assigned`() {
        val code = """
            fun f() {
                val map = emptyMap<Any, Any>()
                val value = map["key"]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map_get() with not null assertion`() {
        val code = """
            fun f() {
                val map = emptyMap<Any, Any>()
                val value = map.get("key")!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report map get operator function call without not-null assert`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map["key"]
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report map_getValue() call`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map.getValue("key")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report map_getOrDefault() call`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map.getOrDefault("key", "")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report map_getOrElse() call`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map.getOrElse("key", { "" })
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
