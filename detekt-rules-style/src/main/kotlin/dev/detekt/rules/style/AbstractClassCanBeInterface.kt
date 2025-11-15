package dev.detekt.rules.style

import com.intellij.psi.PsiElement
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isAbstract
import dev.detekt.psi.isInternal
import dev.detekt.psi.isProtected
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

/**
 * This rule inspects `abstract` classes. In case an `abstract class` does not define any
 * `abstract` members, it should instead be refactored into an interface.
 *
 * <noncompliant>
 * abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members
 *
 *     abstract val i: Int
 *     abstract fun f()
 * }
 * </noncompliant>
 * <compliant>
 * interface Interface {
 *     val i: Int
 *     fun f()
 * }
 *
 * abstract class NonAbstractMembersInAbstractClass {
 *
 *     abstract val i: Int
 *     fun f() {
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.23.0")
class AbstractClassCanBeInterface(config: Config) :
    Rule(
        config,
        "An abstract class is unnecessary. May be refactored to an interface."
    ),
    RequiresAnalysisApi {

    private val noConcreteMember = "An abstract class without a concrete member can be refactored to an interface."

    override fun visitClass(klass: KtClass) {
        check(klass)
        super.visitClass(klass)
    }

    private fun check(klass: KtClass) {
        val nameIdentifier = klass.nameIdentifier ?: return
        if (klass.isInterface() || !klass.isAbstract()) return
        val members = klass.members()
        analyze(klass) {
            when {
                members.isNotEmpty() -> checkMembers(klass, members, nameIdentifier)

                hasInheritedMember(klass, isAbstract = true) && isAnyParentAbstract(klass) -> return

                !klass.hasConstructorParameter() ->
                    report(Finding(Entity.from(nameIdentifier), noConcreteMember))
            }
        }
    }

    private fun KaSession.checkMembers(
        klass: KtClass,
        members: List<KtCallableDeclaration>,
        nameIdentifier: PsiElement,
    ) {
        val (abstractMembers, concreteMembers) = members.partition { it.isAbstract() }
        when {
            abstractMembers.isEmpty() && !hasInheritedMember(klass, isAbstract = true) ->
                Unit

            abstractMembers.any { it.isInternal() || it.isProtected() } || klass.hasConstructorParameter() ->
                Unit

            concreteMembers.isEmpty() && !hasInheritedMember(klass, isAbstract = false) ->
                report(Finding(Entity.from(nameIdentifier), noConcreteMember))
        }
    }

    private fun KtClass.members() =
        body?.children?.filterIsInstance<KtCallableDeclaration>().orEmpty() +
            primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }.orEmpty()

    private fun KtClass.hasConstructorParameter() = primaryConstructor?.valueParameters?.isNotEmpty() == true

    private fun KaSession.hasInheritedMember(klass: KtClass, isAbstract: Boolean): Boolean =
        when {
            klass.superTypeListEntries.isEmpty() -> false

            else -> {
                (klass.symbol as? KaClassSymbol)?.memberScope?.declarations.orEmpty().any {
                    it.modality == KaSymbolModality.ABSTRACT == isAbstract
                }
            }
        }

    private fun KaSession.isAnyParentAbstract(klass: KtClass): Boolean =
        (klass.symbol as? KaClassSymbol)
            ?.superTypes
            ?.all { (it.symbol as? KaClassSymbol)?.classKind == KaClassKind.INTERFACE } == false
}
