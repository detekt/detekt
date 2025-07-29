package io.github.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtFile

class KtFileCountProcessor : AbstractProjectMetricProcessor() {

    override val id: String = "KtFileCountProcessor"
    override val visitor = KtFileCountVisitor()
    override val key = numberOfFilesKey
}

val numberOfFilesKey = Key<Int>("number of kt files")

class KtFileCountVisitor : DetektVisitor() {
    override fun visitKtFile(file: KtFile) {
        file.putUserData(numberOfFilesKey, 1)
    }
}
