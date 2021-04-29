package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

abstract class AbstractProcessor : FileProcessListener {

    protected abstract val visitor: DetektVisitor
    protected abstract val key: Key<Int>

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        file.accept(visitor)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        val count = files
            .mapNotNull { it.getUserData(key) }
            .sum()
        result.addData(key, count)
    }
}
