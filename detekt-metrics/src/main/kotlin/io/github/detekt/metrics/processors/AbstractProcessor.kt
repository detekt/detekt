package io.github.detekt.metrics.processors

import com.intellij.openapi.util.Key
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtFile

abstract class AbstractProcessor : FileProcessListener {

    protected abstract val visitor: DetektVisitor
    protected abstract val key: Key<Int>

    override fun onProcess(file: KtFile) {
        file.accept(visitor)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        val count = files
            .mapNotNull { it.getUserData(key) }
            .sum()
        result.putUserData(key, count)
    }
}
