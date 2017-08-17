package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

abstract class MultiRule : BaseRule() {

	abstract val rules: List<Rule>

	override fun visitCondition(root: KtFile) = true
}
