package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path

fun PsiFile.absolutePath(): Path = Path(virtualFile.path)

// KtFile.virtualFilePath is cached so should be a tiny bit more performant when called repeatedly for the same file.
fun KtFile.absolutePath(): Path = Path(virtualFilePath)
