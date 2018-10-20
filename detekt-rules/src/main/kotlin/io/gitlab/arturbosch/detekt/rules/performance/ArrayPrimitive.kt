package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Using Array<Primitive> leads to implicit boxing and performance hit. Prefer using Kotlin specialized Array
 * Instances.
 *
 * As stated in the Kotlin [documention](https://kotlinlang.org/docs/reference/basic-types.html#arrays) Kotlin has
 * specialized arrays to represent primitive types without boxing overhead, such as `IntArray`, `ByteArray` and so on.
 *
 * <noncompliant>
 * fun function(array: Array<Int>) { }
 *
 * fun returningFunction(): Array<Double> { }
 * </noncompliant>
 *
 * <compliant>
 * fun function(array: IntArray) { }
 *
 * fun returningFunction(): DoubleArray { }
 * </compliant>
 *
 * @author elaydis
 * @author inytar
 */
class ArrayPrimitive(config: Config = Config.empty) : Rule(config) {

	private val regex = Regex("""^Array<(\w+)>$""")

	override val issue = Issue("ArrayPrimitive",
			Severity.Performance,
			"Using Array<Primitive> leads to implicit boxing and a performance hit",
			Debt.FIVE_MINS)

	override fun visitParameter(parameter: KtParameter) {
		if (parameter.typeReference?.text.isArrayPrimitive()) {
			report(CodeSmell(issue, Entity.from(parameter), issue.description))
		}
		super.visitParameter(parameter)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.hasDeclaredReturnType()) {
			if (function.typeReference?.typeElement?.text.isArrayPrimitive()) {
				report(CodeSmell(issue, Entity.from(function), issue.description))
			}
		}
		super.visitNamedFunction(function)
	}

	private fun String?.isArrayPrimitive(): Boolean {
		this?.run {
			val matchResult = regex.find(this)
			matchResult?.run {
				val type = destructured.component1()
				if (type in primitiveTypes) {
					return true
				}
			}
		}
		return false
	}

	private val primitiveTypes = listOf(
			"Int",
			"Double",
			"Float",
			"Short",
			"Byte",
			"Long",
			"Char"
	)
}
