package io.github.detekt.psi

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import kotlin.LazyThreadSafetyMode.NONE

class FullQualifiedNameGuesser internal constructor(
    private val packageName: String?,
    imports: List<KtImportDirective>,
) {

    constructor(root: KtFile) : this(
        packageName = root.packageDirective?.qualifiedName?.ifBlank { null },
        imports = root.importList?.imports.orEmpty(),
    )

    @Suppress("ClassOrdering")
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

    fun getFullQualifiedName(name: String): Set<String> {
        val resolvedName = findName(name)
        return if (resolvedName != null) {
            setOf(resolvedName)
        } else {
            mutableSetOf<String>()
                .apply {
                    if (name in kotlinPackageClasses) {
                        add("kotlin.$name")
                    }
                    if (packageName != null) {
                        add("$packageName.$name")
                    }
                }
        }
    }

    private fun findName(name: String): String? {
        val searchName = name.substringBefore('.')
        val resolvedName = resolvedNames[searchName]
        return if (name == searchName) {
            resolvedName
        } else {
            "$resolvedName.${name.substringAfter('.')}"
        }
    }
}

private val kotlinPackageClasses = arrayOf(
    "Annotation",
    "Any",
    "Array",
    "ArrayIndexOutOfBoundsException",
    "Boolean",
    "BooleanArray",
    "Byte",
    "ByteArray",
    "Char",
    "CharArray",
    "CharSequence",
    "Comparable",
    "Comparator",
    "DeepRecursiveFunction",
    "DeepRecursiveScope",
    "Deprecated",
    "DeprecationLevel",
    "Double",
    "DoubleArray",
    "Enum",
    "Float",
    "FloatArray",
    "Function",
    "Int",
    "IntArray",
    "KotlinVersion",
    "Lazy",
    "LazyThreadSafetyMode",
    "Long",
    "LongArray",
    "Nothing",
    "Number",
    "OutOfMemoryError",
    "Pair",
    "Result",
    "Short",
    "ShortArray",
    "String",
    "Throwable",
    "Triple",
    "UByte",
    "UByteArray",
    "UInt",
    "UIntArray",
    "ULong",
    "ULongArray",
    "Unit",
    "UShort",
    "UShortArray",
)
