package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ElementPrinterSpec : Spek({

    describe("element printer") {

        it("should print the ast as string") {
            val case = resourceAsPath("cases/Poko.kt")
            val ktFile = compileForTest(case)

            val dump = ElementPrinter.dump(ktFile)

            assertThat(dump.trimIndent()).isEqualTo(expected)
        }
    }
})

private val expected = """0: KtFile
  1: KtPackageDirective
    1: KtNameReferenceExpression
    1: KtImportList
    3: KtClass
      3: KtClassBody
        5: KtProperty
          5: KtTypeReference
          5: KtUserType
          5: KtNameReferenceExpression
          5: KtStringTemplateExpression
          5: KtLiteralStringTemplateEntry
        6: KtNamedFunction
          6: KtParameterList
          6: KtStringTemplateExpression
          6: KtLiteralStringTemplateEntry
          6: KtSimpleNameStringTemplateEntry
          6: KtNameReferenceExpression
""".trimIndent()
