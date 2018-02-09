package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublic
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
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
		if (!klass.isInterface() && !klass.hasDelegates() && hasPublicConstructor(klass)) {
			val declarations = klass.getBody()?.declarations
			if (hasOnlyUtilityClassMembers(declarations)) {
				report(CodeSmell(issue, Entity.from(klass), "The class ${klass.nameAsSafeName} only contains" +
						"utility functions. Consider defining it as an object."))
			}
		}
		super.visitClass(klass)
	}

	private fun KtClass.hasDelegates() = superTypeListEntries.any { it is KtDelegatedSuperTypeEntry }

	private fun hasOnlyUtilityClassMembers(declarations: List<KtDeclaration>?): Boolean {
		return declarations?.all {
			it is KtSecondaryConstructor || it is KtClassInitializer || isCompanionObject(it)
		} == true
	}

	private fun isCompanionObject(declaration: KtDeclaration): Boolean {
		return (declaration as? KtObjectDeclaration)?.isCompanion() == true
	}

	private fun hasPublicConstructor(klass: KtClass): Boolean {
		val primaryConstructor = klass.primaryConstructor
		val secondaryConstructors = klass.secondaryConstructors
		if (primaryConstructor == null) {
			return hasPublicConstructor(secondaryConstructors)
		}
		return hasPublicConstructor(primaryConstructor)
	}

	private fun hasPublicConstructor(primaryConstructor: KtPrimaryConstructor) =
			primaryConstructor.isPublic() && primaryConstructor.valueParameters.isEmpty()

	private fun hasPublicConstructor(secondaryConstructors: List<KtSecondaryConstructor>) =
			secondaryConstructors.isEmpty() || secondaryConstructors.any { it.isPublic() }
}
