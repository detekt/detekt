package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_PACKAGES_KEY
import org.jetbrains.kotlin.psi.KtFile

class PackageCountVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		val packageName = file.packageFqNameByTree.toString()
		file.putUserData(NUMBER_OF_PACKAGES_KEY, packageName)
	}
}
