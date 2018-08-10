package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isPublic
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

/**
 * A class which only contains utility functions and no concrete implementation can be refactored into an `object`.
 *
 * <noncompliant>
 * class UtilityClass {
 *
 *     // public constructor here
 *     constructor() {
 *         // ...
 *     }
 *
 *     companion object {
 *         val i = 0
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class UtilityClass {
 *
 *     private constructor() {
 *         // ...
 *     }
 *
 *     companion object {
 *         val i = 0
 *     }
 * }
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 * @author Artur Bosch
 */
class UtilityClassWithPublicConstructor(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue(javaClass.simpleName,
			Severity.Style,
			"The class declaration is unnecessary because it only contains utility functions. " +
					"An object declaration should be used instead.",
			Debt.FIVE_MINS)

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface() && !klass.superTypeListEntries.any()) {
			val utilityClassConstructor = UtilityClassConstructor(klass)
			val declarations = klass.getBody()?.declarations
			if (hasOnlyUtilityClassMembers(declarations)) {
				if (utilityClassConstructor.hasPublicConstructorWithoutParameters()) {
					report(CodeSmell(issue, Entity.from(klass),
							"The class ${klass.nameAsSafeName} only contains" +
									" utility functions. Consider defining it as an object."))
				} else if (klass.isOpen() && utilityClassConstructor.hasNonPublicConstructorWithoutParameters()) {
					report(CodeSmell(issue, Entity.from(klass),
							"The utility class ${klass.nameAsSafeName} should be final."))
				}
			}
		}
		super.visitClass(klass)
	}

	private fun hasOnlyUtilityClassMembers(declarations: List<KtDeclaration>?): Boolean {
		if (declarations == null || declarations.isEmpty()) {
			return false
		}
		var containsCompanionObject = false
		var isUtilityClassCandidate = true
		declarations.forEach {
			if (isCompanionObject(it)) {
				containsCompanionObject = true
			} else if (it !is KtSecondaryConstructor && it !is KtClassInitializer) {
				isUtilityClassCandidate = false
			}
		}
		return containsCompanionObject && isUtilityClassCandidate
	}

	private fun isCompanionObject(declaration: KtDeclaration) =
			(declaration as? KtObjectDeclaration)?.isCompanion() == true

	internal class UtilityClassConstructor(private val klass: KtClass) {

		internal fun hasPublicConstructorWithoutParameters() = hasConstructorWithoutParameters(true)

		internal fun hasNonPublicConstructorWithoutParameters() = hasConstructorWithoutParameters(false)

		private fun hasConstructorWithoutParameters(publicModifier: Boolean): Boolean {
			val primaryConstructor = klass.primaryConstructor
			if (primaryConstructor != null) {
				return primaryConstructor.isPublic() == publicModifier && primaryConstructor.valueParameters.isEmpty()
			}
			val secondaryConstructors = klass.secondaryConstructors
			return secondaryConstructors.isEmpty() ||
				secondaryConstructors.any { it.isPublic() == publicModifier && it.valueParameters.isEmpty() }
		}
	}
}
