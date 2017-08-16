package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

abstract class MultiRule : BaseRule() {

	protected abstract val rules: List<Rule>

	override fun visitCondition(root: KtFile) = true
}
