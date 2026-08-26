package dev.detekt.core

import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Local copy of `dev.detekt.psi.absolutePath`.
 *
 * `:detekt-psi-utils` exists to support rule authors, so `:detekt-core` should not depend on it
 * (#7279).
 * what remains is `AnnotationSuppressor` and `FunctionSuppressor`, which is a separate change.
 *
 * `KtFile.virtualFilePath` is cached, so this is a tiny bit more performant than going through
 * `virtualFile.path` when called repeatedly for the same file.
 */
internal fun KtFile.absolutePath(): Path = Path(virtualFilePath)
