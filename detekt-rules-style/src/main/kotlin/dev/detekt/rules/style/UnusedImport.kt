package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isPartOf
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassLikeSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.isDotReceiver

/**
 * This rule reports unused imports. Unused imports are dead code and should be removed.
 * Exempt from this rule are imports resulting from references to elements within KDoc and
 * from destructuring declarations (componentN imports).
 */
class UnusedImport(config: Config) :
    Rule(config, "Unused Imports are dead code and should be removed."),
    RequiresAnalysisApi {

    @Configuration("Additional operators from libraries or tools, such as 'assign'(e.g. compiler plugins for Gradle).")
    private val additionalOperatorSet: List<String> by config(emptyList())

    override fun visit(root: KtFile) {
        with(UnusedImportVisitor(additionalOperatorSet)) {
            root.accept(this)
            unusedImports().forEach {
                report(Finding(Entity.from(it), "The import '${it.importedFqName}' is unused."))
            }
        }
        super.visit(root)
    }

    private class UnusedImportVisitor(private val additionalOperatorSet: List<String>) : DetektVisitor() {
        private var currentPackage: FqName? = null
        private var imports: List<KtImportDirective>? = null
        private val namedReferences = mutableSetOf<KtReferenceExpression>()
        private val staticReferences = mutableSetOf<KtReferenceExpression>()
        private val namedReferencesInKDoc = mutableSetOf<String>()

        private val namedReferencesAsString: Set<String> by lazy {
            namedReferences.mapTo(mutableSetOf()) { it.text.trim('`') }
        }
        private val staticReferencesAsString: Set<String> by lazy {
            staticReferences.mapTo(mutableSetOf()) { it.text.trim('`') }
        }
        private val fqNames: Set<FqName> by lazy {
            namedReferences.mapNotNullTo(mutableSetOf()) { it.fqNameOrNull() }
        }

        /**
         * All [namedReferences] whose [KtReferenceExpression.fqNameOrNull] cannot be resolved
         * mapped to their text. String matches to such references shouldn't be marked as unused
         * imports since they could match the unknown value being imported.
         */
        @Suppress("CommentOverPrivateProperty")
        private val unresolvedNamedReferencesAsString: Set<String> by lazy {
            namedReferences.mapNotNullTo(mutableSetOf()) {
                if (it.fqNameOrNull() == null) it.text.trim('`') else null
            }
        }

        fun unusedImports(): List<KtImportDirective> {
            fun KtImportDirective.isFromSamePackage() = importedFqName?.parent() == currentPackage && alias == null

            fun KtImportDirective.isNotUsed(): Boolean {
                if (aliasName in (namedReferencesInKDoc + namedReferencesAsString)) return false
                val identifier = identifier()
                if (identifier in namedReferencesInKDoc || identifier in staticReferencesAsString) return false
                val fqNameUsed = importPath?.fqName?.let { it in fqNames } == true
                val unresolvedNameUsed = identifier in unresolvedNamedReferencesAsString

                return !fqNameUsed && !unresolvedNameUsed
            }

            return imports?.filter { it.isFromSamePackage() || it.isNotUsed() }.orEmpty()
        }

        override fun visitPackageDirective(directive: KtPackageDirective) {
            currentPackage = directive.fqName
            super.visitPackageDirective(directive)
        }

        override fun visitImportList(importList: KtImportList) {
            imports =
                importList.imports
                    .asSequence()
                    .filter { it.isValidImport }
                    .filter {
                        val identifier = it.identifier()
                        identifier?.contains("*")?.not() == true &&
                            !operatorSet.contains(identifier) &&
                            !additionalOperatorSet.contains(identifier) &&
                            !componentNRegex.matches(identifier)
                    }
                    .toList()
            super.visitImportList(importList)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            expression
                .takeIf { !it.isPartOf<KtImportDirective>() && !it.isPartOf<KtPackageDirective>() }
                ?.takeIf { it.children.isEmpty() }
                ?.run {
                    if (this.isDotReceiver()) {
                        staticReferences.add(this)
                    } else {
                        namedReferences.add(this)
                    }
                }
            super.visitReferenceExpression(expression)
        }

        override fun visitDeclaration(dcl: KtDeclaration) {
            val kdoc = dcl.docComment?.getAllSections()

            kdoc?.forEach { kdocSection ->
                kdocSection
                    .getChildrenOfType<KDocTag>()
                    .map { it.text }
                    .forEach { handleKDoc(it) }

                handleKDoc(kdocSection.getContent())
            }

            super.visitDeclaration(dcl)
        }

        private fun handleKDoc(content: String) {
            kotlinDocReferencesRegExp
                .findAll(content, 0)
                .map { it.groupValues[1] }
                .forEach {
                    val referenceNames = it.split(".")
                    namedReferencesInKDoc.add(referenceNames[0])
                    namedReferencesInKDoc.add(referenceNames.last())
                }
            kotlinDocBlockTagReferenceRegExp.find(content)?.let {
                val str = it.groupValues[2].split(whiteSpaceRegex)[0]
                namedReferencesInKDoc.add(str.split(".")[0])
            }
        }

        @Suppress("ClassOrdering")
        private val KaSymbol.fqNameForImport: FqName?
            get() = when (this) {
                is KaClassLikeSymbol -> {
                    val classIdRemovingCompanion =
                        if (classId?.shortClassName == SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT) {
                            classId?.outerClassId
                        } else {
                            classId
                        }
                    classIdRemovingCompanion?.asSingleFqName()
                }

                is KaCallableSymbol -> callableId?.asSingleFqName()
                else -> null
            }

        private fun KtReferenceExpression.fqNameOrNull(): FqName? = analyze(this) {
            mainReference.resolveToSymbol()?.fqNameForImport
        }
    }

    companion object {
        // Kotlin language default operators
        private val operatorSet =
            setOf(
                "unaryPlus",
                "unaryMinus",
                "not",
                "inc",
                "dec",
                "plus",
                "minus",
                "times",
                "div",
                "mod",
                "rangeTo",
                "rangeUntil",
                "contains",
                "get",
                "set",
                "invoke",
                "plusAssign",
                "minusAssign",
                "timesAssign",
                "divAssign",
                "modAssign",
                "equals",
                "compareTo",
                "iterator",
                "getValue",
                "setValue",
                "provideDelegate",
            )

        private val kotlinDocReferencesRegExp = Regex("\\[([^]]+)](?!\\[)")
        private val kotlinDocBlockTagReferenceRegExp = Regex("^@(see|throws|exception) (.+)")
        private val whiteSpaceRegex = Regex("\\s+")
        private val componentNRegex = Regex("component\\d+")
    }
}

private fun KtImportDirective.identifier() = this.importPath?.importedName?.identifier
