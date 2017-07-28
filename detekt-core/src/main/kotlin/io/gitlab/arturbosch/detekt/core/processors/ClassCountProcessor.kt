package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.NUMBER_OF_CLASSES_KEY
import io.gitlab.arturbosch.detekt.core.visitors.ClassCountVisitor

class ClassCountProcessor : AbstractProjectMetricProcessor() {

	override val visitor = ClassCountVisitor()
	override val key = NUMBER_OF_CLASSES_KEY
}
