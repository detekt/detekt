package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CastNullableToNonNullableTypeSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = CastNullableToNonNullableType(Config.empty)

    @Test
    fun `reports casting Nullable type to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar as String
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement().hasMessage(
            "Use separate `null` assertion and type cast like " +
                "('(bar ?: error(\"null assertion message\")) as String') instead of 'bar as String'."
        )
        assertThat(findings).hasStartSourceLocation(2, 17)
    }

    @Test
    fun `reports casting Nullable value returned from a function call to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                bar() as Int
            }
            
            fun bar(): Int? {
                return null
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement().hasMessage(
            "Use separate `null` assertion and type cast like " +
                "('(bar() ?: error(\"null assertion message\")) as Int') instead of 'bar() as Int'."
        )
        assertThat(findings).hasStartSourceLocation(2, 11)
    }

    @Test
    fun `does not report casting of platform type to NonNullable type when ignorePlatformTypes by default`() {
        val code = """
            class Foo {
                fun test() {
                    // getSimpleName() is not annotated with nullability information in the JDK, so compiler treats
                    // it as a platform type with unknown nullability.
                    val y = javaClass.simpleName as String
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report casting of platform type to NonNullable type when ignorePlatformTypes is true`() {
        val code = """
            class Foo {
                fun test() {
                    // getSimpleName() is not annotated with nullability information in the JDK, so compiler treats
                    // it as a platform type with unknown nullability.
                    val y = javaClass.simpleName as String
                }
            }
        """.trimIndent()
        val findings = CastNullableToNonNullableType(
            TestConfig(
                IGNORE_PLATFORM_TYPES to true
            )
        ).lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports casting of platform type to NonNullable type when ignorePlatformTypes is false`() {
        val code = """
            class Foo {
                fun test() {
                    // getSimpleName() is not annotated with nullability information in the JDK, so compiler treats
                    // it as a platform type with unknown nullability.
                    val y = javaClass.simpleName as String
                }
            }
        """.trimIndent()
        val findings = CastNullableToNonNullableType(
            TestConfig(
                IGNORE_PLATFORM_TYPES to false
            )
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report unnecessary safe check chain to NonNullable type`() {
        val code = """
            class Foo {
                fun test() {
                    val z = 1?.and(2) as Int
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report casting of Nullable type to NonNullable expression with assertion to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = (bar ?: error("null assertion message")) as String
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report casting of Nullable type to NonNullable expression with !! assertion to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar!! as String
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report casting of NonNullable type to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar as String?
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report safe casting of Nullable type to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar as? String
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report as compile error will happen when null to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = null as String
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when value is casted to nullable type parameter`() {
        val code = """
            fun <T> combine(
                value: T,
                array: List<*>,
            ) {
                array[0] as T
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when value is casted to non-nullable type parameter`() {
        val code = """
            fun <T: Any> combine(
                value: T,
                array: List<*>,
            ) {
                array[0] as T
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement().hasMessage(
            "Use separate `null` assertion and type cast like " +
                "('(array[0] ?: error(\"null assertion message\")) as T') instead of 'array[0] as T'."
        )
    }

    companion object {
        private const val IGNORE_PLATFORM_TYPES = "ignorePlatformTypes"
    }
}
