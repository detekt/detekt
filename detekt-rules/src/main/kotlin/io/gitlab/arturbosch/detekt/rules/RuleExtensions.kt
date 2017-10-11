package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.style.File
import org.jetbrains.kotlin.psi.KtElement

fun <T : File> T.reportFindings(rules: List<SubRule<T>>) {
	rules.filter { it.visitCondition(this.file) }
			.onEach { it.apply(this) }
			.flatMap { it.findings }
}

fun <T : KtElement> T.reportFindings(rule: SubRule<T>) {
	if (rule.visitCondition(this.containingKtFile)) {
		rule.apply(this)
	}
}
