package io.gitlab.arturbosch.detekt.sample.extensions

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import io.gitlab.arturbosch.detekt.sample.extensions.rules.TooManyFunctions
import org.junit.jupiter.api.Test

class TooManyFunctionsSpec {
    private val subject = TooManyFunctions(Config.empty)

    @Test
    fun `it should find one file with too many functions`() {
        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage("The file Test.kt has 16 function declarations. Threshold is specified with 10.")
    }
}

private val code: String = """
    class TooManyFunctions : Rule("TooManyFunctions") {

        override fun visitUserType(type: KtUserType) {
            super.visitUserType(type)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            super.visitReferenceExpression(expression)
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            super.visitCallExpression(expression)
        }

        override fun visitBlockStringTemplateEntry(entry: KtBlockStringTemplateEntry) {
            super.visitBlockStringTemplateEntry(entry)
        }

        override fun visitUnaryExpression(expression: KtUnaryExpression) {
            super.visitUnaryExpression(expression)
        }

        override fun visitDynamicType(type: KtDynamicType) {
            super.visitDynamicType(type)
        }

        override fun visitDynamicType(type: KtDynamicType, data: Void?): Void {
            return super.visitDynamicType(type, data)
        }

        override fun visitSuperTypeCallEntry(call: KtSuperTypeCallEntry) {
            super.visitSuperTypeCallEntry(call)
        }

        override fun visitParenthesizedExpression(expression: KtParenthesizedExpression) {
            super.visitParenthesizedExpression(expression)
        }

        override fun visitFinallySection(finallySection: KtFinallySection) {
            super.visitFinallySection(finallySection)
        }

        override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
            super.visitStringTemplateExpression(expression)
        }

        override fun visitDeclaration(dcl: KtDeclaration) {
            super.visitDeclaration(dcl)
        }

        override fun visitLabeledExpression(expression: KtLabeledExpression) {
            super.visitLabeledExpression(expression)
        }

        override fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry) {
            super.visitEscapeStringTemplateEntry(entry)
        }

        override fun visitScript(script: KtScript) {
            super.visitScript(script)
        }

        override fun visitTypeConstraintList(list: KtTypeConstraintList) {
            super.visitTypeConstraintList(list)
        }

    }
""".trimIndent()
