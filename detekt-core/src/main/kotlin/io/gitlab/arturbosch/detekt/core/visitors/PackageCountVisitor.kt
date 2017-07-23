package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_PACKAGES_KEY
import org.jetbrains.kotlin.psi.KtFile

class PackageCountVisitor : DetektVisitor() {

	private val packageNames: MutableSet<String> = HashSet<String>()

	override fun visitKtFile(file: KtFile) {
		packageNames.add(file.packageFqNameByTree.toString())
		file.putUserData(NUMBER_OF_PACKAGES_KEY, packageNames.size)
	}
}
