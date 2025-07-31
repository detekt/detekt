package dev.detekt.api.internal

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffsetSkippingComments

private val multipleWhitespaces = Regex("\\s{2,}")

internal fun PsiElement.buildFullSignature(): String {
    var fullSignature = this.searchSignature()
    val parentSignatures = this.parents
        .filter { it is KtClassOrObject }
        .map { it.extractClassName() }
        .toList()
        .reversed()
        .joinToString(".")

    if (parentSignatures.isNotEmpty()) {
        fullSignature = "$parentSignatures\$$fullSignature"
    }

    return fullSignature
}

private fun PsiElement.extractClassName() =
    this.getNonStrictParentOfType<KtClassOrObject>()?.nameAsSafeName?.asString().orEmpty()

private fun PsiElement.searchSignature(): String =
    when (this) {
        is KtNamedFunction -> buildFunctionSignature(this)
        is KtClassOrObject -> buildClassSignature(this)
        is KtFile -> fileSignature()
        else -> this.text
    }.replace('\n', ' ').replace(multipleWhitespaces, " ")

private fun KtFile.fileSignature() = "${this.packageFqName.asString()}.${this.name}"

private fun buildClassSignature(classOrObject: KtClassOrObject): String {
    var baseName = classOrObject.nameAsSafeName.asString()
    val typeParameters = classOrObject.typeParameters
    if (typeParameters.size > 0) {
        baseName += "<"
        baseName += typeParameters.joinToString(", ") { it.text }
        baseName += ">"
    }
    val extendedEntries = classOrObject.superTypeListEntries
    if (extendedEntries.isNotEmpty()) baseName += " : "
    extendedEntries.forEach { baseName += it.typeAsUserType?.referencedName.orEmpty() }
    return baseName
}

private fun buildFunctionSignature(element: KtNamedFunction): String {
    val startOffset = element.startOffsetSkippingComments - element.startOffset
    val endOffset = if (element.typeReference != null) {
        element.typeReference?.endOffset ?: 0
    } else {
        element.valueParameterList?.endOffset ?: 0
    } - element.startOffset

    require(startOffset < endOffset) {
        "Error building function signature with range $startOffset - $endOffset for element: ${element.text}"
    }
    return element.text.substring(startOffset, endOffset)
}
