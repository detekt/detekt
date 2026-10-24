@file:Suppress("TooManyFunctions")

package dev.detekt.rules.style

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isPartOf
import org.jetbrains.kotlin.analysis.api.KaContextParameterApi
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.containingDeclaration
import org.jetbrains.kotlin.analysis.api.components.expectedType
import org.jetbrains.kotlin.analysis.api.components.expressionType
import org.jetbrains.kotlin.analysis.api.components.isSubClassOf
import org.jetbrains.kotlin.analysis.api.components.isSubtypeOf
import org.jetbrains.kotlin.analysis.api.components.resolveSymbol
import org.jetbrains.kotlin.analysis.api.components.resolveToCall
import org.jetbrains.kotlin.analysis.api.components.resolveToCallCandidates
import org.jetbrains.kotlin.analysis.api.components.returnType
import org.jetbrains.kotlin.analysis.api.components.scopeContext
import org.jetbrains.kotlin.analysis.api.components.semanticallyEquals
import org.jetbrains.kotlin.analysis.api.diagnostics.KaDiagnosticWithPsi
import org.jetbrains.kotlin.analysis.api.resolution.KaCallCandidateInfo
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.KaInapplicableCallCandidateInfo
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassLikeSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaEnumEntrySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.symbols.KaTypeParameterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaNamedSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.analysis.api.types.KaFunctionType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExperimentalApi
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtWhenConditionIsPattern
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * Reports qualified enum entries, sealed subtypes and companion object properties that can be written without their
 * qualifier, because context-sensitive resolution finds them from the expected type.
 *
 * Context-sensitive resolution is a preview feature, enabled with the `-Xcontext-sensitive-resolution` compiler flag.
 * This rule does nothing unless the flag is set.
 *
 * The rule does not report:
 * - Names that already resolve without the qualifier, like an enum entry used inside its own enum
 * - Sealed subtypes nested more than one level below the expected type
 * - Sealed objects and companion properties in an if or when branch passed as an argument, since the other branches can
 * change the expected type
 * - Arguments that decide a type parameter of a function with other type parameters too
 * - Imports that are only needed without context-sensitive resolution, like `import com.example.Role.Admin` or
 * `import com.example.State.Failure`
 *
 * See the [Kotlin docs](https://kotlinlang.org/docs/whatsnew22.html#preview-of-context-sensitive-resolution),
 * the [KEEP](https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0379-context-sensitive-resolution.md) and
 * [KT-16768](https://youtrack.jetbrains.com/issue/KT-16768). Since the feature is in preview, its behavior and
 * this rule may change.
 *
 * <noncompliant>
 * enum class Role { Admin, Basic }
 *
 * sealed interface State {
 *     data object Loading : State
 *     data class Failure(val reason: String) : State
 * }
 *
 * class Color(val rgb: Int) {
 *     companion object {
 *         val Red = Color(0xFF0000)
 *     }
 * }
 *
 * fun takesRole(role: Role) = role
 * fun takesColor(color: Color) = color
 * fun takesState(state: State) = state
 *
 * fun foo(role: Role, state: State, fallback: Role?): Role {
 *     takesRole(Role.Admin)
 *     takesColor(Color.Red)
 *     takesState(State.Loading)
 *     val roles = listOf(Role.Admin, Role.Basic)
 *     if (state is State.Failure) return fallback ?: Role.Admin
 *     if (role == Role.Admin) return Role.Basic
 *     return when (role) {
 *         Role.Admin -> Role.Basic
 *         Role.Basic -> Role.Admin
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * enum class Role { Admin, Basic }
 *
 * sealed interface State {
 *     data object Loading : State
 *     data class Failure(val reason: String) : State
 * }
 *
 * class Color(val rgb: Int) {
 *     companion object {
 *         val Red = Color(0xFF0000)
 *     }
 * }
 *
 * fun takesRole(role: Role) = role
 * fun takesColor(color: Color) = color
 * fun takesState(state: State) = state
 *
 * fun foo(role: Role, state: State, fallback: Role?): Role {
 *     takesRole(Admin)
 *     takesColor(Red)
 *     takesState(Loading)
 *     val roles = listOf<Role>(Admin, Basic)
 *     if (state is Failure) return fallback ?: Admin
 *     if (role == Admin) return Basic
 *     return when (role) {
 *         Admin -> Basic
 *         Basic -> Admin
 *     }
 * }
 *
 * // The qualifier is needed when there is no expected type, or the argument decides it
 * fun bar(): List<Any> {
 *     val role = Role.Admin
 *     val any: Any = Role.Basic
 *     val single = listOf(Role.Admin)
 *     val failure = takesState(State.Failure("x"))
 *     return listOf(role, any, single, failure)
 * }
 * </compliant>
 */
class UseContextSensitiveResolution(config: Config) :
    Rule(config, "The qualifier can be dropped, context-sensitive resolution finds the name."),
    RequiresAnalysisApi {

    override fun visit(root: KtFile) {
        if (languageVersionSettings.supportsFeature(LanguageFeature.ContextSensitiveResolutionUsingExpectedType)) {
            super.visit(root)
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        // Calls like State.Failure("x") have a call as the selector, so are skipped here
        val selector = expression.selectorExpression as? KtNameReferenceExpression ?: return
        // Only look at the whole chain, not Role.Admin inside Role.Admin.name
        val parent = expression.parent
        if (parent is KtDotQualifiedExpression && parent.receiverExpression == expression) return
        // Receivers like makeUser() or this can't name a class, so there is no need to analyze them
        if (!expression.receiverExpression.isNameChain()) return
        if (hasNoExpectedType(expression)) return

        analyze(expression) {
            @OptIn(KaExperimentalApi::class)
            val symbol = selector.resolveSymbol() ?: return
            val owner = owner(symbol) ?: return
            val expected = expectedType(expression) ?: return
            if (!expected.isSearchedIn(owner)) return
            // In `failure ?: State.Loading` the left side's type State.Failure is expected, and Loading isn't one
            if (expression.expressionType?.isSubtypeOf(expected) != true) return
            // A branch of an if or when passed as an argument can get its expected type from the other branches.
            // In `takesResponse(if (x) Response.Success(1) else Empty)` that is Response.Success<Int>, so Empty
            // isn't found. Every enum entry has the enum's type, so enum entries are safe
            if (symbol !is KaEnumEntrySymbol && argumentBranchRoot(expression) != null) return
            if (!isInScope(expression, symbol)) {
                report(expression, selector.getReferencedName(), "the expected type is known")
            }
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.typeArgumentList != null) return

        val callee = expression.calleeExpression as? KtNameReferenceExpression ?: return
        val qualified = qualifiedArguments(expression) ?: return
        val names = qualified.map { it.selectorExpression as? KtNameReferenceExpression ?: return }

        analyze(expression) {
            val resolved = expression.resolveToCall()?.successfulFunctionCallOrNull() ?: return

            // pairOf<A, B>(...) would need more than one type argument
            if (resolved.typeArgumentsMapping.size != 1) return
            val owner = commonOwner(names) ?: return
            if (!qualified.all { qualifiesWith(it, owner) }) return
            if (!qualified.all { decidesTypeArgument(resolved, it, owner) }) return

            val arguments = names.joinToString { it.getReferencedName() }
            val original = "${callee.text}(${qualified.joinToString { it.text }})"
            // val roles: List<Role> = listOf(Role.Admin) already gives listOf its type argument
            val typed = expression.expressionType?.let { expectedType(expression)?.semanticallyEquals(it) } == true
            if (typed) {
                report(expression, "${callee.text}($arguments)", "the expected type is known", original)
            } else {
                // listOf<Response>(...) would be missing Response's own type arguments
                if (owner.typeParameters.isNotEmpty()) return
                // listOf<Role>(Admin) is no shorter than listOf(Role.Admin)
                if (qualified.size < 2) return
                val receiver = qualified.first().receiverExpression.text
                val reason = "a type argument gives the names an expected type"
                report(expression, "${callee.text}<$receiver>($arguments)", reason, original)
            }
        }
    }

    override fun visitTypeReference(typeReference: KtTypeReference) {
        super.visitTypeReference(typeReference)

        val nullable = typeReference.typeElement as? KtNullableType
        val userType = (nullable?.innerType ?: typeReference.typeElement) as? KtUserType ?: return
        if (userType.qualifier == null) return
        val name = userType.referenceExpression ?: return

        val subject = checkedValue(typeReference) ?: return

        analyze(typeReference) {
            val symbol = (typeReference.type as? KaClassType)?.symbol ?: return
            val owner = owner(symbol) ?: return
            val expected = if (subject is KtWhenExpression) subjectType(subject) else subject.expressionType
            if (expected?.isSearchedIn(owner) != true) return
            if (isInScope(typeReference, symbol, isType = true)) return
            val replacement = name.getReferencedName() + userType.typeArgumentList?.text.orEmpty()
            report(typeReference, if (nullable != null) "$replacement?" else replacement, "the expected type is known")
        }
    }

    // The value being checked or cast, or the `when` whose subject is
    private fun checkedValue(typeReference: KtTypeReference): KtExpression? =
        when (val parent = typeReference.parent) {
            is KtIsExpression -> parent.leftHandSide
            is KtBinaryExpressionWithTypeRHS -> parent.left
            is KtWhenConditionIsPattern -> parent.getStrictParentOfType<KtWhenExpression>()
            else -> null
        }

    // Imports, package directives and untyped declarations like val role = Role.Admin
    private fun hasNoExpectedType(expression: KtExpression): Boolean =
        expression.isPartOf<KtImportDirective>() ||
            expression.isPartOf<KtPackageDirective>() ||
            isUntypedDeclaration(expression)

    private fun KtExpression.isNameChain(): Boolean =
        when (this) {
            is KtNameReferenceExpression -> true

            is KtDotQualifiedExpression ->
                selectorExpression is KtNameReferenceExpression && receiverExpression.isNameChain()

            else -> false
        }

    private fun qualifiedArguments(expression: KtCallExpression): List<KtDotQualifiedExpression>? {
        val arguments = expression.valueArguments
        val qualified = arguments
            .filter { !it.isNamed() && it.getSpreadElement() == null }
            .mapNotNull { it.getArgumentExpression() as? KtDotQualifiedExpression }
        return qualified.takeIf { it.isNotEmpty() && it.size == arguments.size && it.shareReceiverName() }
    }

    // qualifiesWith needs every receiver to end in the owner's name, which listOf(Role.Admin, Method.Header) can't
    private fun List<KtDotQualifiedExpression>.shareReceiverName(): Boolean {
        val names = map { receiverName(it)?.getReferencedName() }
        return names.first() != null && names.distinct().size == 1
    }

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun commonOwner(names: List<KtNameReferenceExpression>): KaNamedClassSymbol? =
        names
            .map { name ->
                @OptIn(KaExperimentalApi::class)
                val symbol = name.resolveSymbol() ?: return null
                if (isInScope(name, symbol)) return null
                owner(symbol) ?: return null
            }
            .distinct()
            .singleOrNull()

    // Whether the argument goes to a parameter typed by the function's own type parameter, like the T in
    // listOf's vararg parameter, and that type parameter is currently inferred as the owner class
    context(_: KaSession)
    private fun decidesTypeArgument(
        call: KaFunctionCall<*>,
        argument: KtExpression,
        owner: KaNamedClassSymbol,
    ): Boolean {
        val signature = call.valueArgumentMapping[argument] ?: return false
        val declared = signature.symbol.returnType
        return declared is KaTypeParameterType &&
            isDecidedByArgument(declared.symbol, call.symbol) &&
            signature.returnType.isClass(owner)
    }

    private fun report(element: KtElement, replacement: String, reason: String, original: String = element.text) =
        report(
            Finding(
                entity = Entity.from(element),
                message = "'$original' can be written as '$replacement', $reason.",
            ),
        )

    // Whether the qualifier names the class itself, like Role in Role.Admin or com.example.Role in
    // com.example.Role.Admin. In Color.Red the Color resolves to the companion, not the class
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun qualifiesWith(expression: KtDotQualifiedExpression, owner: KaNamedClassSymbol): Boolean {
        val name = receiverName(expression) ?: return false
        if (name.getReferencedName() != owner.name.asString()) return false
        @OptIn(KaExperimentalApi::class)
        val symbol = name.resolveSymbol()
        val isCompanion = symbol is KaNamedClassSymbol &&
            symbol.classKind == KaClassKind.COMPANION_OBJECT &&
            symbol.containingDeclaration == owner
        return symbol == owner || isCompanion
    }

    // The last name in the receiver, like Role in com.example.Role.Admin
    private fun receiverName(expression: KtDotQualifiedExpression): KtNameReferenceExpression? {
        val receiver = expression.receiverExpression
        return ((receiver as? KtDotQualifiedExpression)?.selectorExpression ?: receiver) as? KtNameReferenceExpression
    }

    // The type whose scope the symbol is found in, if it's one context-sensitive resolution looks in
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun owner(symbol: KaSymbol): KaNamedClassSymbol? {
        val owner = symbol.containingDeclaration as? KaNamedClassSymbol ?: return null
        return when (symbol) {
            is KaEnumEntrySymbol -> owner

            is KaNamedClassSymbol -> owner.takeIf {
                it.modality == KaSymbolModality.SEALED &&
                    symbol.classKind != KaClassKind.COMPANION_OBJECT &&
                    symbol.isSubClassOf(it)
            }

            is KaPropertySymbol -> (owner.containingDeclaration as? KaNamedClassSymbol)?.takeIf {
                owner.classKind == KaClassKind.COMPANION_OBJECT && !symbol.isExtension
            }

            else -> null
        }
    }

    // The expected type context-sensitive resolution would use, or null. The Analysis API's expectedType is
    // too generous for some positions, so those are worked out by hand
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun expectedType(expression: KtExpression): KaType? =
        when (val parent = expression.parent) {
            is KtBinaryExpression -> operandType(parent, expression)

            is KtWhenConditionWithExpression ->
                parent.getStrictParentOfType<KtWhenExpression>()?.let { subjectType(it) }

            is KtValueArgument -> argumentType(parent, expression)

            is KtParenthesizedExpression -> expectedType(parent)

            else -> {
                val branchRoot = argumentBranchRoot(expression)
                if (branchRoot != null) {
                    argumentType(branchRoot.parent as KtValueArgument, branchRoot)
                } else {
                    tryResultType(expression)
                        ?: expression.expectedType?.takeUnless {
                            isUntypedDeclaration(expression) || isInferredLambdaResult(expression)
                        }
                }
            }
        }

    // "override val role = Role.Admin" and "val role = if (x) Role.Admin else Role.Basic" take their
    // type from the initializer, so there is no expected type to use
    private fun isUntypedDeclaration(expression: KtExpression): Boolean {
        val parent = expression.parents.firstOrNull { element ->
            !element.isBranchBody() && element !is KtBlockExpression && element !is KtParenthesizedExpression
        }
        val declaration = (parent as? KtPropertyAccessor)?.property ?: parent
        return declaration is KtCallableDeclaration &&
            declaration !is KtFunctionLiteral &&
            declaration.typeReference == null
    }

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun argumentType(argument: KtValueArgument, expression: KtExpression): KaType? {
        val call = callOf(argument)
        val inferred = call != null && isInferred(call, expression) { type -> type }
        return if (inferred) null else expression.expectedType
    }

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun operandType(binary: KtBinaryExpression, operand: KtExpression): KaType? =
        when (binary.operationToken) {
            // The right side of role == Role.Admin takes the type of the left. The left side gets nothing
            in EQUALITY -> if (binary.right == operand) binary.left?.expressionType else null

            KtTokens.EQ -> operand.expectedType

            KtTokens.ELVIS -> if (binary.right == operand) binary.left?.expressionType else null

            // roles += Role.Admin doesn't pass an expected type down
            in KtTokens.AUGMENTED_ASSIGNMENTS -> null

            else -> {
                val resolved = binary.resolveToCall()?.successfulFunctionCallOrNull()
                val signature = resolved?.valueArgumentMapping?.get(operand)
                signature?.returnType?.takeUnless { isInferred(binary, operand) { type -> type } }
            }
        }

    context(_: KaSession)
    private fun tryResultType(expression: KtExpression): KaType? {
        val block = expression.parent as? KtBlockExpression ?: return null
        if (block.statements.lastOrNull() != expression) return null
        val tryExpression = when (val parent = block.parent) {
            is KtTryExpression -> parent.takeIf { it.tryBlock == block }
            is KtCatchClause -> parent.parent as? KtTryExpression
            else -> null
        }
        return tryExpression?.let { expectedType(it) }
    }

    // The `if`, `when` or `try` that the expression is a branch result of, when that sits directly in an argument
    private fun argumentBranchRoot(expression: KtExpression): KtExpression? {
        val path = expression.parents
            .takeWhile { it.isBranchBody() || it is KtBlockExpression || it is KtParenthesizedExpression }
            .toList()
        if (path.none { it.isBranchBody() }) return null
        return (path.last() as? KtExpression)?.takeIf { it.parent is KtValueArgument }
    }

    private fun PsiElement.isBranchBody(): Boolean =
        this is KtContainerNodeForControlStructureBody ||
            this is KtWhenEntry ||
            this is KtIfExpression ||
            this is KtWhenExpression ||
            this is KtTryExpression ||
            this is KtCatchClause

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun subjectType(whenExpression: KtWhenExpression): KaType? =
        whenExpression.subjectVariable?.returnType ?: whenExpression.subjectExpression?.expressionType

    // A trailing lambda's parent is the call itself, other arguments sit inside a KtValueArgumentList
    private fun callOf(argument: KtValueArgument): KtCallExpression? =
        when (val parent = argument.parent) {
            is KtValueArgumentList -> parent.parent as? KtCallExpression
            else -> parent as? KtCallExpression
        }

    // Whether the expression is a lambda's result and the lambda's return type is inferred from it, as in
    // associateWith { Role.Admin }. flow.update { State.Loading } is fine, since update fixes the type
    context(_: KaSession)
    private fun isInferredLambdaResult(expression: KtExpression): Boolean {
        val lambda = lambdaReturning(expression) ?: return false
        val argument = lambda.parent as? KtValueArgument ?: return isUntypedDeclaration(lambda)
        val call = callOf(argument) ?: return true
        return isInferred(call, lambda) { type -> (type as? KaFunctionType)?.returnType }
    }

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun lambdaReturning(element: PsiElement): KtLambdaExpression? {
        val parent = element.parent
        val passesResultUp = when (parent) {
            is KtFunctionLiteral -> return parent.parent as? KtLambdaExpression

            is KtBlockExpression -> parent.statements.lastOrNull() == element

            is KtIfExpression -> parent.condition != element

            // return@label leaves the lambda it names from anywhere in its body. A plain return leaves the
            // enclosing function instead
            is KtReturnExpression -> {
                val label = parent.getTargetLabel() ?: return null

                @OptIn(KaExperimentalApi::class, KtExperimentalApi::class)
                val target = label.resolveSymbol()?.psi as? KtFunctionLiteral
                return target?.parent as? KtLambdaExpression
            }

            is KtWhenEntry,
            is KtWhenExpression,
            is KtContainerNodeForControlStructureBody,
            is KtParenthesizedExpression,
            -> true

            else -> false
        }
        return if (passesResultUp) lambdaReturning(parent) else null
    }

    // Whether the type comes from the argument itself, as it does for T in listOf(Role.Admin), or
    // can't be pinned down because overloads disagree about it. There is no expected type in both
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun isInferred(call: KtElement, argument: KtExpression, select: (KaType) -> KaType?): Boolean {
        val resolved = call.resolveToCall()?.successfulFunctionCallOrNull()
        val signature = resolved?.valueArgumentMapping?.get(argument)
        val declared = signature?.let { select(it.symbol.returnType) }
        val substituted = signature?.let { select(it.returnType) }
        if (resolved == null || declared == null || substituted == null) return true

        // overloaded(Role.Admin) with overloaded(Role) and overloaded(Int) has no single expected type
        val overloadsDisagree = call.resolveToCallCandidates().any { info ->
            val candidate = info.candidate as? KaFunctionCall<*>
            val other = candidate?.valueArgumentMapping?.get(argument)?.returnType
            other != null &&
                !failsElsewhere(info, argument) &&
                select(other)?.semanticallyEquals(substituted) != true
        }

        val decidedByArgument = (call as? KtCallExpression)?.typeArgumentList == null &&
            declared is KaTypeParameterType &&
            isDecidedByArgument(declared.symbol, resolved.symbol) &&
            !isFixedByOtherArgument(resolved, argument, declared.symbol, substituted)
        return overloadsDisagree || decidedByArgument
    }

    // Whether another argument already gives the type parameter its type, as makeRole() does in
    // listOf(Role.Admin, makeRole())
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun isFixedByOtherArgument(
        call: KaFunctionCall<*>,
        argument: KtExpression,
        parameter: KaTypeParameterSymbol,
        type: KaType,
    ): Boolean =
        call.valueArgumentMapping.any { (other, signature) ->
            other != argument &&
                (signature.symbol.returnType as? KaTypeParameterType)?.symbol == parameter &&
                !mayLoseQualifier(other) &&
                other.expressionType?.semanticallyEquals(type) == true
        }

    // Names the rule could report, and branches that could hold them, may not keep their type once fixed
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun mayLoseQualifier(expression: KtExpression): Boolean {
        val unwrapped = KtPsiUtil.safeDeparenthesize(expression)
        if (unwrapped.isBranchBody()) return true
        val selector = (unwrapped as? KtDotQualifiedExpression)?.selectorExpression as? KtNameReferenceExpression
            ?: return false

        @OptIn(KaExperimentalApi::class)
        val symbol = selector.resolveSymbol() ?: return true
        return owner(symbol) != null
    }

    // A candidate that fails on something other than this argument, like Assert<String?>.isEqualTo
    // called on an Assert<Role>, is dropped before the argument is looked at
    private fun failsElsewhere(info: KaCallCandidateInfo, argument: KtExpression): Boolean {
        val diagnostic = (info as? KaInapplicableCallCandidateInfo)?.diagnostic
        val psi = (diagnostic as? KaDiagnosticWithPsi<*>)?.psi ?: return false
        return !PsiTreeUtil.isAncestor(argument.parent as? KtValueArgument ?: argument, psi, false)
    }

    // Whether the arguments decide the type parameter, as they do for listOf and constructors.
    // Assert<T>.isEqualTo(expected: T) gets T from its receiver, not the argument
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun isDecidedByArgument(parameter: KaTypeParameterSymbol, function: KaFunctionSymbol): Boolean {
        if (function is KaConstructorSymbol) return true
        if (parameter.containingDeclaration != function) return false
        val receiver = function.receiverParameter?.returnType ?: return true
        return !receiver.mentions(parameter)
    }

    private fun KaType.mentions(parameter: KaTypeParameterSymbol): Boolean =
        when (this) {
            is KaTypeParameterType -> symbol == parameter
            is KaClassType -> typeArguments.any { it.type?.mentions(parameter) == true }
            else -> false
        }

    // Java parameters have platform types like TimeUnit!
    private fun KaType.isClass(symbol: KaClassLikeSymbol): Boolean {
        val type = (this as? KaFlexibleType)?.lowerBound ?: this
        return (type as? KaClassType)?.symbol == symbol
    }

    // Context-sensitive resolution looks in the expected type's class, and in the sealed classes it is nested in
    // and extends, like Arrangement for Arrangement.Horizontal
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun KaType.isSearchedIn(owner: KaNamedClassSymbol): Boolean {
        if (isClass(owner)) return true
        val type = (this as? KaFlexibleType)?.lowerBound ?: this
        val expected = (type as? KaClassType)?.symbol as? KaNamedClassSymbol ?: return false
        return generateSequence(expected.containingDeclaration as? KaNamedClassSymbol) {
            it.containingDeclaration as? KaNamedClassSymbol
        }
            .takeWhile { expected.isSubClassOf(it) }
            .any { it == owner && it.modality == KaSymbolModality.SEALED }
    }

    // A plain name that already resolves wins over context-sensitive resolution, whether it finds something
    // else, like an imported Admin class, or the target itself, as inside the class. In a type, only classes count
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun isInScope(position: KtElement, target: KaSymbol, isType: Boolean = false): Boolean {
        val name = (target as? KaNamedSymbol)?.name ?: return true
        val context = position.containingKtFile.scopeContext(position)

        @OptIn(KaExperimentalApi::class)
        val receivers = context.implicitValues.map { it.type }
        return context.scopes.any { scoped ->
            scoped.scope.classifiers(name).any() ||
                (!isType && scoped.scope.callables(name).any { it.isReachableWith(receivers) })
        }
    }

    // An extension like `val Icons.Admin` is only found when an implicit receiver fits it
    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun KaCallableSymbol.isReachableWith(receivers: List<KaType>): Boolean {
        val receiver = receiverParameter?.returnType ?: return true
        return receiver is KaTypeParameterType || receivers.any { it.isSubtypeOf(receiver) }
    }

    private companion object {
        val EQUALITY = setOf(KtTokens.EQEQ, KtTokens.EXCLEQ, KtTokens.EQEQEQ, KtTokens.EXCLEQEQEQ)
    }
}
