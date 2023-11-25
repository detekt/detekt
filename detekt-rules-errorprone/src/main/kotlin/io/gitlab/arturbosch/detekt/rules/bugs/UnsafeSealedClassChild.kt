package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.descriptors.isInterface
import org.jetbrains.kotlin.descriptors.isSealed
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * The sealed class children must be data class, object or other sealed class to prevent unexpected errors.
 * The potential error can be provider because the normal class doesn't have default implementation of equals and
 * hashcode.
 *
 * <noncompliant>
 * sealed class Foo {
 *     object Bar : Foo()
 *     open class Baz : Foo()
 *
 *     open class Baz2(val test: Int) : Baz()
 * }
 * </noncompliant>
 *
 * <compliant>
 * sealed class Foo {
 *     object Bar : Foo()
 *     data class Baz(val i: Int) : Foo()
 * }
 * </compliant>
 */
@RequiresTypeResolution
class UnsafeSealedClassChild(ruleSetConfig: Config = Config.empty) : Rule(ruleSetConfig) {

    override val issue: Issue = Issue(
        "UnsafeSealedClassChild",
        "Unsafe sealed class detected. These child doesn't have a equals and hashcode implementation",
        Debt.TWENTY_MINS,
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val isChildOfSealedClass = bindingContext[BindingContext.CLASS, klass]
            ?.defaultType
            ?.supertypes()
            .orEmpty()
            .any { kotlinType -> isSealedClass(kotlinType) }

        if (isChildOfSealedClass) {
            val isNormalSealedChild = klass.isData() || klass.isSealed()
            if (!isNormalSealedChild) {
                reportUnsafeChild(klass)
            }
        }
    }

    private fun reportUnsafeChild(klass: KtClass) {
        report(
            CodeSmell(
                issue,
                Entity.from(klass),
                "The ${klass.name} must be data class or object because it's a subclass of a sealed class"
            )
        )
    }

    private fun isSealedClass(type: KotlinType?): Boolean {
        val sealedClassDescriptor = WhenChecker.getClassDescriptorOfTypeIfSealed(type) ?: return false
        if (sealedClassDescriptor.kind.isInterface) {
            return false
        }
        return sealedClassDescriptor.isSealed()
    }
}
