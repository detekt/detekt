package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.NUMBER_OF_METHODS_KEY
import io.gitlab.arturbosch.detekt.core.visitors.MethodCountVisitor

class MethodCountProcessor : AbstractProcessor() {

	override val visitor = MethodCountVisitor()
	override val key = NUMBER_OF_METHODS_KEY
}
