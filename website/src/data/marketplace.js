/*
 * ADD YOUR REPOSITORY TO THE DETEKT MARKETPLACE
 *
 * We're allowing third-party repositories that contain rulesets,
 * custom processors, custom reporters and external plugins that work with detekt.
 *
 * You can read more about how to extend detekt here:
 * https://detekt.dev/docs/introduction/extensions
 *
 * Instructions for adding your repository:
 *
 * - Add your third-party repository in the json array below
 * - `title` is the repository name.
 * - `description` is a short (≤120 characters) description of the repository.
 * - `repo` is the repository URL.
 * - `ruleset` (Optional) is the ID of the ruleset.
 * - `rules` (Optional) is an array of rules your ruleset is offering.
 * - `usesTypeResolution` (Optional) a boolean whether or not your ruleset uses type resolution.
 * - Open a PR and check for reported CI errors
 */

export const tagTypes = {
  ruleset: {
    color: "#39ca30",
    description: "A collection of custom rules for detekt",
    communityUrls: [
      "https://github.com/topics/detekt-rules",
      "https://github.com/search?q=%22io.gitlab.arturbosch.detekt.api.RuleSetProvider%22+-org%3Adetekt&type=code",
    ],
  },
  processor: {
    color: "#e9669e",
    description: "A custom processor for detekt",
    communityUrls: [
      "https://github.com/search?q=%22io.gitlab.arturbosch.detekt.api.FileProcessListener%22+-org%3Adetekt&type=code",
    ],
  },
  reporter: {
    color: "#fe6829",
    description: "A custom reporter for detekt",
    communityUrls: [
      "https://github.com/search?q=%22io.gitlab.arturbosch.detekt.api.OutputReport%22+-org%3Adetekt&type=code",
      "https://github.com/search?q=%22io.gitlab.arturbosch.detekt.api.ConsoleReport%22+-org%3Adetekt&type=code",
    ],
  },
  configvalidator: {
    color: "#53dbb7",
    description: "A custom config validator for detekt",
    communityUrls: [
      "https://github.com/search?q=%22io.gitlab.arturbosch.detekt.api.ConfigValidator%22+-org%3Adetekt&type=code",
    ],
  },
  plugin: {
    color: "#a44fb7",
    description: "A plugin or a tool built on top of detekt",
    communityUrls: [
      "https://github.com/topics/detekt-plugin"
    ],
  },
};

// Add sites to this list
export const extensions = [
  {
    title: "Faire's Detekt Rules",
    description: "A collection of opinionated rules aiming to provide project code consistency, improve readability and prevent performance issues.",
    repo: "https://github.com/Faire/faire-detekt-rules",
    ruleset: "FaireRuleSet",
    rules: [
      "AlwaysUseIsTrueOrIsFalse",
      "DoNotAccessVisibleForTesting",
      "DoNotSplitByRegex",
      "DoNotUseDirectReceiverReferenceInsideWith",
      "DoNotUseHasSizeForEmptyListInAssert",
      "DoNotUseIsEqualToWhenArgumentIsOne",
      "DoNotUseIsEqualToWhenArgumentIsZero",
      "DoNotUsePropertyAccessInAssert",
      "DoNotUseSingleOnFilter",
      "DoNotUseSizePropertyInAssert",
      "GetOrDefaultShouldBeReplacedWithGetOrElse",
      "NoNonPrivateGlobalVariables",
      "NoNullableLambdaWithDefaultNull",
      "NoPairWithAmbiguousTypes",
      "PreferIgnoreCase",
      "PreventBannedImports",
      "ReturnValueOfLetMustBeUsed",
      "UseEntriesInsteadOfValuesOnEnum",
      "UseFirstOrNullInsteadOfFind",
      "UseMapNotNullInsteadOfFilterNotNull",
      "UseOfCollectionInsteadOfEmptyCollection",
      "UseSetInsteadOfListToSet",
    ],
    usesTypeResolution: true,
    tags: ["ruleset"],
  },
  {
    title: "Hbmartin's Ruleset",
    description:
      "A somewhat opinionated ruleset for Detekt, primarily intended to avoid crashes and bugs related to mutability.",
    repo: "https://github.com/hbmartin/hbmartin-detekt-rules",
    ruleset: "HbmartinRuleSet",
    rules: [
      "AvoidFirstOrLastOnList",
      "AvoidMutableCollections",
      "AvoidVarsExceptWithDelegate",
      "DontForceCast",
      "MutableTypeShouldBePrivate",
      "NoNotNullOperator",
      "NoVarsInConstructor",
      "WhenBranchSingleLineOrBraces",
    ],
    usesTypeResolution: true,
    tags: ["ruleset"],
  },
  {
    title: "Compiler",
    description:
      "A ruleset that wraps the warnings and info messages of the Kotlin compiler as detekt findings..",
    repo: "https://github.com/BraisGabin/detekt-compiler-rules",
    ruleset: "compiler",
    rules: [
      "CompilerInfo",
      "CompilerWarning",
    ],
    usesTypeResolution: true,
    tags: ["ruleset"],
  },
  {
    title: "Compose by appKODE",
    description:
      "A set of detekt rules to help prevent common errors in projects using Jetpack Compose.",
    repo: "https://github.com/appKODE/detekt-rules-compose",
    ruleset: "compose",
    rules: [
      "ComposableEventParameterNaming",
      "ComposableParametersOrdering",
      "ComposeFunctionName",
      "MissingModifierDefaultValue",
      "ModifierDefaultValue",
      "ModifierHeightWithText",
      "ModifierParameterPosition",
      "PublicComposablePreview",
      "ReusedModifierInstance",
      "TopLevelComposableFunctions",
      "UnnecessaryEventHandlerParameter",
    ],
    usesTypeResolution: false,
    tags: ["ruleset"],
  },
  {
    title: "Jetpack Compose Rules",
    description:
      "Static checks to aid with a healthy adoption of Jetpack Compose.",
    repo: "https://github.com/mrmans0n/compose-rules/",
    docs: "https://mrmans0n.github.io/compose-rules/",
    ruleset: "Compose",
    rules: [
      "CompositionLocalAllowlist",
      "ContentEmitterReturningValues",
      "ModifierComposable",
      "ModifierMissing",
      "ModifierReused",
      "ModifierWithoutDefault",
      "MultipleContentEmitters",
      "MutableParameters",
      "Naming",
      "ParameterOrder",
      "PreviewNaming",
      "PreviewPublic",
      "RememberMissing",
      "UnstableCollections",
      "ViewModelForwarding",
      "ViewModelInjection",
    ],
    usesTypeResolution: false,
    tags: ["ruleset"],
  },
  {
    title: "Detekt Rule Authors",
    description:
      "The rule authors ruleset provides rules that ensures good practices when writing detekt rules.",
    repo: "https://github.com/detekt/detekt",
    docs: "https://detekt.dev/docs/rules/ruleauthors",
    tags: ["ruleset"],
    ruleset: "ruleauthors",
    rules: [
      "UseEntityAtName",
      "ViolatesTypeResolutionRequirements",
    ],
    usesTypeResolution: true,
  },
  {
    title: "Doist detekt-rules",
    description:
      "This repository contains custom detekt rules based on Doist internal coding conventions.",
    repo: "https://github.com/Doist/detekt-rules",
    ruleset: "DoistRuleSet",
    rules: [
      "ConsistentWhenEntries",
      "MutableObservablePropertyIsPrivate",
      "NoBlankNewLineAfterClassHeader",
      "NoNotNullOperator",
      "SingleLineWhenEntryExpressionsAreWrapped",
      "TodoPattern",
    ],
    usesTypeResolution: false,
    tags: ["ruleset"],
  },
  {
    title: "GitLab Report",
    description:
      "A reporter to export detekt findings to GitLab Code Quality (and other code climate compatible tools).\nThis is designed for use with GitLab, but should also work fine with everything else that accepts the code climate format.",
    repo: "https://gitlab.com/cromefire/detekt-gitlab-report",
    tags: ["reporter"],
  },
  {
    title: "Hint",
    description:
      "A ruleset to implement detection of violation of programming principles. detekt-hint offers also instructions on how to integrate with Danger and GitHub Actions",
    repo: "https://github.com/mkohm/detekt-hint",
    docs: "https://mkohm.github.io/detekt-hint/",
    tags: ["plugin", "ruleset"],
    ruleset: "detekt-hint",
    rules: [
      "InterfaceSegregationPrinciple",
      "LackOfCohesionMethods",
      "OpenClosedPrinciple",
      "UseCompositionInsteadOfInheritance",
    ],
    usesTypeResolution: true,
  },
  {
    title: "UseInvokeForOperator",
    description: "This set contains rules that help to improve readability and to keep a single project-wide convention. UseInvokeForOperater rule detects cases when using invoke expression instead of a direct call with round brackets. The Invoke operator makes code more readable and the rule helps keeping it.",
    repo: "https://github.com/Kiolk/Detekt-rules",
    ruleset: "kiolk-detekt-rules",
    rules: [
      "UseInvokeForOperator",
    ],
    usesTypeResolution: true,
    tags: ["ruleset"],
   },
  {
    title: "ktlint",
    description:
      "This rule set provides wrappers for rules implemented by ktlint.",
    repo: "https://github.com/detekt/detekt",
    docs: "https://detekt.dev/docs/rules/formatting",
    tags: ["ruleset"],
    ruleset: "formatting",
    rules: [
      "AnnotationOnSeparateLine",
      "AnnotationSpacing",
      "ArgumentListWrapping",
      "BlockCommentInitialStarAlignment",
      "ChainWrapping",
      "CommentSpacing",
      "CommentWrapping",
      "DiscouragedCommentLocation",
      "EnumEntryNameCase",
      "Filename",
      "FinalNewline",
      "FunKeywordSpacing",
      "FunctionReturnTypeSpacing",
      "FunctionSignature",
      "FunctionStartOfBodySpacing",
      "FunctionTypeReferenceSpacing",
      "ImportOrdering",
      "Indentation",
      "KdocWrapping",
      "MaximumLineLength",
      "ModifierListSpacing",
      "ModifierOrdering",
      "MultiLineIfElse",
      "NoBlankLineBeforeRbrace",
      "NoBlankLinesInChainedMethodCalls",
      "NoConsecutiveBlankLines",
      "NoEmptyClassBody",
      "NoEmptyFirstLineInMethodBlock",
      "NoLineBreakAfterElse",
      "NoLineBreakBeforeAssignment",
      "NoMultipleSpaces",
      "NoSemicolons",
      "NoTrailingSpaces",
      "NoUnitReturn",
      "NoUnusedImports",
      "NoWildcardImports",
      "NullableTypeSpacing",
      "PackageName",
      "ParameterListSpacing",
      "ParameterListWrapping",
      "SpacingAroundAngleBrackets",
      "SpacingAroundColon",
      "SpacingAroundComma",
      "SpacingAroundCurly",
      "SpacingAroundDot",
      "SpacingAroundDoubleColon",
      "SpacingAroundKeyword",
      "SpacingAroundOperators",
      "SpacingAroundParens",
      "SpacingAroundRangeOperator",
      "SpacingAroundUnaryOperator",
      "SpacingBetweenDeclarationsWithAnnotations",
      "SpacingBetweenDeclarationsWithComments",
      "SpacingBetweenFunctionNameAndOpeningParenthesis",
      "StringTemplate",
      "TrailingCommaOnCallSite",
      "TrailingCommaOnDeclarationSite",
      "TypeArgumentListSpacing",
      "TypeParameterListSpacing",
      "UnnecessaryParenthesesBeforeTrailingLambda",
      "Wrapping",
    ],
    usesTypeResolution: false,
  },
  {
    title: "Kure",
    description:
      "The purpose of this ruleset for functional programming is to report the potential use of impure language elements in kotlin code.",
    repo: "https://github.com/neeffect/kure-potlin",
    tags: ["ruleset"],
    ruleset: "kure-potlin",
    rules: [
      "AbstractClassDefinition",
      "BranchStatement",
      "ClassDefinition",
      "LoopDefinition",
      "MissingElse",
      "MutableCollections",
      "ReturnStatement",
      "ReturnUnit",
      "ThrowExpression",
      "VariableDefinition",
    ],
    usesTypeResolution: true,
  },
  {
    title: "Library Authors",
    description:
      "Rules in this rule set report issues related to libraries API exposure.",
    repo: "https://github.com/detekt/detekt",
    docs: "https://detekt.dev/docs/rules/libraries",
    tags: ["ruleset"],
    ruleset: "libraries",
    rules: [
      "ForbiddenPublicDataClass",
      "LibraryCodeMustSpecifyReturnType",
      "LibraryEntitiesShouldNotBePublic",
    ],
    usesTypeResolution: true,
  },
  {
    title: "Operator",
    description:
      "Rules to prefer expressions over named functions for kotlin operators.",
    repo: "https://github.com/colematthew4/detekt-operator",
    ruleset: "detekt-operator",
    rules: [
      "PreferArithmeticSymbolSyntax",
      "PreferBracketAccessorOverFunctionSyntax",
      "PreferInOverContainsSyntax",
      "PreferUnaryPrefixOverFunctionSyntax",
      "PreferUnaryPostfixOverFunctionSyntax",
    ],
    usesTypeResolution: false,
    tags: ["ruleset"],
  },
  {
    title: "Verify Implementation",
    description:
      "A ruleset which enables verifying whether concrete classes are implemented as specified according to annotations applied to base types.",
    repo: "https://github.com/cph-cachet/detekt-verify-implementation",
    ruleset: "verify-implementation",
    rules: [
      "DataClass",
      "Immutable",
    ],
    usesTypeResolution: true,
    tags: ["ruleset"],
  },
  /*
   * Pro Tip: add your ruleset in alphabetical order.
   * Appending your ruleset here (at the end) is more likely to produce Git conflicts.
   */
];
