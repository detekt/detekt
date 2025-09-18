package io.gitlab.arturbosch.detekt.sample.extensions.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

class QualifiedNameProcessor : FileProcessListener {

    override val id = "QualifiedNameProcessor"

    override fun onProcess(file: KtFile) {
        val packageName = file.packageFqName.asString()
        val nameVisitor = ClassNameVisitor()
        file.accept(nameVisitor)
        val fqNames = nameVisitor.names
            .mapTo(HashSet()) { "$packageName.$it" }
        file.putUserData(fqNamesKey, fqNames)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
        val fqNames = files
            .mapNotNull { it.getUserData(fqNamesKey) }
            .flatMapTo(HashSet()) { it }

        result.userData[fqNamesKey.toString()] = fqNames
        return result
    }

    class ClassNameVisitor : DetektVisitor() {

        val names = mutableSetOf<String>()

        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            names.add(classOrObject.nameAsSafeName.asString())
        }
    }
}

val fqNamesKey: Key<Set<String>> = Key.create("FQNames")
