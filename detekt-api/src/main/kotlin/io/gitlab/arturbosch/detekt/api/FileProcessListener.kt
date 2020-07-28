package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

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
    @Deprecated(
        "Use alternative with a binding context.",
        ReplaceWith("onStart(files, bindingContext)")
    )
    fun onStart(files: List<KtFile>) {
    }

    /**
     * Use this to gather some additional information for the real onProcess function.
     * This calculation should be lightweight as this method is called from the main thread.
     */
    @Suppress("DEPRECATION")
    fun onStart(files: List<KtFile>, bindingContext: BindingContext) {
        onStart(files)
    }

    /**
     * Called when processing of a file begins.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    @Deprecated(
        "Use alternative with a binding context.",
        ReplaceWith("onProcess(file, bindingContext)")
    )
    fun onProcess(file: KtFile) {
    }

    /**
     * Called when processing of a file begins.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    @Suppress("DEPRECATION")
    fun onProcess(file: KtFile, bindingContext: BindingContext) {
        onProcess(file)
    }

    /**
     * Called when processing of a file completes.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    @Deprecated(
        "Use alternative with a binding context.",
        ReplaceWith("onProcessComplete(file, findings, bindingContext)")
    )
    fun onProcessComplete(file: KtFile, findings: Map<String, List<Finding>>) {
    }

    /**
     * Called when processing of a file completes.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    @Suppress("DEPRECATION")
    fun onProcessComplete(file: KtFile, findings: Map<String, List<Finding>>, bindingContext: BindingContext) {
        onProcessComplete(file, findings)
    }

    /**
     * Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container.
     * Do not do heavy computations here as this method is called from the main thread.
     *
     * This method is called before any [ReportingExtension].
     */
    @Deprecated(
        "Use alternative with a binding context.",
        ReplaceWith("onFinish(files, result, bindingContext)")
    )
    fun onFinish(files: List<KtFile>, result: Detektion) {
    }

    /**
     * Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container.
     * Do not do heavy computations here as this method is called from the main thread.
     *
     * This method is called before any [ReportingExtension].
     */
    @Suppress("DEPRECATION")
    fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        onFinish(files, result)
    }
}
