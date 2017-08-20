package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class TooManyFunctionsSpec : SubjectSpek<TooManyFunctions>({
	subject { TooManyFunctions() }

	describe("a simple test") {
		it("should find one file with too many functions") {
			Assertions.assertThat(subject.lint(code)).hasSize(1)
		}

		it("should find one file with too many top level functions") {
			Assertions.assertThat(subject.lint(scriptCode)).hasSize(1)
		}
	}

})

val code: String =
		"""
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
		"""

val scriptCode = """
				fun main(args : Array<String>) {}
				fun a() {}
				fun b() {}
				fun c() {}
				fun d() {}
				fun e() {}
				fun f() {}
				fun g() {}
				fun h() {}
				fun i() {}
				fun j() {}"""
