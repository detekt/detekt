package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Using Array<Primitive> leads to implicit boxing and performance hit. Prefer using Kotlin specialized Array
 * Instances.
 *
 * As stated in the Kotlin [documention](https://kotlinlang.org/docs/reference/basic-types.html#arrays) Kotlin has
 * specialized arrays to represent primitive types without boxing overhead, such as `IntArray`, `ByteArray` and so on.
 *
 * <noncompliant>
 * fun function(array: Array<Int>)
 * </noncompliant>
 *
 * <compliant>
 * fun function(array: IntArray)
 * </compliant>
 *
 * @author elaydis
 * @author inytar
 */
class ArrayPrimitive(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ArrayPrimitive",
			Severity.Performance,
			"Using Array<Primitive> leads to implicit boxing and a performance hit",
			Debt.FIVE_MINS)

	override fun visitParameter(parameter: KtParameter) {
		val regex = Regex("""^Array<(\w+)>$""")
		val matchResult = regex.find(parameter.typeReference?.text as CharSequence)
		matchResult?.let {
			val type = it.destructured.component1()
			if (type in primitiveTypes) {
				report(CodeSmell(issue, Entity.from(parameter), issue.description))
			}
		}
		super.visitParameter(parameter)
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
