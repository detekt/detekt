package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.LLOC_KEY
import io.gitlab.arturbosch.detekt.core.visitors.LLOCVisitor

class ProjectLLOCProcessor : AbstractProcessor() {

	override val visitor = LLOCVisitor()
	override val key = LLOC_KEY
}
