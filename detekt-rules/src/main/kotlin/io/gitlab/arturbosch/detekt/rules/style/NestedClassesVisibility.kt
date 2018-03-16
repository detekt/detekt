package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isInternal
import io.gitlab.arturbosch.detekt.rules.isPublic
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry

/**
 * Nested classes are often used to implement functionality local to the class it is nested in. Therefore it should
 * not be public to other parts of the code.
 * Prefer keeping nested classes `private`.
 *
 * <noncompliant>
 * internal class NestedClassesVisibility {
 *
 *     public class NestedPublicClass // should not be public
 * }
 * </noncompliant>
 *
 * <compliant>
 * internal class NestedClassesVisibility {
 *
 *     internal class NestedPublicClass
 * }
 * </compliant>
 *
 * @author Ivan Balaksha
 * @author schalkms
 * @author Marvin Ramin
 */
class NestedClassesVisibility(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("NestedClassesVisibility", Severity.Style,
			"Nested types are often used for implementing private functionality " +
					"and therefore this should not be public.",
					Debt.FIVE_MINS)

	override fun visitClass(klass: KtClass) {
		if (klass.isTopLevel() && klass.isInternal()) {
			checkDeclarations(klass)
		}
	}

	private fun checkDeclarations(klass: KtClass) {
		klass.declarations
				.filterIsInstance<KtClassOrObject>()
				.filter { it.isPublic() && it.isNoEnum() }
				.forEach {
					report(CodeSmell(issue, Entity.from(it),
						"Nested types are often used for implementing private functionality. " +
								"However the visibility of ${klass.name} makes it visible externally."))
				}
	}

	private fun KtClassOrObject.isNoEnum() = !this.hasModifier(KtTokens.ENUM_KEYWORD) && this !is KtEnumEntry
}
