package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtDoubleColonExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports unused private classes.
 * If private classes are unused they should be removed. Otherwise this dead code
 * can lead to confusion and potential bugs.
 */
class UnusedPrivateClass(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("unused")

    override val issue: Issue = Issue("UnusedPrivateClass",
            Severity.Maintainability,
            "Private class is unused.",
            Debt.FIVE_MINS)

    override fun visit(root: KtFile) {
        super.visit(root)

        val classVisitor = UnusedClassVisitor()
        root.accept(classVisitor)

        classVisitor.getUnusedClasses().forEach {
            report(CodeSmell(issue, Entity.from(it), "Private class ${it.nameAsSafeName.identifier} is unused."))
        }
    }

    @Suppress("Detekt.TooManyFunctions")
    private class UnusedClassVisitor : DetektVisitor() {

        private val privateClasses = mutableSetOf<KtNamedDeclaration>()
        private val namedClasses = mutableSetOf<String>()

        fun getUnusedClasses(): List<KtNamedDeclaration> {
            return privateClasses.filter { it.nameAsSafeName.identifier !in namedClasses }
        }

        override fun visitClass(klass: KtClass) {
            if (klass.isPrivate()) {
                privateClasses.add(klass)
            }
            klass.getSuperTypeList()?.entries
                    ?.mapNotNull { it.typeReference }
                    ?.forEach { registerAccess(it) }
            super.visitClass(klass)
        }

        private fun registerAccess(typeReference: KtTypeReference) {
            // Try with the actual type of the reference (e.g. Foo, Foo?)
            typeReference.orInnerType().run { namedClasses.add(text) }

            // Try with the type with generics (e.g. Foo<Any>, Foo<Any>?)
            (typeReference.typeElement?.orInnerType() as? KtUserType)
                    ?.referencedName
                    ?.run { namedClasses.add(this) }

            // Try with the type being a generic argument of other type (e.g. List<Foo>, List<Foo?>)
            typeReference.typeElement?.typeArgumentsAsTypes
                    ?.asSequence()
                    ?.filterNotNull()
                    ?.map { it.orInnerType() }
                    ?.forEach {
                        namedClasses.add(it.text)
                        // Recursively register for nested generic types (e.g. List<List<Foo>>)
                        (it as? KtTypeReference)?.run { registerAccess(it) }
                    }
        }

        override fun visitParameter(parameter: KtParameter) {
            parameter.typeReference?.run { registerAccess(this) }
            super.visitParameter(parameter)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            function.typeReference?.run { registerAccess(this) }
            super.visitNamedFunction(function)
        }

        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            declaration.getSuperTypeList()?.entries?.forEach {
                it.typeReference?.run { registerAccess(this) }
            }
            super.visitObjectDeclaration(declaration)
        }

        override fun visitFunctionType(type: KtFunctionType) {
            type.returnTypeReference?.run { registerAccess(this) }
            super.visitFunctionType(type)
        }

        override fun visitProperty(property: KtProperty) {
            property.typeReference?.run { registerAccess(this) }
            super.visitProperty(property)
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            expression.calleeExpression?.text?.run { namedClasses.add(this) }
            super.visitCallExpression(expression)
        }

        override fun visitDoubleColonExpression(expression: KtDoubleColonExpression) {
            checkReceiverForClassUsage(expression.receiverExpression)
            if (expression.isEmptyLHS) {
                expression.safeAs<KtCallableReferenceExpression>()
                        ?.callableReference
                        ?.takeIf { looksLikeAClassName(it.getReferencedName()) }
                        ?.let { namedClasses.add(it.getReferencedName()) }
            }
            super.visitDoubleColonExpression(expression)
        }

        private fun checkReceiverForClassUsage(receiver: KtExpression?) {
            (receiver as? KtNameReferenceExpression)
                    ?.text
                    ?.takeIf { looksLikeAClassName(it) }
                    ?.let { namedClasses.add(it) }
        }

        override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
            checkReceiverForClassUsage(expression.receiverExpression)
            super.visitDotQualifiedExpression(expression)
        }

        // Without symbol solving it is hard to tell if this is really a class or part of a package.
        // We use "first char is uppercase" as a heuristic in conjunction with "KtNameReferenceExpression"
        private fun looksLikeAClassName(maybeClassName: String) =
                maybeClassName.firstOrNull()?.isUpperCase() == true
    }
}

/**
 * Get the non-nullable type of a reference to a potentially nullable one (e.g. String? -> String)
 */
private fun KtTypeReference.orInnerType() = (typeElement as? KtNullableType)?.innerType ?: this

/**
 * Get the non-nullable type of a type element to a potentially nullable one (e.g. String? -> String)
 */
private fun KtTypeElement.orInnerType() = (this as? KtNullableType)?.innerType ?: this
