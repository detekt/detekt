package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.NUMBER_OF_PACKAGES_KEY
import io.gitlab.arturbosch.detekt.core.visitors.PackageCountVisitor

class PackageCountProcessor : AbstractProcessor() {

	override val visitor = PackageCountVisitor()
	override val key = NUMBER_OF_PACKAGES_KEY
}
