package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtEnumEntry

class EnumNaming(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Enum names should follow the naming convention set in the projects configuration.",
			debt = Debt.FIVE_MINS)
	private val enumEntryPattern = Regex(valueOrDefault(ENUM_PATTERN, "^[A-Z$][a-zA-Z_$]*$"))

	override fun visitEnumEntry(enumEntry: KtEnumEntry) {
		if (!enumEntry.identifierName().matches(enumEntryPattern)) {
			report(CodeSmell(
					issue,
					Entity.from(enumEntry),
					message = "Enum entry names should match the pattern: $enumEntryPattern"))
		}
	}

	companion object {
		const val ENUM_PATTERN = "enumEntryPattern"
	}
}
