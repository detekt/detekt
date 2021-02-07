package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

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

    fun onStart(files: List<KtFile>, binding: BindingContext, resources: CompilerResources) = Unit

    fun onProcess(file: KtFile, binding: BindingContext, resources: CompilerResources) = Unit

    fun onProcessComplete(
        file: KtFile,
        findings: Map<String, List<Finding>>,
        binding: BindingContext,
        resources: CompilerResources
    ) = Unit

    fun onFinish(files: List<KtFile>, result: Detektion, binding: BindingContext, resources: CompilerResources) = Unit
}
