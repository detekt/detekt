package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.com.intellij.psi.templateLanguages.OuterLanguageElement
import org.jetbrains.kotlin.psi.*

/**
 * Base visitor for detekt rules.
 *
 * @author Artur Bosch
 */
open class DetektVisitor : KtTreeVisitor<Context>() {
    // methods with void return

    open fun visitKtElement(context: Context, element: KtElement) {
        super.visitKtElement(element, context)
    }

    open fun visitDeclaration(context: Context, dcl: KtDeclaration) {
        super.visitDeclaration(dcl, context)
    }

    open fun visitClass(context: Context, klass: KtClass) {
        super.visitClass(klass, context)
    }

    open fun visitClassOrObject(context: Context, classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject, context)
    }

    open fun visitSecondaryConstructor(context: Context, constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor, context)
    }

    open fun visitPrimaryConstructor(context: Context, constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor, context)
    }

    open fun visitNamedFunction(context: Context, function: KtNamedFunction) {
        super.visitNamedFunction(function, context)
    }

    open fun visitProperty(context: Context, property: KtProperty) {
        super.visitProperty(property, context)
    }

    open fun visitTypeAlias(context: Context, typeAlias: KtTypeAlias) {
        super.visitTypeAlias(typeAlias, context)
    }

    open fun visitDestructuringDeclaration(context: Context,
                                           destructuringDeclaration: KtDestructuringDeclaration) {
        super.visitDestructuringDeclaration(destructuringDeclaration, context)
    }

    open fun visitDestructuringDeclarationEntry(context: Context,
                                                multiDeclarationEntry: KtDestructuringDeclarationEntry) {
        super.visitDestructuringDeclarationEntry(multiDeclarationEntry, context)
    }

    open fun visitKtFile(context: Context, file: KtFile) {
        super.visitKtFile(file, context)
    }

    open fun visitScript(context: Context, script: KtScript) {
        super.visitScript(script, context)
    }

    open fun visitImportDirective(context: Context, importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective, context)
    }

    open fun visitImportList(context: Context, importList: KtImportList) {
        super.visitImportList(importList, context)
    }

    open fun visitClassBody(context: Context, classBody: KtClassBody) {
        super.visitClassBody(classBody, context)
    }

    open fun visitModifierList(context: Context, list: KtModifierList) {
        super.visitModifierList(list, context)
    }

    open fun visitAnnotation(context: Context, annotation: KtAnnotation) {
        super.visitAnnotation(annotation, context)
    }

    open fun visitAnnotationEntry(context: Context, annotationEntry: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotationEntry, context)
    }

    open fun visitConstructorCalleeExpression(context: Context,
                                              constructorCalleeExpression: KtConstructorCalleeExpression) {
        super.visitConstructorCalleeExpression(constructorCalleeExpression, context)
    }

    open fun visitTypeParameterList(context: Context, list: KtTypeParameterList) {
        super.visitTypeParameterList(list, context)
    }

    open fun visitTypeParameter(context: Context, parameter: KtTypeParameter) {
        super.visitTypeParameter(parameter, context)
    }

    open fun visitEnumEntry(context: Context, enumEntry: KtEnumEntry) {
        super.visitEnumEntry(enumEntry, context)
    }

    open fun visitParameterList(context: Context, list: KtParameterList) {
        super.visitParameterList(list, context)
    }

    open fun visitParameter(context: Context, parameter: KtParameter) {
        super.visitParameter(parameter, context)
    }

    open fun visitSuperTypeList(context: Context, list: KtSuperTypeList) {
        super.visitSuperTypeList(list, context)
    }

    open fun visitSuperTypeListEntry(context: Context, specifier: KtSuperTypeListEntry) {
        super.visitSuperTypeListEntry(specifier, context)
    }

    open fun visitDelegatedSuperTypeEntry(context: Context, specifier: KtDelegatedSuperTypeEntry) {
        super.visitDelegatedSuperTypeEntry(specifier, context)
    }

    open fun visitSuperTypeCallEntry(context: Context, call: KtSuperTypeCallEntry) {
        super.visitSuperTypeCallEntry(call, context)
    }

    open fun visitSuperTypeEntry(context: Context, specifier: KtSuperTypeEntry) {
        super.visitSuperTypeEntry(specifier, context)
    }

    open fun visitConstructorDelegationCall(context: Context, call: KtConstructorDelegationCall) {
        super.visitConstructorDelegationCall(call, context)
    }

    open fun visitPropertyDelegate(context: Context, delegate: KtPropertyDelegate) {
        super.visitPropertyDelegate(delegate, context)
    }

    open fun visitTypeReference(context: Context, typeReference: KtTypeReference) {
        super.visitTypeReference(typeReference, context)
    }

    open fun visitValueArgumentList(context: Context, list: KtValueArgumentList) {
        super.visitValueArgumentList(list, context)
    }

    open fun visitArgument(context: Context, argument: KtValueArgument) {
        super.visitArgument(argument, context)
    }

    open fun visitExpression(context: Context, expression: KtExpression) {
        super.visitExpression(expression, context)
    }

    open fun visitLoopExpression(context: Context, loopExpression: KtLoopExpression) {
        super.visitLoopExpression(loopExpression, context)
    }

    open fun visitConstantExpression(context: Context, expression: KtConstantExpression) {
        super.visitConstantExpression(expression, context)
    }

    open fun visitSimpleNameExpression(context: Context, expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression, context)
    }

    open fun visitReferenceExpression(context: Context, expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression, context)
    }

    open fun visitLabeledExpression(context: Context, expression: KtLabeledExpression) {
        super.visitLabeledExpression(expression, context)
    }

    open fun visitPrefixExpression(context: Context, expression: KtPrefixExpression) {
        super.visitPrefixExpression(expression, context)
    }

    open fun visitPostfixExpression(context: Context, expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression, context)
    }

    open fun visitUnaryExpression(context: Context, expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression, context)
    }

    open fun visitBinaryExpression(context: Context, expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression, context)
    }

    open fun visitReturnExpression(context: Context, expression: KtReturnExpression) {
        super.visitReturnExpression(expression, context)
    }

    open fun visitExpressionWithLabel(context: Context, expression: KtExpressionWithLabel) {
        super.visitExpressionWithLabel(expression, context)
    }

    open fun visitThrowExpression(context: Context, expression: KtThrowExpression) {
        super.visitThrowExpression(expression, context)
    }

    open fun visitBreakExpression(context: Context, expression: KtBreakExpression) {
        super.visitBreakExpression(expression, context)
    }

    open fun visitContinueExpression(context: Context, expression: KtContinueExpression) {
        super.visitContinueExpression(expression, context)
    }

    open fun visitIfExpression(context: Context, expression: KtIfExpression) {
        super.visitIfExpression(expression, context)
    }

    open fun visitWhenExpression(context: Context, expression: KtWhenExpression) {
        super.visitWhenExpression(expression, context)
    }

    open fun visitCollectionLiteralExpression(context: Context, expression: KtCollectionLiteralExpression) {
        super.visitCollectionLiteralExpression(expression, context)
    }

    open fun visitTryExpression(context: Context, expression: KtTryExpression) {
        super.visitTryExpression(expression, context)
    }

    open fun visitForExpression(context: Context, expression: KtForExpression) {
        super.visitForExpression(expression, context)
    }

    open fun visitWhileExpression(context: Context, expression: KtWhileExpression) {
        super.visitWhileExpression(expression, context)
    }

    open fun visitDoWhileExpression(context: Context, expression: KtDoWhileExpression) {
        super.visitDoWhileExpression(expression, context)
    }

    open fun visitLambdaExpression(context: Context, lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression, context)
    }

    open fun visitAnnotatedExpression(context: Context, expression: KtAnnotatedExpression) {
        super.visitAnnotatedExpression(expression, context)
    }

    open fun visitCallExpression(context: Context, expression: KtCallExpression) {
        super.visitCallExpression(expression, context)
    }

    open fun visitArrayAccessExpression(context: Context, expression: KtArrayAccessExpression) {
        super.visitArrayAccessExpression(expression, context)
    }

    open fun visitQualifiedExpression(context: Context, expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression, context)
    }

    open fun visitDoubleColonExpression(context: Context, expression: KtDoubleColonExpression) {
        super.visitDoubleColonExpression(expression, context)
    }

    open fun visitCallableReferenceExpression(context: Context, expression: KtCallableReferenceExpression) {
        super.visitCallableReferenceExpression(expression, context)
    }

    open fun visitClassLiteralExpression(context: Context, expression: KtClassLiteralExpression) {
        super.visitClassLiteralExpression(expression, context)
    }

    open fun visitDotQualifiedExpression(context: Context, expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression, context)
    }

    open fun visitSafeQualifiedExpression(context: Context, expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression, context)
    }

    open fun visitObjectLiteralExpression(context: Context, expression: KtObjectLiteralExpression) {
        super.visitObjectLiteralExpression(expression, context)
    }

    open fun visitBlockExpression(context: Context, expression: KtBlockExpression) {
        super.visitBlockExpression(expression, context)
    }

    open fun visitCatchSection(context: Context, catchClause: KtCatchClause) {
        super.visitCatchSection(catchClause, context)
    }

    open fun visitFinallySection(context: Context, finallySection: KtFinallySection) {
        super.visitFinallySection(finallySection, context)
    }

    open fun visitTypeArgumentList(context: Context, typeArgumentList: KtTypeArgumentList) {
        super.visitTypeArgumentList(typeArgumentList, context)
    }

    open fun visitThisExpression(context: Context, expression: KtThisExpression) {
        super.visitThisExpression(expression, context)
    }

    open fun visitSuperExpression(context: Context, expression: KtSuperExpression) {
        super.visitSuperExpression(expression, context)
    }

    open fun visitParenthesizedExpression(context: Context, expression: KtParenthesizedExpression) {
        super.visitParenthesizedExpression(expression, context)
    }

    open fun visitInitializerList(context: Context, list: KtInitializerList) {
        super.visitInitializerList(list, context)
    }

    open fun visitAnonymousInitializer(context: Context, initializer: KtAnonymousInitializer) {
        super.visitAnonymousInitializer(initializer, context)
    }

    open fun visitScriptInitializer(context: Context, initializer: KtScriptInitializer) {
        super.visitScriptInitializer(initializer, context)
    }

    open fun visitClassInitializer(context: Context, initializer: KtClassInitializer) {
        super.visitClassInitializer(initializer, context)
    }

    open fun visitPropertyAccessor(context: Context, accessor: KtPropertyAccessor) {
        super.visitPropertyAccessor(accessor, context)
    }

    open fun visitTypeConstraintList(context: Context, list: KtTypeConstraintList) {
        super.visitTypeConstraintList(list, context)
    }

    open fun visitTypeConstraint(context: Context, constraint: KtTypeConstraint) {
        super.visitTypeConstraint(constraint, context)
    }

    open fun visitUserType(context: Context, type: KtUserType) {
        super.visitUserType(type, context)
    }

    open fun visitDynamicType(context: Context, type: KtDynamicType) {
        super.visitDynamicType(type, context)
    }

    open fun visitFunctionType(context: Context, type: KtFunctionType) {
        super.visitFunctionType(type, context)
    }

    open fun visitSelfType(context: Context, type: KtSelfType) {
        super.visitSelfType(type, context)
    }

    open fun visitBinaryWithTypeRHSExpression(context: Context, expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression, context)
    }

    open fun visitStringTemplateExpression(context: Context, expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression, context)
    }

    open fun visitNamedDeclaration(context: Context, declaration: KtNamedDeclaration) {
        super.visitNamedDeclaration(declaration, context)
    }

    open fun visitNullableType(context: Context, nullableType: KtNullableType) {
        super.visitNullableType(nullableType, context)
    }

    open fun visitTypeProjection(context: Context, typeProjection: KtTypeProjection) {
        super.visitTypeProjection(typeProjection, context)
    }

    open fun visitWhenEntry(context: Context, jetWhenEntry: KtWhenEntry) {
        super.visitWhenEntry(jetWhenEntry, context)
    }

    open fun visitIsExpression(context: Context, expression: KtIsExpression) {
        super.visitIsExpression(expression, context)
    }

    open fun visitWhenConditionIsPattern(context: Context, condition: KtWhenConditionIsPattern) {
        super.visitWhenConditionIsPattern(condition, context)
    }

    open fun visitWhenConditionInRange(context: Context, condition: KtWhenConditionInRange) {
        super.visitWhenConditionInRange(condition, context)
    }

    open fun visitWhenConditionWithExpression(context: Context, condition: KtWhenConditionWithExpression) {
        super.visitWhenConditionWithExpression(condition, context)
    }

    open fun visitObjectDeclaration(context: Context, declaration: KtObjectDeclaration) {
        super.visitObjectDeclaration(declaration, context)
    }

    open fun visitStringTemplateEntry(context: Context, entry: KtStringTemplateEntry) {
        super.visitStringTemplateEntry(entry, context)
    }

    open fun visitStringTemplateEntryWithExpression(context: Context, entry: KtStringTemplateEntryWithExpression) {
        super.visitStringTemplateEntryWithExpression(entry, context)
    }

    open fun visitBlockStringTemplateEntry(context: Context, entry: KtBlockStringTemplateEntry) {
        super.visitBlockStringTemplateEntry(entry, context)
    }

    open fun visitSimpleNameStringTemplateEntry(context: Context, entry: KtSimpleNameStringTemplateEntry) {
        super.visitSimpleNameStringTemplateEntry(entry, context)
    }

    open fun visitLiteralStringTemplateEntry(context: Context, entry: KtLiteralStringTemplateEntry) {
        super.visitLiteralStringTemplateEntry(entry, context)
    }

    open fun visitEscapeStringTemplateEntry(context: Context, entry: KtEscapeStringTemplateEntry) {
        super.visitEscapeStringTemplateEntry(entry, context)
    }

    open fun visitPackageDirective(context: Context, directive: KtPackageDirective) {
        super.visitPackageDirective(directive, context)
    }

    open fun visitAnnotationUseSiteTarget(context: Context, annotationTarget: KtAnnotationUseSiteTarget) {
        super.visitAnnotationUseSiteTarget(annotationTarget, context)
    }

    open fun visitFileAnnotationList(context: Context, fileAnnotationList: KtFileAnnotationList) {
        super.visitFileAnnotationList(fileAnnotationList, context)
    }

    // hidden methods
    override final fun visitKtElement(element: KtElement, context: Context): Void? {
        visitKtElement(context, element)
        return null
    }

    override final fun visitDeclaration(dcl: KtDeclaration, context: Context): Void? {
        visitDeclaration(context, dcl)
        return null
    }

    override final fun visitClass(klass: KtClass, context: Context): Void? {
        visitClass(context, klass)
        return null
    }

    override final fun visitClassOrObject(classOrObject: KtClassOrObject, context: Context): Void? {
        visitClassOrObject(context, classOrObject)
        return null
    }

    override final fun visitSecondaryConstructor(constructor: KtSecondaryConstructor, context: Context): Void? {
        visitSecondaryConstructor(context, constructor)
        return null
    }

    override final fun visitPrimaryConstructor(constructor: KtPrimaryConstructor, context: Context): Void? {
        visitPrimaryConstructor(context, constructor)
        return null
    }

    override final fun visitNamedFunction(function: KtNamedFunction, context: Context): Void? {
        visitNamedFunction(context, function)
        return null
    }

    override final fun visitProperty(property: KtProperty, context: Context): Void? {
        visitProperty(context, property)
        return null
    }

    override final fun visitTypeAlias(typeAlias: KtTypeAlias, context: Context): Void? {
        visitTypeAlias(context, typeAlias)
        return null
    }

    override final fun visitDestructuringDeclaration(multiDeclaration: KtDestructuringDeclaration,
                                                     context: Context): Void? {
        visitDestructuringDeclaration(context, multiDeclaration)
        return null
    }

    override final fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry,
                                                          context: Context): Void? {
        visitDestructuringDeclarationEntry(context, multiDeclarationEntry)
        return null
    }

    override final fun visitKtFile(file: KtFile, context: Context): Void? {
        visitKtFile(context, file)
        return null
    }

    override final fun visitScript(script: KtScript, context: Context): Void? {
        visitScript(context, script)
        return null
    }

    override final fun visitImportDirective(importDirective: KtImportDirective, context: Context): Void? {
        visitImportDirective(context, importDirective)
        return null
    }

    override final fun visitImportList(importList: KtImportList, context: Context): Void? {
        visitImportList(context, importList)
        return null
    }

    override final fun visitClassBody(classBody: KtClassBody, context: Context): Void? {
        visitClassBody(context, classBody)
        return null
    }

    override final fun visitModifierList(list: KtModifierList, context: Context): Void? {
        visitModifierList(context, list)
        return null
    }

    override final fun visitAnnotation(annotation: KtAnnotation, context: Context): Void? {
        visitAnnotation(context, annotation)
        return null
    }

    override final fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry, context: Context): Void? {
        visitAnnotationEntry(context, annotationEntry)
        return null
    }

    override final fun visitConstructorCalleeExpression(constructorCalleeExpression: KtConstructorCalleeExpression,
                                                        context: Context): Void? {
        visitConstructorCalleeExpression(context, constructorCalleeExpression)
        return null
    }

    override final fun visitTypeParameterList(list: KtTypeParameterList, context: Context): Void? {
        visitTypeParameterList(context, list)
        return null
    }

    override final fun visitTypeParameter(parameter: KtTypeParameter, context: Context): Void? {
        visitTypeParameter(context, parameter)
        return null
    }

    override final fun visitEnumEntry(enumEntry: KtEnumEntry, context: Context): Void? {
        visitEnumEntry(context, enumEntry)
        return null
    }

    override final fun visitParameterList(list: KtParameterList, context: Context): Void? {
        visitParameterList(context, list)
        return null
    }

    override final fun visitParameter(parameter: KtParameter, context: Context): Void? {
        visitParameter(context, parameter)
        return null
    }

    override final fun visitSuperTypeList(list: KtSuperTypeList, context: Context): Void? {
        visitSuperTypeList(context, list)
        return null
    }

    override final fun visitSuperTypeListEntry(specifier: KtSuperTypeListEntry, context: Context): Void? {
        visitSuperTypeListEntry(context, specifier)
        return null
    }

    override final fun visitDelegatedSuperTypeEntry(specifier: KtDelegatedSuperTypeEntry, context: Context): Void? {
        visitDelegatedSuperTypeEntry(context, specifier)
        return null
    }

    override final fun visitSuperTypeCallEntry(call: KtSuperTypeCallEntry, context: Context): Void? {
        visitSuperTypeCallEntry(context, call)
        return null
    }

    override final fun visitSuperTypeEntry(specifier: KtSuperTypeEntry, context: Context): Void? {
        visitSuperTypeEntry(context, specifier)
        return null
    }

    override final fun visitConstructorDelegationCall(call: KtConstructorDelegationCall, context: Context): Void? {
        visitConstructorDelegationCall(context, call)
        return null
    }

    override final fun visitPropertyDelegate(delegate: KtPropertyDelegate, context: Context): Void? {
        visitPropertyDelegate(context, delegate)
        return null
    }

    override final fun visitTypeReference(typeReference: KtTypeReference, context: Context): Void? {
        visitTypeReference(context, typeReference)
        return null
    }

    override final fun visitValueArgumentList(list: KtValueArgumentList, context: Context): Void? {
        visitValueArgumentList(context, list)
        return null
    }

    override final fun visitArgument(argument: KtValueArgument, context: Context): Void? {
        visitArgument(context, argument)
        return null
    }

    override final fun visitExpression(expression: KtExpression, context: Context): Void? {
        visitExpression(context, expression)
        return null
    }

    override final fun visitLoopExpression(loopExpression: KtLoopExpression, context: Context): Void? {
        visitLoopExpression(context, loopExpression)
        return null
    }

    override final fun visitConstantExpression(expression: KtConstantExpression, context: Context): Void? {
        visitConstantExpression(context, expression)
        return null
    }

    override final fun visitSimpleNameExpression(expression: KtSimpleNameExpression, context: Context): Void? {
        visitSimpleNameExpression(context, expression)
        return null
    }

    override final fun visitReferenceExpression(expression: KtReferenceExpression, context: Context): Void? {
        visitReferenceExpression(context, expression)
        return null
    }

    override final fun visitLabeledExpression(expression: KtLabeledExpression, context: Context): Void? {
        visitLabeledExpression(context, expression)
        return null
    }

    override final fun visitPrefixExpression(expression: KtPrefixExpression, context: Context): Void? {
        visitPrefixExpression(context, expression)
        return null
    }

    override final fun visitPostfixExpression(expression: KtPostfixExpression, context: Context): Void? {
        visitPostfixExpression(context, expression)
        return null
    }

    override final fun visitUnaryExpression(expression: KtUnaryExpression, context: Context): Void? {
        visitUnaryExpression(context, expression)
        return null
    }

    override final fun visitBinaryExpression(expression: KtBinaryExpression, context: Context): Void? {
        visitBinaryExpression(context, expression)
        return null
    }

    override final fun visitReturnExpression(expression: KtReturnExpression, context: Context): Void? {
        visitReturnExpression(context, expression)
        return null
    }

    override final fun visitExpressionWithLabel(expression: KtExpressionWithLabel, context: Context): Void? {
        visitExpressionWithLabel(context, expression)
        return null
    }

    override final fun visitThrowExpression(expression: KtThrowExpression, context: Context): Void? {
        visitThrowExpression(context, expression)
        return null
    }

    override final fun visitBreakExpression(expression: KtBreakExpression, context: Context): Void? {
        visitBreakExpression(context, expression)
        return null
    }

    override final fun visitContinueExpression(expression: KtContinueExpression, context: Context): Void? {
        visitContinueExpression(context, expression)
        return null
    }

    override final fun visitIfExpression(expression: KtIfExpression, context: Context): Void? {
        visitIfExpression(context, expression)
        return null
    }

    override final fun visitWhenExpression(expression: KtWhenExpression, context: Context): Void? {
        visitWhenExpression(context, expression)
        return null
    }

    override final fun visitTryExpression(expression: KtTryExpression, context: Context): Void? {
        visitTryExpression(context, expression)
        return null
    }

    override final fun visitForExpression(expression: KtForExpression, context: Context): Void? {
        visitForExpression(context, expression)
        return null
    }

    override final fun visitWhileExpression(expression: KtWhileExpression, context: Context): Void? {
        visitWhileExpression(context, expression)
        return null
    }

    override final fun visitDoWhileExpression(expression: KtDoWhileExpression, context: Context): Void? {
        visitDoWhileExpression(context, expression)
        return null
    }

    override final fun visitLambdaExpression(expression: KtLambdaExpression, context: Context): Void? {
        visitLambdaExpression(context, expression)
        return null
    }

    override final fun visitAnnotatedExpression(expression: KtAnnotatedExpression, context: Context): Void? {
        visitAnnotatedExpression(context, expression)
        return null
    }

    override final fun visitCallExpression(expression: KtCallExpression, context: Context): Void? {
        visitCallExpression(context, expression)
        return null
    }

    override final fun visitArrayAccessExpression(expression: KtArrayAccessExpression, context: Context): Void? {
        visitArrayAccessExpression(context, expression)
        return null
    }

    override final fun visitQualifiedExpression(expression: KtQualifiedExpression, context: Context): Void? {
        visitQualifiedExpression(context, expression)
        return null
    }

    override final fun visitDoubleColonExpression(expression: KtDoubleColonExpression, context: Context): Void? {
        visitDoubleColonExpression(context, expression)
        return null
    }

    override final fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression,
                                                        context: Context): Void? {
        visitCallableReferenceExpression(context, expression)
        return null
    }

    override final fun visitClassLiteralExpression(expression: KtClassLiteralExpression, context: Context): Void? {
        visitClassLiteralExpression(context, expression)
        return null
    }

    override final fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, context: Context): Void? {
        visitDotQualifiedExpression(context, expression)
        return null
    }

    override final fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression, context: Context): Void? {
        visitSafeQualifiedExpression(context, expression)
        return null
    }

    override final fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression, context: Context): Void? {
        visitObjectLiteralExpression(context, expression)
        return null
    }

    override final fun visitBlockExpression(expression: KtBlockExpression, context: Context): Void? {
        visitBlockExpression(context, expression)
        return null
    }

    override final fun visitCatchSection(catchClause: KtCatchClause, context: Context): Void? {
        visitCatchSection(context, catchClause)
        return null
    }

    override final fun visitFinallySection(finallySection: KtFinallySection, context: Context): Void? {
        visitFinallySection(context, finallySection)
        return null
    }

    override final fun visitTypeArgumentList(typeArgumentList: KtTypeArgumentList, context: Context): Void? {
        visitTypeArgumentList(context, typeArgumentList)
        return null
    }

    override final fun visitThisExpression(expression: KtThisExpression, context: Context): Void? {
        visitThisExpression(context, expression)
        return null
    }

    override final fun visitSuperExpression(expression: KtSuperExpression, context: Context): Void? {
        visitSuperExpression(context, expression)
        return null
    }

    override final fun visitParenthesizedExpression(expression: KtParenthesizedExpression, context: Context): Void? {
        visitParenthesizedExpression(context, expression)
        return null
    }

    override final fun visitInitializerList(list: KtInitializerList, context: Context): Void? {
        visitInitializerList(context, list)
        return null
    }

    override final fun visitAnonymousInitializer(initializer: KtAnonymousInitializer, context: Context): Void? {
        visitAnonymousInitializer(context, initializer)
        return null
    }

    override final fun visitPropertyAccessor(accessor: KtPropertyAccessor, context: Context): Void? {
        visitPropertyAccessor(context, accessor)
        return null
    }

    override final fun visitTypeConstraintList(list: KtTypeConstraintList, context: Context): Void? {
        visitTypeConstraintList(context, list)
        return null
    }

    override final fun visitTypeConstraint(constraint: KtTypeConstraint, context: Context): Void? {
        visitTypeConstraint(context, constraint)
        return null
    }

    override final fun visitUserType(type: KtUserType, context: Context): Void? {
        visitUserType(context, type)
        return null
    }

    override final fun visitDynamicType(type: KtDynamicType, context: Context): Void? {
        visitDynamicType(context, type)
        return null
    }

    override final fun visitFunctionType(type: KtFunctionType, context: Context): Void? {
        visitFunctionType(context, type)
        return null
    }

    override final fun visitSelfType(type: KtSelfType, context: Context): Void? {
        visitSelfType(context, type)
        return null
    }

    override final fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS,
                                                        context: Context): Void? {
        visitBinaryWithTypeRHSExpression(context, expression)
        return null
    }

    override final fun visitStringTemplateExpression(expression: KtStringTemplateExpression, context: Context): Void? {
        visitStringTemplateExpression(context, expression)
        return null
    }

    override final fun visitNamedDeclaration(declaration: KtNamedDeclaration, context: Context): Void? {
        visitNamedDeclaration(context, declaration)
        return null
    }

    override final fun visitNullableType(nullableType: KtNullableType, context: Context): Void? {
        visitNullableType(context, nullableType)
        return null
    }

    override final fun visitTypeProjection(typeProjection: KtTypeProjection, context: Context): Void? {
        visitTypeProjection(context, typeProjection)
        return null
    }

    override final fun visitWhenEntry(jetWhenEntry: KtWhenEntry, context: Context): Void? {
        visitWhenEntry(context, jetWhenEntry)
        return null
    }

    override final fun visitIsExpression(expression: KtIsExpression, context: Context): Void? {
        visitIsExpression(context, expression)
        return null
    }

    override final fun visitWhenConditionIsPattern(condition: KtWhenConditionIsPattern, context: Context): Void? {
        visitWhenConditionIsPattern(context, condition)
        return null
    }

    override final fun visitWhenConditionInRange(condition: KtWhenConditionInRange, context: Context): Void? {
        visitWhenConditionInRange(context, condition)
        return null
    }

    override final fun visitWhenConditionWithExpression(condition: KtWhenConditionWithExpression,
                                                        context: Context): Void? {
        visitWhenConditionWithExpression(context, condition)
        return null
    }

    override final fun visitObjectDeclaration(declaration: KtObjectDeclaration, context: Context): Void? {
        visitObjectDeclaration(context, declaration)
        return null
    }

    override final fun visitStringTemplateEntry(entry: KtStringTemplateEntry, context: Context): Void? {
        visitStringTemplateEntry(context, entry)
        return null
    }

    override final fun visitStringTemplateEntryWithExpression(entry: KtStringTemplateEntryWithExpression,
                                                              context: Context): Void? {
        visitStringTemplateEntryWithExpression(context, entry)
        return null
    }

    override final fun visitBlockStringTemplateEntry(entry: KtBlockStringTemplateEntry, context: Context): Void? {
        visitBlockStringTemplateEntry(context, entry)
        return null
    }

    override final fun visitSimpleNameStringTemplateEntry(entry: KtSimpleNameStringTemplateEntry,
                                                          context: Context): Void? {
        visitSimpleNameStringTemplateEntry(context, entry)
        return null
    }

    override final fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry, context: Context): Void? {
        visitLiteralStringTemplateEntry(context, entry)
        return null
    }

    override final fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry, context: Context): Void? {
        visitEscapeStringTemplateEntry(context, entry)
        return null
    }

    override final fun visitPackageDirective(directive: KtPackageDirective, context: Context): Void? {
        visitPackageDirective(context, directive)
        return null
    }

    override final fun visitScriptInitializer(initializer: KtScriptInitializer, context: Context): Void? {
        visitScriptInitializer(context, initializer)
        return null
    }

    override final fun visitClassInitializer(initializer: KtClassInitializer, context: Context): Void? {
        visitClassInitializer(context, initializer)
        return null
    }

    override final fun visitCollectionLiteralExpression(expression: KtCollectionLiteralExpression,
                                                        context: Context): Void? {
        visitCollectionLiteralExpression(context, expression)
        return null
    }

    override final fun visitAnnotationUseSiteTarget(annotationTarget: KtAnnotationUseSiteTarget,
                                                    context: Context): Void? {
        visitAnnotationUseSiteTarget(context, annotationTarget)
        return null
    }

    override final fun visitFileAnnotationList(fileAnnotationList: KtFileAnnotationList, context: Context): Void? {
        visitFileAnnotationList(context, fileAnnotationList)
        return null
    }

    // Prevent override

    override final fun visitFile(file: PsiFile?) {
        super.visitFile(file)
    }

    override final fun visitErrorElement(element: PsiErrorElement?) {
        super.visitErrorElement(element)
    }

    override final fun visitComment(comment: PsiComment?) {
        super.visitComment(comment)
    }

    override final fun visitPlainTextFile(file: PsiPlainTextFile?) {
        super.visitPlainTextFile(file)
    }

    override final fun visitElement(element: PsiElement?) {
        super.visitElement(element)
    }

    override final fun visitOuterLanguageElement(element: OuterLanguageElement?) {
        super.visitOuterLanguageElement(element)
    }

    override final fun visitWhiteSpace(space: PsiWhiteSpace?) {
        super.visitWhiteSpace(space)
    }

    override final fun visitDirectory(dir: PsiDirectory?) {
        super.visitDirectory(dir)
    }

    override final fun visitBinaryFile(file: PsiBinaryFile?) {
        super.visitBinaryFile(file)
    }

    override final fun visitPlainText(content: PsiPlainText?) {
        super.visitPlainText(content)
    }
}