package dev.detekt.api

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
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

    @Test
    fun `return keyword's location`() {
        val code = """
            fun data(): Int {
                return 0
            }
        """.trimIndent()
        val psiElement = compileContentForTest(code).findDescendantOfType<KtReturnExpression>()!!
        val location = Location.from(psiElement.returnKeyword)

        assertThat(location.toString()).isEqualTo(
            "Location(source=2:5, endSource=2:11, text=22:28, " +
                "path=${location.path})"
        )
    }
}
