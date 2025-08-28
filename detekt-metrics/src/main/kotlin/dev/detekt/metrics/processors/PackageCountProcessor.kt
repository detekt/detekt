package dev.detekt.metrics.processors

import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class PackageCountProcessor : FileProcessListener {
    private val packages: AtomicSet<String> = AtomicSet(emptySet())

    override val id: String = "PackageCountProcessor"

    override fun onStart(files: List<KtFile>) {
        packages.store(emptySet())
    }

    override fun onProcess(file: KtFile) {
        packages.add(file.packageFqName.toString())
    }

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        result.add(ProjectMetric("number of packages", packages.count()))
    }
}

@ExperimentalAtomicApi
class AtomicSet<T>(initialValue: Set<T>) {
    val reference = AtomicReference<Set<T>>(initialValue)

    fun store(value: Set<T>) {
        reference.store(value)
    }

    fun add(value: T) {
        var success: Boolean
        do {
            val set = reference.load()
            success = reference.compareAndSet(set, set + value)
        } while (!success)
    }

    fun count() = reference.load().count()
}
