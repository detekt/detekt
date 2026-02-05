package dev.detekt.rules.libraries

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

/**
 * Functions/properties exposed as public APIs of a library should have an explicit return type.
 * Inferred return type can easily be changed by mistake which may lead to breaking changes.
 *
 * See also: [Kotlin 1.4 Explicit API](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 *
 * <noncompliant>
 * // code from a library
 * val strs = listOf("foo, bar")
 * fun bar() = 5
 * class Parser {
 *      fun parse() = ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * // code from a library
 * val strs: List<String> = listOf("foo, bar")
 * fun bar(): Int = 5
 *
 * class Parser {
 *      fun parse(): ParsingResult = ...
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.2.0")
class LibraryCodeMustSpecifyReturnType(config: Config) :
    Rule(
        config,
        "Library functions/properties should have an explicit return type. " +
            "Inferred return types can easily be changed by mistake which may lead to breaking changes."
    ),
    RequiresAnalysisApi {

    @Configuration("if functions with `Unit` return type should be allowed without return type declaration")
    private val allowOmitUnit: Boolean by config(false)

    override fun visitProperty(property: KtProperty) {
        property.check()
        super.visitProperty(property)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        function.check()
        super.visitNamedFunction(function)
    }

    @Suppress("ReturnCount")
    private fun KtCallableDeclaration.check() {
        if (this.typeReference != null) return

        if (this.containingClassOrObject?.isLocal == true) return

        if (this is KtProperty) {
            if (this.isLocal) return
            analyze(this) {
                if (!isPublicApi(symbol)) return
            }
        }

        if (this is KtNamedFunction) {
            if (this.isLocal || this.hasBlockBody() || bodyExpression?.text == "Unit") return
            analyze(this) {
                if (allowOmitUnit && symbol.returnType.isUnitType) return
                if (!isPublicApi(symbol)) return
            }
        }

        report(
            Finding(
                Entity.atName(this),
                "Library function '${this.nameAsSafeName}' without explicit return type."
            )
        )
    }
}
