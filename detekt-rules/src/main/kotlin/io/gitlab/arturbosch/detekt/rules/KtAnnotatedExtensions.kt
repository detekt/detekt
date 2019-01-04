package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

fun KtAnnotated.hasAnnotationWithValue(
		annotationName: String,
		annotationValueText: String
): Boolean {
	return annotationEntries.any { it.isAnnotationWithValue(annotationName, annotationValueText) }
}

private fun KtAnnotationEntry.isAnnotationWithValue(
		annotationName: String,
		annotationValueText: String
): Boolean {
	return typeReference.isAnnotationWithName(annotationName) &&
			valueArgumentList.containsAnnotationValue(annotationValueText)
}

private fun KtTypeReference?.isAnnotationWithName(annotationName: String): Boolean {
	if (this == null) {
		return false
	}

	val type = typeElement

	return if (type is KtUserType) {
		type.referencedName == annotationName
	} else {
		false
	}
}

private fun KtValueArgumentList?.containsAnnotationValue(annotationValueText: String): Boolean {
	return this?.arguments
			?.any { it.hasValue(annotationValueText) }
			?: false
}

private fun KtValueArgument.hasValue(annotationValueText: String): Boolean {
	return text == annotationValueText
}
