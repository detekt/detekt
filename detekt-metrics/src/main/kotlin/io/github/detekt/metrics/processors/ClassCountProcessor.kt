package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class ClassCountProcessor : AbstractProjectMetricProcessor() {

    override val visitor = ClassCountVisitor()
    override val key = numberOfClassesKey
}

val numberOfClassesKey = Key<Int>("number of classes")

class ClassCountVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        file.putUserData(numberOfClassesKey, file.collectDescendantsOfType<KtClass>().size)
    }
}
