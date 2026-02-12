package dev.detekt.rules.style

import com.intellij.psi.PsiElement
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isAbstract
import dev.detekt.psi.isConstant
import dev.detekt.psi.isInternal
import dev.detekt.psi.isOpen
import dev.detekt.psi.isProtected
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

/**
 * This rule inspects `abstract` classes. In case an `abstract class` does not define any
 * `abstract` members, it should instead be refactored into an interface. It will also check for potential migrations
 * from `sealed class` to `sealed interface`, using the same rules.
 *
 * <noncompliant>
 * abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members
 *     abstract val i: Int
 *     abstract fun f()
 * }
 *
 * sealed class ScreenState { // violation - no concrete members
 *     data class Success(val data: Int) : ScreenState()
 *     data class Failure(val reason: String : ScreenState()
 *     data object Empty : ScreenState()
 * }
 * </noncompliant>
 * <compliant>
 * interface Interface {
 *     val i: Int
 *     fun f()
 * }
 *
 * abstract class NonAbstractMembersInAbstractClass {
 *     abstract val i: Int
 *     fun f() {
 *     }
 * }
 *
 * sealed class SealedClass(val x: Int) {
 *     data class Success(val data: Int) : SealedClass(123)
 *     data class Failure(val reason: String : SealedClass(456)
 *     object Empty : SealedClass(789)
 * }
 *
 * sealed interface SealedInterface {
 *     data class Success(val data: Int) : SealedInterface
 *     data class Failure(val reason: String : SealedInterface
 *     data object Empty : SealedInterface
 * }
 * </compliant>
 */
@Suppress("TooManyFunctions")
@ActiveByDefault(since = "1.23.0")
class AbstractClassCanBeInterface(config: Config) :
    Rule(
        config,
        "An abstract class is unnecessary. May be refactored to an interface."
    ),
    RequiresAnalysisApi {

    override fun visitClass(klass: KtClass) {
        check(klass)
        super.visitClass(klass)
    }

    private fun check(klass: KtClass) {
        val nameIdentifier = klass.nameIdentifier ?: return
        if (!shouldCheck(klass)) return

        val members = klass.members()
        analyze(klass) {
            when {
                members.isNotEmpty() -> checkMembers(klass, members, nameIdentifier)
                hasInheritedMember(klass, isAbstract = true) && isAnyParentAbstract(klass) -> return
                klass.hasConstructorParameter() || klass.containsInternalClass() -> return
                else -> report(Finding(Entity.from(nameIdentifier), klass.message()))
            }
        }
    }

    private fun KtClass.message(): String = if (isSealed()) SEALED_NO_CONCRETE_MEMBER else NO_CONCRETE_MEMBER

    private fun shouldCheck(klass: KtClass) =
        when {
            klass.isInterface() -> false
            klass.isSealed() -> true
            else -> klass.isAbstract()
        }

    private fun KaSession.checkMembers(
        klass: KtClass,
        members: List<KtCallableDeclaration>,
        nameIdentifier: PsiElement,
    ) {
        // Treat open members as abstract-like unless they have a non-const backing field. An open val with a
        // non-const initializer (e.g. open val x = computeSomething()) stores a value evaluated once per instance.
        // In an interface it would become a getter evaluated on every access, changing the behavior and preventing
        // a safe refactoring.
        val (abstractMembers, concreteMembers) = members.partition { member ->
            member.isAbstract() || (member.isOpen() && member.hasConstOrNoBackingField())
        }

        when {
            abstractMembers.isEmpty() && !hasInheritedMember(klass, isAbstract = true) ->
                return

            abstractMembers.any { it.isInternal() || it.isProtected() } ||
                klass.hasConstructorParameter() ||
                klass.containsInternalClass() -> return

            concreteMembers.isEmpty() && !hasInheritedMember(klass, isAbstract = false) ->
                report(Finding(Entity.from(nameIdentifier), klass.message()))
        }
    }

    private fun KtClass.members() =
        body?.children?.filterIsInstance<KtCallableDeclaration>().orEmpty() +
            primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }.orEmpty()

    private fun KtClass.hasConstructorParameter() = primaryConstructor?.valueParameters?.isNotEmpty() == true

    // Kotlin doesn't allow internal classes within an interface, but it does allow them within a sealed class
    private fun KtClass.containsInternalClass() =
        body?.children?.filterIsInstance<KtClass>()?.any { it.isInternal() } == true

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

    /**
     * Returns true if this callable can be safely represented in an interface: functions, getter-only properties
     * (no initializer), and properties with compile-time constant initializers all qualify.
     *
     * Returns false for properties with a non-const backing field. In a class the initializer runs once per
     * instance, but in an interface the property requires a getter evaluated on every access, changing behaviour.
     *
     * Only literal values (e.g. 404, "text") and direct references to const vals are considered constant.
     */
    context(session: KaSession)
    private fun KtCallableDeclaration.hasConstOrNoBackingField(): Boolean =
        when (val initializer = (this as? KtProperty)?.initializer) {
            // No initializer: getter-only property or a function. no backing field, safe for interface
            null -> true

            // Literal constant. value is always the same whether field or getter, safe for interface getters
            is KtConstantExpression -> true

            // Reference to a const val. Effectively a compile-time constant, safe for interface getters
            is KtNameReferenceExpression -> {
                val symbol = with(session) { initializer.mainReference.resolveToSymbol() }
                val psi = symbol?.psi as? KtProperty
                psi?.isConstant() == true
            }

            // Any other expression (function call, arithmetic, etc.) has a non-const backing field => NOT safe
            else -> false
        }

    internal companion object {
        const val NO_CONCRETE_MEMBER = "An abstract class without a concrete member can be refactored to an interface."

        const val SEALED_NO_CONCRETE_MEMBER =
            "A sealed class without a concrete member can be refactored to a sealed interface."
    }
}
