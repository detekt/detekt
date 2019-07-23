package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.jetbrains.kotlin.asJava.namedUnwrappedElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

fun testEntity(element: KtElement) = Entity(
    element.namedUnwrappedElement?.name ?: "",
    "",
    "",
    testLocation(element)
)

fun testLocation(element: KtElement): Location {
    val start = Location.startLineAndColumn(element)
    val sourceLocation = SourceLocation(start.line, start.column)
    val textLocation = TextLocation(element.startOffset, element.endOffset)
    return Location(sourceLocation, textLocation, "", "")
}
