package dev.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * Gather additional metrics about the analyzed kotlin file.
 * Pay attention to the thread policy of each function!
 *
 * A bindingContext != BindingContext.EMPTY is only available if Kotlin compiler settings are used.
 */
@Suppress("EmptyFunctionBlock")
interface FileProcessListener : Extension {

    /**
     * Use this to gather some additional information for the real onProcess function.
     * This calculation should be lightweight as this method is called from the main thread.
     */
    fun onStart(files: List<KtFile>) {}

    /**
     * Called when processing of a file begins.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    fun onProcess(file: KtFile) {}

    /**
     * Called when processing of a file completes.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    fun onProcessComplete(file: KtFile, issues: List<Issue>) {}

    /**
     * Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container.
     * Do not do heavy computations here as this method is called from the main thread.
     *
     * This method is called before any [ReportingExtension].
     */
    fun onFinish(files: List<KtFile>, result: Detektion) {}
}
