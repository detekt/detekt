package io.gitlab.arturbosch.detekt.rules.style

import com.intellij.psi.PsiElement
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isAbstract
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

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
    RequiresAnalysisApi {

    private val noAbstractMember = "An abstract class without an abstract member can be refactored to a concrete class."

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        klass.check()
    }

    private fun KtClass.check() {
        val nameIdentifier = this.nameIdentifier ?: return
        if (isInterface() || !isAbstract()) return
        val members = members()
        when {
            members.isNotEmpty() -> checkMembers(members, nameIdentifier)
            hasConstructorParameter() ->
                report(Finding(Entity.from(nameIdentifier), noAbstractMember))
        }
    }

    private fun KtClass.checkMembers(
        members: List<KtCallableDeclaration>,
        nameIdentifier: PsiElement,
    ) {
        val (abstractMembers, _) = members.partition { it.isAbstract() }
        if (abstractMembers.isEmpty() && !hasInheritedMember()) {
            report(Finding(Entity.from(nameIdentifier), noAbstractMember))
        }
    }

    private fun KtClass.members() = body?.children?.filterIsInstance<KtCallableDeclaration>().orEmpty() +
        primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }.orEmpty()

    private fun KtClass.hasConstructorParameter() = primaryConstructor?.valueParameters?.isNotEmpty() == true

    private fun KtClass.hasInheritedMember(): Boolean =
        when {
            superTypeListEntries.isEmpty() -> false
            else -> {
                analyze(this) {
                    (symbol as? KaClassSymbol)?.memberScope?.declarations.orEmpty().any {
                        it.modality == KaSymbolModality.ABSTRACT
                    }
                }
            }
        }
}
