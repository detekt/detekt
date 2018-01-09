package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class MatchingDeclarationName(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
			"If a source file contains only a single top-level class or object, " +
					"the file name should reflect the case-sensitive name plus the .kt extension.",
			Debt.FIVE_MINS)

	override fun visitKtFile(file: KtFile) {
		val declarations = file.declarations
				.filterIsInstance<KtClassOrObject>()
		if (declarations.size == 1) {
			val declaration = declarations[0] as? KtClassOrObject
			val declarationName = declaration?.name ?: return
			if (declarationName != file.name.removeSuffix(KOTLIN_SUFFIX)) {
				report(CodeSmell(issue, Entity.from(file), "The file name '${file.name}' " +
						"does not match the name of the single top-level declaration '$declarationName'."))
			}
		}
	}
}

private const val KOTLIN_SUFFIX = ".kt"

