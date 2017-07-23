package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.NUMBER_OF_FILES_KEY
import io.gitlab.arturbosch.detekt.core.visitors.KtFileCountVisitor

class KtFileCountProcessor : AbstractProcessor() {

	override val visitor = KtFileCountVisitor()
	override val key = NUMBER_OF_FILES_KEY
}
