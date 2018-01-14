package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
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
 * More information at: http://kotlinlang.org/docs/reference/coding-conventions.html
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
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class MatchingDeclarationName(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
			"If a source file contains only a single non-private top-level class or object, " +
					"the file name should reflect the case-sensitive name plus the .kt extension.",
			Debt.FIVE_MINS)

	override fun visitKtFile(file: KtFile) {
		val declarations = file.declarations
				.filterIsInstance<KtClassOrObject>()
				.filterNot { it.isPrivate() }
		if (declarations.size == 1) {
			val declaration = declarations[0] as? KtClassOrObject
			val declarationName = declaration?.name ?: return
			if (declarationName != file.name.removeSuffix(KOTLIN_SUFFIX)) {
				report(CodeSmell(issue, Entity.from(file), "The file name '${file.name}' " +
						"does not match the name of the single top-level declaration '$declarationName'."))
			}
		}
	}
}

private const val KOTLIN_SUFFIX = ".kt"

