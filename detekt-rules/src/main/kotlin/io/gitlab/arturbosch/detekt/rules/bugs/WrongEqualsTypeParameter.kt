package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.bugs.util.hasCorrectEqualsParameter
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

class WrongEqualsTypeParameter(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("WrongEqualsTypeParameter", Severity.Defect,
			"Wrong parameter type for equals() method found. " +
					"To correctly override the equals() method use Any?")

	override fun visitClass(klass: KtClass) {
		if (klass.isInterface()) {
			return
		}
		super.visitClass(klass)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.name == "equals" && !function.hasCorrectEqualsParameter()) {
			report(CodeSmell(issue, Entity.from(function)))
		}
	}
}
