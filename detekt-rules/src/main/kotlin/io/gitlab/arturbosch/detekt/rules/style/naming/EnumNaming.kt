package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtEnumEntry

class EnumNaming(config: Config = Config.empty) : SubRule<KtEnumEntry>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val enumEntryPattern = Regex(valueOrDefault(ENUM_PATTERN, "^[A-Z$][a-zA-Z_$]*$"))

	override fun apply(element: KtEnumEntry) {
		if (!element.identifierName().matches(enumEntryPattern)) {
			report(CodeSmell(
					issue.copy(description = "Enum entry names should match the pattern: $enumEntryPattern"),
					Entity.from(element)))
		}
	}

	companion object {
		const val ENUM_PATTERN = "enumEntryPattern"
	}
}
