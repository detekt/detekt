package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MatchingDeclarationNameSpec {

    @Nested
    inner class `compliant test cases` {

        @Test
        fun `should pass for object declaration`() {
            val ktFile = compileContentForTest("object O", filename = "O.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for suppress`() {
            val ktFile = compileContentForTest(
                """@file:Suppress("MatchingDeclarationName") object O""",
                filename = "Objects.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for class declaration`() {
            val ktFile = compileContentForTest("class C", filename = "C.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for interface declaration`() {
            val ktFile = compileContentForTest("interface I", filename = "I.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for enum declaration`() {
            val ktFile = compileContentForTest(
                """
                    enum class E {
                        ONE, TWO, THREE
                    }
                """.trimIndent(),
                filename = "E.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for multiple declaration`() {
            val ktFile = compileContentForTest(
                """
                    class C
                    object O
                    fun a() = 5
                """.trimIndent(),
                filename = "MultiDeclarations.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for class declaration with utility functions`() {
            val ktFile = compileContentForTest(
                """
                    class C
                    fun a() = 5
                    fun C.b() = 5
                """.trimIndent(),
                filename = "C.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for class declaration not as first declaration with utility functions`() {
            val ktFile = compileContentForTest(
                """
                    fun a() = 5
                    fun C.b() = 5
                    class C
                """.trimIndent(),
                filename = "Classes.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for private class declaration`() {
            val ktFile = compileContentForTest(
                """
                    private class C
                    fun a() = 5
                """.trimIndent(),
                filename = "b.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for a class with a typealias`() {
            val code = """
                typealias Foo = FooImpl
                
                class FooImpl {}
            """.trimIndent()
            val ktFile = compileContentForTest(code, filename = "Foo.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `non-compliant test cases` {

        @Test
        fun `should not pass for object declaration`() {
            val ktFile = compileContentForTest("object O", filename = "Objects.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 8)
        }

        @Test
        fun `should not pass for object declaration even with suppress on the object`() {
            val ktFile = compileContentForTest(
                """@Suppress("MatchingDeclarationName") object O""",
                filename = "Objects.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 45)
        }

        @Test
        fun `should not pass for class declaration`() {
            val ktFile = compileContentForTest("class C", filename = "Classes.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 7)
        }

        @Test
        fun `should not pass for class declaration as first declaration with utility functions`() {
            val ktFile = compileContentForTest(
                """
                    class C
                    fun a() = 5
                    fun C.b() = 5
                """.trimIndent(),
                filename = "ClassUtils.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 7)
        }

        @Test
        fun `should not pass for interface declaration`() {
            val ktFile = compileContentForTest("interface I", filename = "Not_I.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 11)
        }

        @Test
        fun `should not pass for enum declaration`() {
            val ktFile = compileContentForTest(
                """
                    enum class NOT_E {
                        ONE, TWO, THREE
                    }
                """.trimIndent(),
                filename = "E.kt"
            )
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 12)
        }

        @Test
        fun `should not pass for a typealias with a different name`() {
            val code = """
                class FooImpl {}
                typealias Bar = FooImpl
            """.trimIndent()
            val ktFile = compileContentForTest(code, filename = "Foo.kt")
            val findings = MatchingDeclarationName().lint(ktFile)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not pass for class declaration not as first declaration with utility functions when mustBeFirst is false`() {
            val ktFile = compileContentForTest(
                """
                    fun a() = 5
                    fun C.b() = 5
                    class C
                """.trimIndent(),
                filename = "Classes.kt"
            )
            val findings = MatchingDeclarationName(
                TestConfig("mustBeFirst" to "false")
            ).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(3, 7)
        }
    }
}
