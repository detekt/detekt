package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class FunctionCountProcessor : AbstractProjectMetricProcessor() {

    override val visitor = FunctionCountVisitor()
    override val key = numberOfFunctionsKey
}

val numberOfFunctionsKey = Key<Int>("number of functions")

class FunctionCountVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        file.putUserData(numberOfFunctionsKey, file.collectDescendantsOfType<KtNamedFunction>().size)
    }
}
