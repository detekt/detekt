package io.gitlab.arturbosch.detekt.api.v2

import org.jetbrains.kotlin.psi.KtFile

interface FileProcessListener {

    val priority: Int
        get() = 0

    fun onProcess(file: KtFile) = Unit

    fun onProcessComplete(file: KtFile, findings: List<Finding>): List<Finding> = findings

    fun onFinish(files: List<KtFile>, result: Detektion): Detektion = result
}
