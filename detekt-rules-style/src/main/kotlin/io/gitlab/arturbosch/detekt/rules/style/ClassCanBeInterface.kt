package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isAbstract
import io.gitlab.arturbosch.detekt.rules.isInternal
import io.gitlab.arturbosch.detekt.rules.isProtected
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.types.typeUtil.isInterface

/**
 * This rule inspects `abstract` classes. In case an `abstract class` does not define any
 * `abstract` members, it should instead be refactored into concrete classes.
 *
 * <noncompliant>
 * abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members
 *
 *     abstract val i: Int
 *     abstract fun f()
 * }
 * </noncompliant>
 */
@ActiveByDefault(since = "1.23.0")
@RequiresTypeResolution
class ClassCanBeInterface(config: Config = Config.empty) : Rule(config) {

    private val noConcreteMember = "An abstract class without a concrete member can be refactored to an interface."

    override val issue =
        Issue(
            "ClassCanBeInterface",
            Severity.Style,
            "An abstract class is unnecessary. May be refactored to an interface.",
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
        klass.check()
        super.visitClass(klass)
    }

    private fun KtClass.check() {
        val nameIdentifier = this.nameIdentifier ?: return
        if (annotationExcluder.shouldExclude(annotationEntries) || isInterface() || !isAbstract()) return
        val members = members()
        when {
            members.isNotEmpty() -> checkMembers(members, nameIdentifier)
            hasInheritedMember(true) && isAnyParentAbstract() -> return
            !hasConstructorParameter() ->
                report(CodeSmell(issue, Entity.from(nameIdentifier), noConcreteMember))
        }
    }

    private fun KtClass.checkMembers(
        members: List<KtCallableDeclaration>,
        nameIdentifier: PsiElement
    ) {
        val (abstractMembers, concreteMembers) = members.partition { it.isAbstract() }
        when {
            abstractMembers.isEmpty() && !hasInheritedMember(true) ->
                Unit
            abstractMembers.any { it.isInternal() || it.isProtected() } || hasConstructorParameter() ->
                Unit
            concreteMembers.isEmpty() && !hasInheritedMember(false) ->
                report(CodeSmell(issue, Entity.from(nameIdentifier), noConcreteMember))
        }
    }

    private fun KtClass.members() = body?.children?.filterIsInstance<KtCallableDeclaration>().orEmpty() +
        primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }.orEmpty()

    private fun KtClass.hasConstructorParameter() = primaryConstructor?.valueParameters?.isNotEmpty() == true

    private fun KtClass.hasInheritedMember(isAbstract: Boolean): Boolean {
        return when {
            superTypeListEntries.isEmpty() -> false
            bindingContext == BindingContext.EMPTY -> true
            else -> {
                val descriptor = bindingContext[BindingContext.CLASS, this]
                descriptor?.unsubstitutedMemberScope?.getContributedDescriptors().orEmpty().any {
                    (it as? MemberDescriptor)?.modality == Modality.ABSTRACT == isAbstract
                }
            }
        }
    }

    private fun KtClass.isAnyParentAbstract() =
        (bindingContext[BindingContext.CLASS, this]?.unsubstitutedMemberScope as? LazyClassMemberScope)
            ?.supertypes
            ?.all { it.isInterface() } == false
}
