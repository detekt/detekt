package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.companionObject
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty

/**
 * @configuration threshold - maximum amount of definitions in an interface (default: 10)
 * @configuration includeStaticDeclarations - whether static declarations should be included (default: false)
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class ComplexInterface(config: Config = Config.empty,
					   threshold: Int = DEFAULT_LARGE_INTERFACE_COUNT) : ThresholdRule(config, threshold) {

	override val issue = Issue(javaClass.simpleName, Severity.Maintainability,
			"An interface contains too many functions and properties. " +
					"Large classes tend to handle many things at once. " +
					"An interface should have one responsibility. " +
					"Split up large interfaces into smaller ones that are easier to understand.")

	private val includeStaticDeclarations = valueOrDefault(INCLUDE_STATIC_DECLARATIONS, false)

	override fun visitClass(klass: KtClass) {
		if (klass.isInterface()) {
			val body = klass.getBody() ?: return
			var size = calculateMembers(body)
			if (includeStaticDeclarations) {
				size += countStaticDeclarations(klass.companionObject())
			}
			if (size > threshold) {
				report(ThresholdedCodeSmell(issue,
						Entity.from(klass),
						Metric("SIZE: ", size, threshold),
						"The interface ${klass.name} is too complex. Consider splitting it up."))
			}
		}
		super.visitClass(klass)
	}

	private fun countStaticDeclarations(companionObject: KtObjectDeclaration?): Int {
		val body = companionObject?.getBody()
		return if (body != null) calculateMembers(body) else 0
	}

	private fun calculateMembers(body: KtClassBody) = body.children.count { it is KtNamedFunction || it is KtProperty }

	companion object {
		const val INCLUDE_STATIC_DECLARATIONS = "includeStaticDeclarations"
	}
}

private const val DEFAULT_LARGE_INTERFACE_COUNT = 10
