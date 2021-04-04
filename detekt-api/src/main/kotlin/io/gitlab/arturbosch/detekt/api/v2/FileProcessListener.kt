package io.gitlab.arturbosch.detekt.api.v2

import org.jetbrains.kotlin.psi.KtFile

interface FileProcessListener {

    fun onStart(files: List<KtFile>) = Unit

    fun onProcess(file: KtFile) = Unit

    fun onProcessComplete(file: KtFile, findings: List<Finding>) = Unit

    fun onFinish(files: List<KtFile>, findings: List<Finding>) = Unit
}
