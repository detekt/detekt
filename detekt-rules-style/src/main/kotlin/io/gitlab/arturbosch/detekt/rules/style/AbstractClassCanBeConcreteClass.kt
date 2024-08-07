package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.psi.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isAbstract
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
 * This rule inspects `abstract` classes. Abstract classes which do not define any `abstract` members should instead be
 * refactored into concrete classes.
 *
 * <noncompliant>
 * abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members
 *
 *     val i: Int = 0
 *     fun f() { }
 * }
 * </noncompliant>
 *
 * <compliant>
 * interface OnlyAbstractMembersInInterface {
 *     val i: Int
 *     fun f()
 * }
 *
 * class OnlyConcreteMembersInClass {
 *     val i: Int = 0
 *     fun f() { }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class AbstractClassCanBeConcreteClass(config: Config) :
    Rule(
        config,
        "An abstract class is unnecessary. May be refactored to a concrete class."
    ),
    RequiresTypeResolution {
    private val noAbstractMember = "An abstract class without an abstract member can be refactored to a concrete class."

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
        super.visitClass(klass)
        klass.check()
    }

    private fun KtClass.check() {
        val nameIdentifier = this.nameIdentifier ?: return
        if (annotationExcluder.shouldExclude(annotationEntries) || isInterface() || !isAbstract()) return
        val members = members()
        when {
            members.isNotEmpty() -> checkMembers(members, nameIdentifier)
            hasInheritedMember(true) && isAnyParentAbstract() -> return
            hasConstructorParameter() ->
                report(CodeSmell(Entity.from(nameIdentifier), noAbstractMember))
        }
    }

    private fun KtClass.checkMembers(
        members: List<KtCallableDeclaration>,
        nameIdentifier: PsiElement
    ) {
        val (abstractMembers, _) = members.partition { it.isAbstract() }
        if (abstractMembers.isEmpty() && !hasInheritedMember(true)) {
            report(CodeSmell(Entity.from(nameIdentifier), noAbstractMember))
        }
    }

    private fun KtClass.members() = body?.children?.filterIsInstance<KtCallableDeclaration>().orEmpty() +
        primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }.orEmpty()

    private fun KtClass.hasConstructorParameter() = primaryConstructor?.valueParameters?.isNotEmpty() == true

    private fun KtClass.hasInheritedMember(isAbstract: Boolean): Boolean =
        when {
            superTypeListEntries.isEmpty() -> false
            bindingContext == BindingContext.EMPTY -> true
            else -> {
                val descriptor = bindingContext[BindingContext.CLASS, this]
                descriptor?.unsubstitutedMemberScope?.getContributedDescriptors().orEmpty().any {
                    (it as? MemberDescriptor)?.modality == Modality.ABSTRACT == isAbstract
                }
            }
        }

    private fun KtClass.isAnyParentAbstract() =
        (bindingContext[BindingContext.CLASS, this]?.unsubstitutedMemberScope as? LazyClassMemberScope)
            ?.supertypes
            ?.all { it.isInterface() } == false
}
