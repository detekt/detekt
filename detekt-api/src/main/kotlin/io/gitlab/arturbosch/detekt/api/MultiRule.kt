package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

open class MultiRule : BaseRule() {
	override fun visitCondition(root: KtFile) = true
}
