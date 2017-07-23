package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.COMPLEXITY_KEY
import io.gitlab.arturbosch.detekt.core.visitors.ComplexityVisitor

class ProjectComplexityProcessor : AbstractProcessor() {

	override val visitor = ComplexityVisitor()
	override val key = COMPLEXITY_KEY
}
