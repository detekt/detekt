package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.NUMBER_OF_FIELDS_KEY
import io.gitlab.arturbosch.detekt.core.visitors.FieldCountVisitor

class FieldCountProcessor : AbstractProcessor() {

    override val visitor = FieldCountVisitor()
    override val key = NUMBER_OF_FIELDS_KEY
}
