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
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isAbstract
import io.gitlab.arturbosch.detekt.rules.isInternal
import io.gitlab.arturbosch.detekt.rules.isProtected
import org.jetbrains.kotlin.builtins.StandardNames.FqNames.list
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.resolve.BindingContext

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
 */
@ActiveByDefault(since = "1.2.0")
class UnnecessaryAbstractClass(config: Config = Config.empty) : Rule(config) {

    private val noConcreteMember = "An abstract class without a concrete member can be refactored to an interface."
    private val noAbstractMember = "An abstract class without an abstract member can be refactored to a concrete class."

    override val issue =
        Issue(
            "UnnecessaryAbstractClass",
            Severity.Style,
            "An abstract class is unnecessary and can be refactored. " +
                "An abstract class should have both abstract and concrete properties or functions. " +
                noConcreteMember + " " + noAbstractMember,
            Debt.FIVE_MINS
        )

    @Configuration("Allows you to provide a list of annotations that disable this check.")
    @Deprecated("Use `ignoreAnnotated` instead")
    private val excludeAnnotatedClasses: List<Regex> by config(emptyList<String>()) { list ->
        list.map { it.replace(".", "\\.").replace("*", ".*").toRegex() }
    }

    private lateinit var annotationExcluder: AnnotationExcluder

    override fun visitKtFile(file: KtFile) {
        annotationExcluder = AnnotationExcluder(
            file,
            @Suppress("DEPRECATION") excludeAnnotatedClasses,
            bindingContext,
        )
        super.visitKtFile(file)
    }

    override fun visitClass(klass: KtClass) {
        if (!klass.isInterface() && klass.isAbstract()) {
            val namedMembers = klass.body?.children.orEmpty().filterIsInstance<KtNamedDeclaration>()
            if (namedMembers.isNotEmpty()) {
                NamedClassMembers(klass, namedMembers).detectAbstractAndConcreteType()
            } else if (!klass.hasConstructorParameter()) {
                report(CodeSmell(issue, Entity.from(klass), noConcreteMember), klass)
            } else {
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

    private fun KtClass.hasConstructorParameter() = primaryConstructor?.valueParameters?.isNotEmpty() == true

    private inner class NamedClassMembers(val klass: KtClass, val members: List<KtNamedDeclaration>) {

        fun detectAbstractAndConcreteType() {
            val (abstractMembers, concreteMembers) = members.partition { it.isAbstract() }

            if (abstractMembers.isEmpty() && !hasInheritedMember(true)) {
                report(CodeSmell(issue, Entity.from(klass), noAbstractMember), klass)
                return
            }

            if (abstractMembers.any { it.isInternal() || it.isProtected() } || klass.hasConstructorParameter()) return

            if (concreteMembers.isEmpty() && !hasInheritedMember(false)) {
                report(CodeSmell(issue, Entity.from(klass), noConcreteMember), klass)
            }
        }

        private fun hasInheritedMember(isAbstract: Boolean): Boolean {
            return when {
                klass.superTypeListEntries.isEmpty() -> false
                bindingContext == BindingContext.EMPTY -> true
                else -> {
                    val descriptor = bindingContext[BindingContext.CLASS, klass]
                    descriptor?.unsubstitutedMemberScope?.getContributedDescriptors().orEmpty().any {
                        (it as? MemberDescriptor)?.modality == Modality.ABSTRACT == isAbstract
                    }
                }
            }
        }
    }
}
