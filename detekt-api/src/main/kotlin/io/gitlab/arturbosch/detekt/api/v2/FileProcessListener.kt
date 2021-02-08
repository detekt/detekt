package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtFile

// This will be a sealed interface in 1.5
interface FileProcessListener {
}

interface PlainFileProcessListener : FileProcessListener {

    fun onStart(files: List<KtFile>) = Unit

    fun onProcess(file: KtFile) = Unit

    fun onProcessComplete(file: KtFile, findings: Map<String, List<Finding>>) = Unit

    fun onFinish(files: List<KtFile>, result: Detektion) = Unit
}

interface TypeSolvingFileProcessListener : FileProcessListener {

    fun onStart(files: List<KtFile>, resolvedContext: ResolvedContext) = Unit

    fun onProcess(file: KtFile, resolvedContext: ResolvedContext) = Unit

    fun onProcessComplete(file: KtFile, findings: Map<String, List<Finding>>, resolvedContext: ResolvedContext) = Unit

    fun onFinish(files: List<KtFile>, result: Detektion, resolvedContext: ResolvedContext) = Unit
}
