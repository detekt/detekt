package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

private const val ANNOTATIONS = "annotations"

@KotlinCoreEnvironmentTest
class ForbiddenAnnotationSpec(val env: KotlinCoreEnvironment) {

    @Test
    fun `should report SuppressWarnings usages by default`() {
        val code = """
        @SuppressWarnings("unused")
        fun main() {}
        """.trimIndent()
        val findings = ForbiddenAnnotation(TestConfig()).compileAndLintWithContext(env, code)

        assertThat(findings)
            .hasSize(1)
            .hasStartSourceLocations(
                SourceLocation(1, 1)
            )
            .hasTextLocations(0 to 17)
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
        val findings = ForbiddenAnnotation(TestConfig()).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(6)
        assertThat(findings).hasTextLocations(
            301 to 312,
            313 to 324,
            325 to 335,
            361 to 368,
            387 to 398,
            425 to 435
        )
    }

    @Test
    fun `should report nothing when annotations do not match`() {
        val code = """
        @SuppressWarnings("unused")
        fun main() {}
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(mapOf(ANNOTATIONS to listOf("kotlin.jvm.Transient")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report annotation call when the fully qualified name is used`() {
        val code = """
        @java.lang.SuppressWarnings("unused")
        fun main() {}
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(mapOf(ANNOTATIONS to listOf("java.lang.SuppressWarnings")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(0 to 27)
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
                mapOf(
                    ANNOTATIONS to listOf(
                        "java.lang.SuppressWarnings",
                        "kotlin.jvm.Transient",
                        "kotlin.jvm.Volatile"
                    )
                )
            )
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(3)
        assertThat(findings).hasTextLocations(0 to 17, 54 to 64, 69 to 78)
    }

    @Test
    fun `should report annotation on class`() {
        val code = """
        @SuppressWarnings("unused")
        class SomeClass
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(mapOf(ANNOTATIONS to listOf("java.lang.SuppressWarnings")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(1, 1)
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
            TestConfig(mapOf(ANNOTATIONS to listOf("java.lang.SuppressWarnings")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(2, 5)
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
            TestConfig(mapOf(ANNOTATIONS to listOf("java.lang.SuppressWarnings")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(2, 5)
    }

    @Test
    fun `should report annotation on function parameter`() {
        val code = """
        fun main(@SuppressWarnings("unused") args: Array<String>){}
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(mapOf(ANNOTATIONS to listOf("java.lang.SuppressWarnings")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(1, 10)
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
            TestConfig(mapOf(ANNOTATIONS to listOf("java.lang.SuppressWarnings")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(2, 5)
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
            TestConfig(mapOf(ANNOTATIONS to listOf("java.lang.SuppressWarnings")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(2, 5)
    }

    @Test
    fun `should report nested annotations`() {
        val code = """
        @Deprecated("unused", ReplaceWith("bar"))
        fun foo() = "1234"
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(mapOf(ANNOTATIONS to listOf("kotlin.ReplaceWith")))
        ).compileAndLintWithContext(env, code)
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
        val findings = ForbiddenAnnotation(TestConfig()).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasStartSourceLocation(2, 1)
            .hasTextLocations(37 to 41)
    }

    @Test
    fun `should report annotations for expressions`() {
        val code = """
        val num = @Suppress("MagicNumber") 42
        val x = 0 + @Suppress("UnnecessaryParentheses") (((1)+(2))) + 3
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(mapOf(ANNOTATIONS to listOf("kotlin.Suppress")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
            .hasTextLocations(10 to 19, 50 to 59)
    }

    @Test
    fun `should report annotations for blocks`() {
        val code = """
        fun f(list: List<String>) {
            list.map @Suppress("x") { it.length }
        }
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(mapOf(ANNOTATIONS to listOf("kotlin.Suppress")))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
            .hasTextLocations(41 to 50)
    }
}
