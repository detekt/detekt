package dev.detekt.cli.runners

import dev.detekt.test.utils.compileForTest
import dev.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ElementPrinterSpec {

    @Test
    fun `should print the ast as string`() {
        val case = resourceAsPath("cases/Poko.kt")
        val ktFile = compileForTest(case)

        val dump = ElementPrinter.dump(ktFile)

        assertThat(dump.trimIndent()).isEqualTo(expected)
    }
}

private val expected = """
    0: KtFile
      1: KtPackageDirective
        1: KtNameReferenceExpression
        1: KtImportList
        3: KtClass
          3: KtClassBody
            5: KtNamedFunction
            6: KtParameterList
              6: KtParameter
                6: KtTypeReference
                6: KtUserType
                6: KtNameReferenceExpression
              6: KtTypeReference
              6: KtUserType
              6: KtNameReferenceExpression
              6: KtBlockExpression
              7: KtWhenExpression
                7: KtNameReferenceExpression
              8: KtWhenEntry
                8: KtWhenConditionWithExpression
                8: KtConstantExpression
                8: KtReturnExpression
                  8: KtConstantExpression
              9: KtWhenEntry
                9: KtWhenConditionWithExpression
                9: KtConstantExpression
                9: KtReturnExpression
                  9: KtConstantExpression
              10: KtWhenEntry
                10: KtWhenConditionWithExpression
                10: KtConstantExpression
                10: KtReturnExpression
                  10: KtConstantExpression
""".trimIndent()
