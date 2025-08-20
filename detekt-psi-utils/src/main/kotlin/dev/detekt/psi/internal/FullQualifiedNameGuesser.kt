package dev.detekt.psi.internal

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.utils.addIfNotNull
import kotlin.LazyThreadSafetyMode.NONE

internal class FullQualifiedNameGuesser internal constructor(
    private val packageName: String?,
    imports: List<KtImportDirective>,
) {

    @Suppress("ClassOrdering")
    constructor(root: KtFile) : this(
        packageName = root.packageDirective?.qualifiedName?.ifBlank { null },
        imports = root.importList?.imports.orEmpty(),
    )

    private val resolvedNames: Map<String, String> by lazy(NONE) {
        imports
            .asSequence()
            .filterNot { it.isAllUnder }
            .mapNotNull { import ->
                import.importedFqName?.toString()?.let { fqImport ->
                    (import.alias?.name ?: fqImport.substringAfterLast('.')) to fqImport
                }
            }
            .toMap()
    }

    private val starImports: List<String> by lazy(NONE) {
        imports
            .asSequence()
            .filter { it.isAllUnder }
            .mapNotNull { import ->
                import.importedFqName?.toString()
            }
            .toList()
    }

    fun getFullQualifiedName(name: String): Set<String> {
        val resolvedName = findName(name)
        return if (resolvedName != null) {
            setOf(resolvedName)
        } else {
            mutableSetOf<String>()
                .apply {
                    addIfNotNull(defaultImportClasses[name])
                    if (packageName != null) {
                        add("$packageName.$name")
                    }
                    if (name.first().isLowerCase()) {
                        add(name)
                    }
                    starImports.forEach {
                        add("$it.$name")
                    }
                }
        }
    }

    private fun findName(name: String): String? {
        val searchName = name.substringBefore('.')
        val resolvedName = resolvedNames[searchName] ?: return null
        return if (name == searchName) {
            resolvedName
        } else {
            "$resolvedName.${name.substringAfter('.')}"
        }
    }
}
