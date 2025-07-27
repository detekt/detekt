package io.github.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class PropertyCountProcessor : AbstractProjectMetricProcessor() {

    override val id: String = "PropertyCountProcessor"
    override val visitor = PropertyCountVisitor()
    override val key = numberOfFieldsKey
}

val numberOfFieldsKey = Key<Int>("number of properties")

class PropertyCountVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        file.putUserData(numberOfFieldsKey, file.collectDescendantsOfType<KtProperty>().size)
    }
}
