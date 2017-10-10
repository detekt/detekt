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
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtEnumEntry

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
				.filterIsInstance<KtClass>()
				.filter { it.isPublic() && !it.isEnum() && it !is KtEnumEntry }
				.forEach { report(CodeSmell(issue,
						Entity.from(it),
						"Nested types are often used for implementing private functionality. " +
								"However the visibility of ${klass.name} makes it visible externally."))
		}
	}
}
