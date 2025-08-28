package dev.detekt.metrics.processors

import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.plusAssign

@OptIn(ExperimentalAtomicApi::class)
class ClassCountProcessor : FileProcessListener {
    private val count: AtomicInt = AtomicInt(0)
    override val id: String = "ClassCountProcessor"

    override fun onStart(files: List<KtFile>) {
        count.store(0)
    }

    override fun onProcess(file: KtFile) {
        count += file.collectDescendantsOfType<KtClass>().size
    }

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        result.add(ProjectMetric("number of classes", count.load()))
    }
}
