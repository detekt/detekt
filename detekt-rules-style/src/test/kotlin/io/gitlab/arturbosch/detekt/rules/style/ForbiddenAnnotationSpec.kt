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
            .extracting("message")
            .containsExactly(
                "The annotation `java.lang.SuppressWarnings` has been forbidden: it is a java annotation. Use `Suppress` instead.",
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
        assertThat(findings).hasTextLocations(0 to 37)
    }

    @Test
    fun `should report multiple different annotations`() {
        val code = """
        @SuppressWarnings("unused")
        @Transient
        fun main() {}
        """.trimIndent()
        val findings = ForbiddenAnnotation(
            TestConfig(
                mapOf(
                    ANNOTATIONS to listOf(
                        "java.lang.SuppressWarnings",
                        "kotlin.jvm.Transient"
                    )
                )
            )
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
        assertThat(findings).hasTextLocations(0 to 27, 28 to 38)
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
            val someField: String
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
}
