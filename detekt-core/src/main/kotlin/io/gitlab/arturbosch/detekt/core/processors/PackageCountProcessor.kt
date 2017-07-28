package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_PACKAGES_KEY
import io.gitlab.arturbosch.detekt.core.visitors.PackageCountVisitor
import org.jetbrains.kotlin.psi.KtFile

class PackageCountProcessor : FileProcessListener {

	private val visitor = PackageCountVisitor()
	private val key = NUMBER_OF_PACKAGES_KEY

	override fun onProcess(file: KtFile) {
		file.accept(visitor)
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		val count = files
				.map { it.getUserData(key) }
				.filterNotNull()
				.distinct()
				.size
		result.add(ProjectMetric(key.toString(), count))
	}
}
