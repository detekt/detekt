package dev.detekt.psi

import dev.detekt.test.KotlinAnalysisApiEngine
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtProperty
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class TypeUtilsSpec {
    @Test
    fun `TypeUtils#isNullable returns true for expression that can be null`() {
        val code = """
            var a: Int? = null
            val b = a
        """.trimIndent()
        val file = KotlinAnalysisApiEngine.compile(code)
        val expression = file.children.filterIsInstance<KtProperty>().last().initializer!!

        assertThat(expression.isNullable(false)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "val c = a!!",
            "val d = (a ?: error(\"null assertion message\"))",
            "val e = 1?.and(2)",
        ]
    )
    fun `TypeUtils#isNullable returns false for expression that cannot be null`(codeToTest: String) {
        val code = """
            var a: Int? = null
            $codeToTest
        """.trimIndent()
        val file = KotlinAnalysisApiEngine.compile(code)
        val expression = file.children.filterIsInstance<KtProperty>().last().initializer!!

        assertThat(expression.isNullable(false)).isFalse()
    }

    @Nested
    inner class PlatformType {
        val code = """
            class Thing {
                val f = javaClass.simpleName
            }
        """.trimIndent()
        val file = KotlinAnalysisApiEngine.compile(code)
        val expression = file
            .children
            .filterIsInstance<KtClass>()
            .single()
            .body!!
            .properties
            .single()
            .initializer!!

        @Test
        fun `platform type is nullable when shouldConsiderPlatformTypeAsNullable = true`() {
            assertThat(expression.isNullable(shouldConsiderPlatformTypeAsNullable = true)).isTrue()
        }

        @Test
        fun `platform type is not nullable when shouldConsiderPlatformTypeAsNullable = false`() {
            assertThat(expression.isNullable(shouldConsiderPlatformTypeAsNullable = false)).isFalse()
        }
    }
}
