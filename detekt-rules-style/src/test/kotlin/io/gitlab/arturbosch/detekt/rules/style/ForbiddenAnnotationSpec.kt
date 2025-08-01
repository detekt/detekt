package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.SourceLocation
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

private const val ANNOTATIONS = "annotations"

@KotlinCoreEnvironmentTest
class ForbiddenAnnotationSpec(val env: KotlinEnvironmentContainer) {

    @Test
    fun `should report SuppressWarnings usages by default`() {
        val code = """
            @SuppressWarnings("unused")
            fun main() {}
        """.trimIndent()
        val findings = ForbiddenAnnotation(Config.empty).lintWithContext(env, code)

        assertThat(findings)
            .hasSize(1)
            .hasStartSourceLocations(
                SourceLocation(1, 1)
            )
            .hasTextLocations("@SuppressWarnings")
            .extracting("message")
            .containsExactly(
                "The annotation `java.lang.SuppressWarnings` has been forbidden: it is a java annotation. Use `Suppress` instead.",
            )
    }

    @Test
    fun `should report annotations from java lang annotation package by default`() {
        val code = """
            import java.lang.annotation.Retention
            import java.lang.annotation.Documented
            import java.lang.annotation.Target
            import java.lang.annotation.Repeatable
            import java.lang.annotation.Inherited
            import java.lang.annotation.RetentionPolicy
            import java.lang.annotation.ElementType
            import java.lang.Deprecated
            @Deprecated
            @Documented
            @Retention(RetentionPolicy.RUNTIME)
            @Target(ElementType.TYPE)
            @Repeatable(value = SomeClass::class)
            @Inherited
            annotation class SomeClass(val value: Array<SomeClass>)
        """.trimIndent()
        val findings = ForbiddenAnnotation(Config.empty).lintWithContext(env, code)
        assertThat(findings).hasSize(6)
            .hasTextLocations(
                "@Deprecated",
                "@Documented",
                "@Retention",
                "@Target",
                "@Repeatable",
                "@Inherited"
            )
    }

    @Test
    fun `should report nothing when annotations do not match`() {
        val code = """
            @SuppressWarnings("unused")
            fun main() {}
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("kotlin.jvm.Transient"))
        ).lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report annotation call when the fully qualified name is used`() {
        val code = """
            @java.lang.SuppressWarnings("unused")
            fun main() {}
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("java.lang.SuppressWarnings"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@java.lang.SuppressWarnings")
    }

    @Test
    fun `should report multiple different annotations`() {
        val code = """
            @SuppressWarnings("unused")
            data class SomeClass(
                @Transient
                @Volatile
                var transient: String? = null
            )
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(
                ANNOTATIONS to listOf(
                    "java.lang.SuppressWarnings",
                    "kotlin.jvm.Transient",
                    "kotlin.jvm.Volatile",
                )
            )
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(3)
            .hasTextLocations("@SuppressWarnings", "@Transient", "@Volatile")
    }

    @Test
    fun `should report annotation on class`() {
        val code = """
            @SuppressWarnings("unused")
            class SomeClass
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("java.lang.SuppressWarnings"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@SuppressWarnings")
    }

    @Test
    fun `should report annotation on method`() {
        val code = """
            class SomeClass {
                @SuppressWarnings("unused")
                fun someMethod(){}
            }
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("java.lang.SuppressWarnings"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@SuppressWarnings")
    }

    @Test
    fun `should report annotation on field`() {
        val code = """
            class SomeClass {
                @SuppressWarnings("unused")
                val someField: String = "lalala"
            }
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("java.lang.SuppressWarnings"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@SuppressWarnings")
    }

    @Test
    fun `should report annotation on function parameter`() {
        val code = """
            fun main(@SuppressWarnings("unused") args: Array<String>){}
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("java.lang.SuppressWarnings"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@SuppressWarnings")
    }

    @Test
    fun `should report annotation on constructor`() {
        val code = """
            class SomeClass {
                @SuppressWarnings("unused")
                constructor(s: String)
                constructor(t: Int)
            }
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("java.lang.SuppressWarnings"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@SuppressWarnings")
    }

    @Test
    fun `should report annotation on local variable`() {
        val code = """
            fun some(): Int {
                @SuppressWarnings("unused")
                val q = "1234"
                return 10
            }
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("java.lang.SuppressWarnings"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@SuppressWarnings")
    }

    @Test
    fun `should report nested annotations`() {
        val code = """
            import kotlin.Deprecated
            @Deprecated(message = "unused", replaceWith = ReplaceWith("bar"))
            fun foo() = "1234"
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("kotlin.ReplaceWith"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("ReplaceWith")
    }

    @Test
    fun `should report aliased annotations`() {
        val code = """
            typealias Dep = java.lang.Deprecated
            @Dep
            fun f() = Unit
        """.trimIndent()
        val findings = ForbiddenAnnotation(Config.empty).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@Dep")
    }

    @Test
    fun `should report annotations for expressions`() {
        val code = """
            val x = 0 + @Suppress("UnnecessaryParentheses") (((1)+(2))) + 3
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("kotlin.Suppress"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@Suppress")
    }

    @Test
    fun `should report annotations for blocks`() {
        val code = """
            fun f(list: List<String>) {
                list.map @Suppress("x") { it.length }
            }
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(ANNOTATIONS to listOf("kotlin.Suppress"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations("@Suppress")
    }
}
