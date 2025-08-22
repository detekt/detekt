package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class FunctionCountProcessor : AbstractProjectMetricProcessor() {

    override val id: String = "FunctionCountProcessor"
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
