package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtDoubleColonExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtIsExpression
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
import org.jetbrains.kotlin.utils.addIfNotNull

/**
 * Reports unused private classes. If private classes are unused they should be removed. Otherwise, this dead code
 * can lead to confusion and potential bugs.
 */
@ActiveByDefault(since = "1.2.0")
@Alias("unused")
class UnusedPrivateClass(config: Config) : Rule(
    config,
    "Private class is unused and should be removed."
) {

    override fun visit(root: KtFile) {
        super.visit(root)

        val classVisitor = UnusedClassVisitor()
        root.accept(classVisitor)

        classVisitor.getUnusedClasses().forEach {
            report(
                Finding(
                    Entity.from(it),
                    "Private class ${it.nameAsSafeName.identifier} is unused."
                )
            )
        }
    }

    @Suppress("TooManyFunctions")
    private class UnusedClassVisitor : DetektVisitor() {

        private val privateClasses = mutableSetOf<KtNamedDeclaration>()
        private val namedClasses = mutableSetOf<String>()
        private val importedFqNames = mutableSetOf<FqName>()

        fun getUnusedClasses(): List<KtNamedDeclaration> = privateClasses.filter { !it.isUsed() }

        private fun KtNamedDeclaration.isUsed(): Boolean {
            if (nameAsSafeName.identifier in namedClasses) return true
            val pathSegments = fqName?.pathSegments().orEmpty()
            return pathSegments.isNotEmpty() &&
                importedFqNames.any { importedFqName ->
                    importedFqName.pathSegments().zip(pathSegments).all { it.first == it.second }
                }
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

        override fun visitImportDirective(importDirective: KtImportDirective) {
            importedFqNames.addIfNotNull(importDirective.importedFqName)
            super.visitImportDirective(importDirective)
        }

        override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
            namedClasses.addIfNotNull(annotationEntry.typeReference?.text)
            super.visitAnnotationEntry(annotationEntry)
        }

        private fun registerAccess(typeReference: KtTypeReference) {
            // Try with the actual type of the reference (e.g. Foo, Foo?)
            typeReference.orInnerType().run { namedClasses.add(text) }

            // Try with the type with generics (e.g. Foo<Any>, Foo<Any>?)
            (typeReference.typeElement?.orInnerType() as? KtUserType)
                ?.referencedName
                ?.run { namedClasses.add(this) }

            // Try with the type being a generic argument of other type (e.g. List<Foo>, List<Foo?>)
            typeReference.typeElement?.run {
                typeArgumentsAsTypes
                    .asSequence()
                    .filterNotNull()
                    .map { it.orInnerType() }
                    .forEach {
                        namedClasses.add(it.text)
                        // Recursively register for nested generic types (e.g. List<List<Foo>>)
                        if (it is KtTypeReference) registerAccess(it)
                    }
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

        override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
            expression.right?.run { registerAccess(this) }
            super.visitBinaryWithTypeRHSExpression(expression)
        }

        override fun visitIsExpression(expression: KtIsExpression) {
            expression.typeReference?.run { registerAccess(this) }
            super.visitIsExpression(expression)
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            expression.calleeExpression?.text?.run { namedClasses.add(this) }
            expression.typeArguments
                .mapNotNull { it.typeReference }
                .forEach { registerAccess(it) }
            super.visitCallExpression(expression)
        }

        override fun visitDoubleColonExpression(expression: KtDoubleColonExpression) {
            checkReceiverForClassUsage(expression.receiverExpression)
            (expression as? KtCallableReferenceExpression)
                ?.callableReference
                ?.takeIf { looksLikeAClassName(it.getReferencedName()) }
                ?.let { namedClasses.add(it.getReferencedName()) }
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

        // Without type resolution it is hard to tell if this is really a class or part of a package.
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
