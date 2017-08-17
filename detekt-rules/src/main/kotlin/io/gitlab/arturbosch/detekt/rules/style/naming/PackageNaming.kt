package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.SubRule
import org.jetbrains.kotlin.psi.KtPackageDirective

class PackageNaming(config: Config = Config.empty) : SubRule<KtPackageDirective>(config) {
	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt.FIVE_MINS)
	private val packagePattern = Regex(valueOrDefault(PACKAGE_PATTERN, "^[a-z]+(\\.[a-z][a-z0-9]*)*$"))

	override fun apply(element: KtPackageDirective) {
		val name = element.qualifiedName
		if (name.isNotEmpty() && !name.matches(packagePattern)) {
			report(CodeSmell(
					issue.copy(description = "Package name should match the pattern: $packagePattern"),
					Entity.from(element)))
		}
	}

	companion object {
		const val PACKAGE_PATTERN = "packagePattern"
	}
}
