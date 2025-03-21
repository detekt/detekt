package io.gitlab.arturbosch.detekt.rules.naming

import com.intellij.psi.PsiFile
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * "If a Kotlin file contains a single non-private class (potentially with related top-level declarations),
 * its name should be the same as the name of the class, with the .kt extension appended.
 * If a file contains multiple classes, or only top-level declarations,
 * choose a name describing what the file contains, and name the file accordingly.
 * Use camel humps with an uppercase first letter (e.g. ProcessDeclarations.kt).
 *
 * The name of the file should describe what the code in the file does.
 * Therefore, you should avoid using meaningless words such as "Util" in file names." - Official Kotlin Style Guide
 *
 * More information at: https://kotlinlang.org/docs/coding-conventions.html
 *
 * <noncompliant>
 *
 * class Foo // FooUtils.kt
 *
 * fun Bar.toFoo(): Foo = ...
 * fun Foo.toBar(): Bar = ...
 *
 * </noncompliant>
 *
 * <compliant>
 *
 * class Foo { // Foo.kt
 *     fun stuff() = 42
 * }
 *
 * fun Bar.toFoo(): Foo = ...
 *
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class MatchingDeclarationName(config: Config) : Rule(
    config,
    "If a source file contains only a single non-private top-level class or object, " +
        "the file name should reflect the case-sensitive name plus the .kt extension."
) {

    @Configuration("name should only be checked if the file starts with a class or object")
    private val mustBeFirst: Boolean by config(true)

    @Configuration("kotlin multiplatform targets, used to allow file names like `MyClass.jvm.kt`")
    private val multiplatformTargets: List<String> by config(COMMON_KOTLIN_KMP_PLATFORM_TARGET_SUFFIXES)

    override fun visitKtFile(file: KtFile) {
        val declarations = file.declarations
            .asSequence()
            .filterIsInstance<KtClassOrObject>()
            .filterNot { it.isPrivate() }
            .toList()

        fun matchesFirstClassOrObjectCondition(): Boolean =
            !mustBeFirst || mustBeFirst && declarations.first() === file.declarations.first()

        fun hasNoMatchingTypeAlias(filename: String): Boolean =
            file.declarations.filterIsInstance<KtTypeAlias>().all { it.name != filename }

        if (declarations.size == 1 && matchesFirstClassOrObjectCondition()) {
            val declaration = declarations.first()
            val declarationName = declaration.name
            val filename = file.fileNameWithoutSuffix(multiplatformTargets)
            if (declarationName != filename && hasNoMatchingTypeAlias(filename)) {
                val entity = Entity.atName(declaration)
                report(
                    Finding(
                        Entity(entity.signature, entity.location, file),
                        "The file name '$filename' " +
                            "does not match the name of the single top-level declaration '$declarationName'."
                    )
                )
            }
        }
    }

    companion object {

        private val COMMON_KOTLIN_KMP_PLATFORM_TARGET_SUFFIXES = listOf(
            "ios",
            "android",
            "js",
            "jvm",
            "native",
            "iosArm64",
            "iosX64",
            "macosX64",
            "mingwX64",
            "linuxX64"
        )
    }
}

/**
 * Removes kotlin specific file name suffixes, e.g. .kt.
 * Note, will not remove other possible/known file suffixes like '.java'
 */
internal fun PsiFile.fileNameWithoutSuffix(multiplatformTargetSuffixes: List<String> = emptyList()): String {
    val fileName = this.name
    val suffixesToRemove = multiplatformTargetSuffixes.map { platform -> ".$platform.kt" } + listOf(".kt", ".kts")
    for (suffix in suffixesToRemove) {
        if (fileName.endsWith(suffix)) {
            return fileName.removeSuffix(suffix)
        }
    }
    return fileName
}
