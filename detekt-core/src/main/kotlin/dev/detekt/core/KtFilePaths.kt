package dev.detekt.core

import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path

internal fun KtFile.absolutePath(): Path = Path(virtualFilePath)
