package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class PropertyCountProcessor : AbstractProjectMetricProcessor() {

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
