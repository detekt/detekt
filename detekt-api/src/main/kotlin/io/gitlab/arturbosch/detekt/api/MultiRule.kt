package io.gitlab.arturbosch.detekt.api

open class MultiRule : BaseRule() {
	override fun visitCondition() = true
}