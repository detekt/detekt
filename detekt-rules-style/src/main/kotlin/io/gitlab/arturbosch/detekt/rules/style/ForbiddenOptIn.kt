package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.ValueWithReason
import dev.detekt.api.config
import dev.detekt.api.valuesWithReason
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * This rule allows to set a list of forbidden opt-ins. This can be used to avoid opting into an api by accident.
 * By default, the list of forbidden opt-ins is empty.
 */
class ForbiddenOptIn(config: Config) :
    Rule(
        config,
        "Using this opt-in is forbidden."
    ),
    RequiresAnalysisApi {

    @Configuration(
        "List of marker classes that are forbidden to be used."
    )
    private val markerClasses: Map<String, ValueWithReason> by config(valuesWithReason()) { list ->
        list.associateBy { it.value }
    }

    override fun visitAnnotationEntry(annotation: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotation)
        if (markerClasses.isEmpty()) {
            return
        }

        analyze(annotation) {
            check(annotation, annotation.typeReference?.type)
        }
    }

    private fun check(annotation: KtAnnotationEntry, type: KaType?) {
        if (type?.symbol?.classId != optInClassId) {
            return
        }

        val usedOptIn = annotation.valueArguments.mapNotNull { arg ->
            val optInClassArgument = arg.getArgumentExpression() as? KtClassLiteralExpression
            (optInClassArgument?.receiverExpression as? KtNameReferenceExpression)?.getReferencedName()
        }.toSet()

        val forbidden = usedOptIn.intersect(markerClasses.keys)
        forbidden.forEach { forbiddenOptIn ->
            val reason = markerClasses[forbiddenOptIn]?.reason
            val message = if (reason != null) {
                "The opt-in `$forbiddenOptIn` has been forbidden: $reason"
            } else {
                "The opt-in `$forbiddenOptIn` has been forbidden in the detekt config."
            }
            report(Finding(Entity.from(annotation), message))
        }
    }

    companion object {
        private val optInClassId: ClassId = ClassId.fromString("kotlin/OptIn")
    }
}
