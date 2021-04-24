package io.gitlab.arturbosch.detekt.core.v2

import io.gitlab.arturbosch.detekt.api.v2.Detektion
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Path

/**
 * Instance of detekt.
 *
 * Runs analysis based on [io.github.detekt.tooling.api.spec.ProcessingSpec] configuration.
 */
interface Detekt {

    fun run(): Detektion

    fun run(path: Path): Detektion

    fun run(sourceCode: String, filename: String): Detektion

    fun run(files: Collection<KtFile>, bindingContext: BindingContext): Detektion
}
