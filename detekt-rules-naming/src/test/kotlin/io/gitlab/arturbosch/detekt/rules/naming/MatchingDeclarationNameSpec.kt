package io.gitlab.arturbosch.detekt.rules.naming

import com.intellij.psi.PsiFile
import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.internal.FakePsiFile
import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MatchingDeclarationNameSpec {

    @Nested
    inner class `compliant test cases` {

        @Test
        fun `should pass for object declaration`() {
            val ktFile = compileContentForTest("object O", filename = "O.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for suppress`() {
            val ktFile = compileContentForTest(
                """
                    @file:Suppress("MatchingDeclarationName")
                    
                    object O
                """.trimIndent(),
                filename = "Objects.kt"
            )
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for class declaration`() {
            val ktFile = compileContentForTest("class C", filename = "C.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for interface declaration`() {
            val ktFile = compileContentForTest("interface I", filename = "I.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for a class with a typealias`() {
            val code = """
                typealias Foo = FooImpl
                
                class FooImpl {}
            """.trimIndent()
            val ktFile = compileContentForTest(code, filename = "Foo.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for class declaration and name with platform suffix`() {
            val ktFile = compileContentForTest("actual class C", filename = "C.android.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass for class declaration and name with custom platform suffix`() {
            val ktFile = compileContentForTest("actual class C", filename = "C.mySuffix.kt")
            val findings = MatchingDeclarationName(
                TestConfig("multiplatformTargets" to listOf("mySuffix"))
            ).lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `non-compliant test cases` {

        @Test
        fun `should not pass for object declaration`() {
            val ktFile = compileContentForTest("object O", filename = "Objects.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 8)
        }

        @Test
        fun `should not pass for class declaration with name and unknown suffix`() {
            val ktFile = compileContentForTest("class C", filename = "Object.mySuffix.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 7)
        }

        @Test
        fun `should not pass for object declaration even with suppress on the object`() {
            val ktFile = compileContentForTest(
                """@Suppress("MatchingDeclarationName") object O""",
                filename = "Objects.kt"
            )
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 45)
        }

        @Test
        fun `should not pass for class declaration`() {
            val ktFile = compileContentForTest("class C", filename = "Classes.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 7)
        }

        @Test
        fun `should not pass for interface declaration`() {
            val ktFile = compileContentForTest("interface I", filename = "Not_I.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 12)
        }

        @Test
        fun `should not pass for a typealias with a different name`() {
            val code = """
                class FooImpl {}
                typealias Bar = FooImpl
            """.trimIndent()
            val ktFile = compileContentForTest(code, filename = "Foo.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
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

        @Test
        fun `should not pass for class declaration and name with common suffix`() {
            val ktFile = compileContentForTest("class C", filename = "C.common.kt")
            val findings = MatchingDeclarationName(Config.empty).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 7)
        }

        @Test
        fun `should pass for class declaration and name with platform suffix if passed empty platform suffixes`() {
            val ktFile = compileContentForTest("actual class C", filename = "C.jvm.kt")
            val findings = MatchingDeclarationName(
                TestConfig("multiplatformTargets" to emptyList<String>())
            ).lint(ktFile)
            assertThat(findings).hasStartSourceLocation(1, 14)
        }
    }

    @Nested
    inner class FileNameWithoutSuffix {

        @Test
        fun `should remove kt suffix`() {
            val filename = makeFile("C.kt").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C")
        }

        @Test
        fun `should remove kts suffix`() {
            val filename = makeFile("C.kts").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C")
        }

        @Test
        fun `should not remove non kotlin suffixes`() {
            val filename = makeFile("C.java").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C.java")
        }

        @Test
        fun `should not remove common suffix`() {
            val filename = makeFile("C.common").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C.common")
        }

        @Test
        fun `should not remove common_ktx suffix`() {
            val filename = makeFile("C.common.ktx").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C.common.ktx")
        }
    }

    private fun makeFile(filename: String): PsiFile = FakePsiFile(name = filename)
}
