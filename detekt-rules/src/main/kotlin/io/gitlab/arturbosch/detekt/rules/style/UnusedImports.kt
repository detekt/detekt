package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPartOf
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * This rule reports unused imports. Unused imports are dead code and should be removed.
 * Exempt from this rule are imports resulting from references to elements within KDoc and
 * from destructuring declarations (componentN imports).
 */
class UnusedImports(config: Config) : Rule(config) {

    override val issue = Issue(
            javaClass.simpleName,
            Severity.Style,
            "Unused Imports are dead code and should be removed.",
            Debt.FIVE_MINS)

    companion object {
        private val operatorSet = setOf("unaryPlus", "unaryMinus", "not", "inc", "dec", "plus", "minus", "times", "div",
                "mod", "rangeTo", "contains", "get", "set", "invoke", "plusAssign", "minusAssign", "timesAssign",
                "divAssign", "modAssign", "equals", "compareTo", "iterator", "getValue", "setValue", "provideDelegate")

        private val kotlinDocReferencesRegExp = Regex("\\[([^]]+)](?!\\[)")
        private val kotlinDocBlockTagReferenceRegExp = Regex("^@(see|throws|exception) (.+)")
        private val whiteSpaceRegex = Regex("\\s+")
        private val componentNRegex = Regex("component\\d+")
    }

    override fun visit(root: KtFile) {
        with(UnusedImportsVisitor()) {
            root.accept(this)
            unusedImports().forEach {
                report(CodeSmell(issue, Entity.from(it), "The import '${it.importedFqName}' is unused."))
            }
        }
        super.visit(root)
    }

    private class UnusedImportsVisitor : DetektVisitor() {
        private var currentPackage: FqName? = null
        private var imports: List<KtImportDirective>? = null
        private val namedReferences = mutableSetOf<String>()

        fun unusedImports(): List<KtImportDirective> {
            fun KtImportDirective.isFromSamePackage() =
                    importedFqName?.parent() == currentPackage && alias == null

            fun KtImportDirective.isNotUsed() =
                    aliasName !in namedReferences && identifier() !in namedReferences

            return imports?.filter { it.isFromSamePackage() || it.isNotUsed() }.orEmpty()
        }

        override fun visitPackageDirective(directive: KtPackageDirective) {
            currentPackage = directive.fqName
            super.visitPackageDirective(directive)
        }

        @Suppress("UnsafeCallOnNullableType")
        override fun visitImportList(importList: KtImportList) {
            imports = importList.imports.asSequence().filter { it.isValidImport }
                .filter { it.identifier()?.contains("*")?.not() == true }
                .filter { it.identifier() != null }
                .filter { !operatorSet.contains(it.identifier()) }
                .filter { !componentNRegex.matches(it.identifier()!!) }.toList()
            super.visitImportList(importList)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            expression
                    .takeIf { !it.isPartOf<KtImportDirective>() && !it.isPartOf<KtPackageDirective>() }
                    ?.takeIf { it.children.isEmpty() }
                    ?.run { namedReferences.add(text.trim('`')) }
            super.visitReferenceExpression(expression)
        }

        override fun visitDeclaration(dcl: KtDeclaration) {
            val kdoc = dcl.docComment?.getDefaultSection()

            kdoc?.getChildrenOfType<KDocTag>()
                    ?.map { it.text }
                    ?.forEach { handleKDoc(it) }

            kdoc?.getContent()?.let {
                handleKDoc(it)
            }
            super.visitDeclaration(dcl)
        }

        private fun handleKDoc(content: String) {
            kotlinDocReferencesRegExp.findAll(content, 0)
                    .map { it.groupValues[1] }
                    .forEach { namedReferences.add(it.split(".")[0]) }
            kotlinDocBlockTagReferenceRegExp.find(content)?.let {
                val str = it.groupValues[2].split(whiteSpaceRegex)[0]
                namedReferences.add(str.split(".")[0])
            }
        }
    }
}

private fun KtImportDirective.identifier() = this.importPath?.importedName?.identifier
