package dev.detekt.test.utils

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class KotlinAnalysisApiEngineTest {

    @Test
    fun `can compile a valid script`() {
        val code = """
            package foo.a
            
            class A
        """.trimIndent()
        KotlinAnalysisApiEngine.compile(code)
    }

    @Test
    fun `fails compiling an invalid script`() {
        val invalidCode = """
            package foo.b
            
            val unknownType: UnknownType
        """.trimIndent()
        assertThatThrownBy { KotlinAnalysisApiEngine.compile(invalidCode) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `fails on missing import if import used in previous script - #5739`() {
        val validCode = """
            import kotlin.random.Random

            fun useRandom() {
                Random.nextBoolean()
            }
        """.trimIndent()

        KotlinAnalysisApiEngine.compile(validCode)

        val codeWithMissingImport = """
            fun useRandom() {
                Random.nextBoolean()
            }
        """.trimIndent()

        assertThatThrownBy { KotlinAnalysisApiEngine.compile(codeWithMissingImport) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("ERROR Unresolved reference 'Random'. (dummy.kt:2:5)")
    }

    @RepeatedTest(10)
    fun `can compile the same script repeatedly`() {
        val code = """
            package foo.c
            
            class A
        """.trimIndent()
        KotlinAnalysisApiEngine.compile(code)
    }

    @RepeatedTest(10)
    fun `fails repeatedly on invalid script`() {
        val invalidCode = """
            package foo.d
            
            val unknownType: UnknownType
        """.trimIndent()
        assertThatThrownBy { KotlinAnalysisApiEngine.compile(invalidCode) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `can compile with an external JAR dependency`() {
        val code = """
            package foo.e
            
            import org.junit.jupiter.api.Assertions.assertEquals
            import org.junit.jupiter.api.Test

            class MyTest {
                @Test
                fun testSomething() {
                    assertEquals(1 + 2, 3)
                }
            }
        """.trimIndent()

        val junitApiJar = Test::class.java.protectionDomain.codeSource.location.path
        KotlinAnalysisApiEngine.compile(
            code = code,
            jvmClasspathRoots = listOf(Path(junitApiJar))
        )
    }

    @Test
    fun `fail compilation if the external JAR dependency isn't specified`() {
        val code = """
            package foo.e
            
            import org.junit.jupiter.api.Assertions.assertEquals
            import org.junit.jupiter.api.Test

            class MyTest {
                @Test
                fun testSomething() {
                    assertEquals(1 + 2, 3)
                }
            }
        """.trimIndent()

        assertThatThrownBy { KotlinAnalysisApiEngine.compile(code) }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
