package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.isAbstract
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

/**
 * This rule inspects `abstract` classes. In case an `abstract class` does not have any concrete members it should be
 * refactored into an interface. Abstract classes which do not define any `abstract` members should instead be
 * refactored into concrete classes.
 *
 * <noncompliant>
 * abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members
 *
 *     abstract val i: Int
 *     abstract fun f()
 * }
 *
 * abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members
 *
 *     val i: Int = 0
 *     fun f() { }
 * }
 * </noncompliant>
 *
 * @configuration excludeAnnotatedClasses - Allows you to provide a list of annotations that disable
 * this check. (default: `"dagger.Module"`)
 */
class UnnecessaryAbstractClass(config: Config = Config.empty) : Rule(config) {

    private val noConcreteMember = "An abstract class without a concrete member can be refactored to an interface."
    private val noAbstractMember = "An abstract class without an abstract member can be refactored to a concrete class."

    override val issue =
            Issue("UnnecessaryAbstractClass", Severity.Style,
                    "An abstract class is unnecessary and can be refactored. " +
                            "An abstract class should have both abstract and concrete properties or functions. " +
                            noConcreteMember + " " + noAbstractMember,
                    Debt.FIVE_MINS)

    private val excludeAnnotatedClasses = SplitPattern(valueOrDefault(EXCLUDE_ANNOTATED_CLASSES, "dagger.Module"))
    private lateinit var annotationExcluder: AnnotationExcluder

    override fun visitKtFile(file: KtFile) {
        annotationExcluder = AnnotationExcluder(file, excludeAnnotatedClasses)
        super.visitKtFile(file)
    }

    override fun visitClass(klass: KtClass) {
        if (!klass.isInterface() && klass.isAbstract() && klass.superTypeListEntries.isEmpty()) {
            val body = klass.body
            if (body != null) {
                val namedMembers = body.children.filter { it is KtProperty || it is KtNamedFunction }
                val namedClassMembers = NamedClassMembers(klass, namedMembers)
                namedClassMembers.detectAbstractAndConcreteType()
            } else if (!hasNoConstructorParameter(klass)) {
                report(CodeSmell(issue, Entity.from(klass), noAbstractMember), klass)
            }
        }
        super.visitClass(klass)
    }

    private fun report(finding: Finding, klass: KtClass) {
        if (!annotationExcluder.shouldExclude(klass.annotationEntries)) {
            report(finding)
        }
    }

    private fun hasNoConstructorParameter(klass: KtClass): Boolean {
        val primaryConstructor = klass.primaryConstructor
        return primaryConstructor == null || !primaryConstructor.valueParameters.any()
    }

    private inner class NamedClassMembers(val klass: KtClass, val namedMembers: List<PsiElement>) {

        fun detectAbstractAndConcreteType() {
            val indexOfFirstAbstractMember = indexOfFirstMember(true)
            if (indexOfFirstAbstractMember == -1) {
                report(CodeSmell(issue, Entity.from(klass), noAbstractMember), klass)
            } else if (isAbstractClassWithoutConcreteMembers(indexOfFirstAbstractMember)) {
                report(CodeSmell(issue, Entity.from(klass), noConcreteMember), klass)
            }
        }

        private fun indexOfFirstMember(isAbstract: Boolean, members: List<PsiElement> = this.namedMembers): Int {
            return members.indexOfFirst {
                val namedDeclaration = it as? KtNamedDeclaration
                namedDeclaration != null && namedDeclaration.isAbstract() == isAbstract
            }
        }

        private fun isAbstractClassWithoutConcreteMembers(indexOfFirstAbstractMember: Int) =
                indexOfFirstAbstractMember == 0 && hasNoConcreteMemberLeft() && hasNoConstructorParameter(klass)

        private fun hasNoConcreteMemberLeft() = indexOfFirstMember(false, namedMembers.drop(1)) == -1
    }

    companion object {
        const val EXCLUDE_ANNOTATED_CLASSES = "excludeAnnotatedClasses"
    }
}
