package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

const val KOTLIN_SUFFIX = ".kt"
const val KOTLIN_SCRIPT_SUFFIX = ".kts"

val PsiFile.fileName: String
    get() = name.substringAfterLast(File.separatorChar)

fun PsiFile.fileNameWithoutSuffix(): String {
    val fileName = this.fileName
    if (fileName.endsWith(KOTLIN_SCRIPT_SUFFIX)) {
        return fileName.removeSuffix(KOTLIN_SCRIPT_SUFFIX)
    }
    return fileName.removeSuffix(KOTLIN_SUFFIX)
}

fun PsiFile.absolutePath(): Path = Paths.get(name)

fun PsiFile.relativePath(): Path {
    val value = getUserData(RELATIVE_PATH)
    checkNotNull(value) { "KtFile '$name' expected to have an relative path." }
    return Paths.get(value)
}
