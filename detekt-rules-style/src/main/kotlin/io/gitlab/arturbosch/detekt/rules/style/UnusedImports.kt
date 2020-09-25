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
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getImportableDescriptor

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

    override fun visit(root: KtFile) {
        with(UnusedImportsVisitor(bindingContext)) {
            root.accept(this)
            unusedImports().forEach {
                report(CodeSmell(issue, Entity.from(it), "The import '${it.importedFqName}' is unused."))
            }
        }
        super.visit(root)
    }

    private class UnusedImportsVisitor(private val bindingContext: BindingContext) : DetektVisitor() {
        private var currentPackage: FqName? = null
        private var imports: List<KtImportDirective>? = null
        private val namedReferences = mutableSetOf<KtReferenceExpression>()
        private val namedReferencesInKDoc = mutableSetOf<String>()

        fun unusedImports(): List<KtImportDirective> {
            fun KtImportDirective.isFromSamePackage() =
                    importedFqName?.parent() == currentPackage && alias == null

            @Suppress("ReturnCount")
            fun KtImportDirective.isNotUsed(): Boolean {
                val namedReferencesAsString = namedReferences.map { it.text.trim('`') }
                if (aliasName in (namedReferencesInKDoc + namedReferencesAsString)) return false
                val identifier = identifier()
                if (identifier in namedReferencesInKDoc) return false
                return if (bindingContext == BindingContext.EMPTY) {
                    identifier !in namedReferencesAsString
                } else {
                    val fqNames = namedReferences.mapNotNull {
                        val descriptor = bindingContext[BindingContext.SHORT_REFERENCE_TO_COMPANION_OBJECT, it]
                            ?: bindingContext[BindingContext.REFERENCE_TARGET, it]
                        descriptor?.getImportableDescriptor()?.fqNameOrNull()
                    }
                    importPath?.fqName?.let { it !in fqNames } == true
                }
            }

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
                    ?.run { namedReferences.add(this) }
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
                    .forEach { namedReferencesInKDoc.add(it.split(".")[0]) }
            kotlinDocBlockTagReferenceRegExp.find(content)?.let {
                val str = it.groupValues[2].split(whiteSpaceRegex)[0]
                namedReferencesInKDoc.add(str.split(".")[0])
            }
        }
    }

    companion object {
        private val operatorSet = setOf("unaryPlus", "unaryMinus", "not", "inc", "dec", "plus", "minus", "times", "div",
            "mod", "rangeTo", "contains", "get", "set", "invoke", "plusAssign", "minusAssign", "timesAssign",
            "divAssign", "modAssign", "equals", "compareTo", "iterator", "getValue", "setValue", "provideDelegate")

        private val kotlinDocReferencesRegExp = Regex("\\[([^]]+)](?!\\[)")
        private val kotlinDocBlockTagReferenceRegExp = Regex("^@(see|throws|exception) (.+)")
        private val whiteSpaceRegex = Regex("\\s+")
        private val componentNRegex = Regex("component\\d+")
    }
}

private fun KtImportDirective.identifier() = this.importPath?.importedName?.identifier
