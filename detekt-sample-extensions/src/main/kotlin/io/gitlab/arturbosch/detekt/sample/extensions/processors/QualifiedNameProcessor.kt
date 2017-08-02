package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class QualifiedNameProcessor : FileProcessListener {

	override fun onProcess(file: KtFile) {
		val packageName = file.packageFqName.asString()
		val nameVisitor = ClassNameVisitor()
		file.accept(nameVisitor)
		val fqNames = nameVisitor.names.mapTo(HashSet<String>()) { "$packageName.$it" }
		file.putUserData(FQ_NAMES_KEY, fqNames)
	}

	override fun onFinish(files: List<KtFile>, result: Detektion) {
		val fqNames = files.map { it.getUserData(FQ_NAMES_KEY) }
				.filterNotNull()
				.flatMapTo(HashSet()) { it }
		result.addData(FQ_NAMES_KEY, fqNames)
	}

	class ClassNameVisitor : DetektVisitor() {

		val names = mutableSetOf<String>()

		override fun visitClassOrObject(classOrObject: KtClassOrObject) {
			names.add(classOrObject.nameAsSafeName.asString())
		}
	}
}

val FQ_NAMES_KEY: Key<Set<String>> = Key.create<Set<String>>("FQNames")
