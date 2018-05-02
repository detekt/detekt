package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * This rule will report any public function which does not have the required documentation.
 * If the codebase should have documentation on all public functions enable this rule to enforce this.
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class UndocumentedPublicFunction(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Maintainability,
			"Public functions require documentation.", Debt.TWENTY_MINS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.funKeyword == null && function.isLocal) return

		if (function.docComment == null && function.shouldBeDocumented()) {
				report(CodeSmell(issue, methodHeaderLocation(function),
						"The function ${function.nameAsSafeName} is missing documentation."))
		}
	}

	/**
	 * A function should be documented when it is not overridden,
	 * and both the function and a class containing it are public.
	 */
	private fun KtNamedFunction.shouldBeDocumented(): Boolean =
			isContainingClassPublic() && (modifierList == null || isPublicNotOverridden())

	private fun KtNamedFunction.isContainingClassPublic(): Boolean =
			containingClass().let { it == null || it.isPublic }

	private fun methodHeaderLocation(function: KtNamedFunction) = Entity.from(function)

}
