package io.github.detekt.test.utils

import com.intellij.openapi.util.Disposer
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class KotlinAnalysisAAEngineTest {

    @Test
    fun `can compile a valid script`() {
        val code = """
            class A
        """.trimIndent()
        KotlinAnalysisApiEngine(code, Disposer.newDisposable()).compile()
    }

    @Test
    fun `fails compiling an invalid script`() {
        val invalidCode = """
            val unknownType: UnknownType
        """.trimIndent()
        assertThatThrownBy { KotlinAnalysisApiEngine(invalidCode, Disposer.newDisposable()).compile() }
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

        KotlinAnalysisApiEngine(validCode, Disposer.newDisposable()).compile()

        val codeWithMissingImport = """
            fun useRandom() {
                Random.nextBoolean()
            }
        """.trimIndent()

        assertThatThrownBy { KotlinAnalysisApiEngine(codeWithMissingImport, Disposer.newDisposable()).compile() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("ERROR Unresolved reference 'Random'. (dummy.kt:2:5)")
    }

    @RepeatedTest(10)
    fun `can compile the same script repeatedly`() {
        val code = """
            class A
        """.trimIndent()
        KotlinAnalysisApiEngine(code, Disposer.newDisposable()).compile()
    }

    @RepeatedTest(10)
    fun `fails repeatedly on invalid script`() {
        val invalidCode = """
            val unknownType: UnknownType
        """.trimIndent()
        assertThatThrownBy { KotlinAnalysisApiEngine(invalidCode, Disposer.newDisposable()).compile() }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
