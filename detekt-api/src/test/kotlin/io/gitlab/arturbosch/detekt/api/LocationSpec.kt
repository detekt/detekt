package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.jupiter.api.Test

class LocationSpec {
    @Test
    fun `start and end positions of block`() {
        val code = """
            fun data(): Int {
                return 0
            }
        """.trimIndent()
        val psiElement = compileContentForTest(code).findChildByClass(KtNamedFunction::class.java)!!
        val location = Location.from(psiElement)

        assertThat("${location.source} - ${location.endSource}").isEqualTo("1:1 - 3:2")
    }

    @Test
    fun `start and end positions of fun keyword`() {
        val code = """
            fun data(): Int {
                return 0
            }
        """.trimIndent()
        val psiElement = compileContentForTest(code).findChildByClass(KtNamedFunction::class.java)!!
        val location = Location.from(psiElement.funKeyword!!)

        assertThat("${location.source} - ${location.endSource}").isEqualTo("1:1 - 1:4")
    }
}
