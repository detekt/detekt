/*
 * ADD YOUR REPOSITORY TO THE DETEKT MARKETPLACE
 *
 * We're allowing third-party repositories that contain rulesets,
 * custom processors, custom reporters and external plugins that work with Detekt.
 *
 * You can read more about how to extend Detekt here:
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
    description: "A collection of custom rules for Detekt",
  },
  processor: {
    color: "#e9669e",
    description: "A custom processor for Detekt",
  },
  reporter: {
    color: "#fe6829",
    description: "A custom reporter for Detekt",
  },
  plugin: {
    color: "#a44fb7",
    description: "A plugin or a tool built on top of Detekt",
  },
};

// Add sites to this list
export const extensions = [
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
      "A set of Detekt rules to help prevent common errors in projects using Jetpack Compose.",
    repo: "https://github.com/appKODE/detekt-rules-compose",
    ruleset: "compose",
    rules: [
      "ComposableEventParameterNaming",
      "MissingModifierDefaultValue",
      "ModifierDefaultValue",
      "ModifierHeightWithText",
      "ModifierParameterPosition",
      "PublicComposablePreview",
      "ReusedModifierInstance",
      "UnnecessaryEventHandlerParameter",
    ],
    usesTypeResolution: false,
    tags: ["ruleset"],
  },
  {
    title: "Compose by Twitter",
    description:
      "Static checks to aid with a healthy adoption of Jetpack Compose.",
    repo: "https://github.com/twitter/compose-rules/",
    ruleset: "TwitterCompose",
    rules: [
      "ComposableNaming",
      "ComposableParamOrder",
      "ContentEmitterReturningValues",
      "ModifierComposable",
      "ModifierMissing",
      "ModifierReused",
      "ModifierWithoutDefault",
      "MultipleEmitters",
      "MutableParams",
      "PreviewPublic",
      "RememberMissing",
      "ViewModelForwarding",
      "ViewModelInjection",
    ],
    usesTypeResolution: false,
    tags: ["ruleset"],
  },
  {
    title: "Detekt rules for Library Authors",
    description:
      "Rules in this rule set report issues related to libraries API exposure.",
    repo: "https://github.com/detekt/detekt",
    docs: "https://detekt.dev/docs/next/rules/libraries",
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
    title: "Detekt rules for Rule Authors",
    description:
      "The rule authors ruleset provides rules that ensures good practices when writing detekt rules.",
    repo: "https://github.com/detekt/detekt",
    docs: "https://detekt.dev/docs/next/rules/ruleauthors",
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
    title: "ktlint",
    description:
      "This rule set provides wrappers for rules implemented by ktlint.",
    repo: "https://github.com/detekt/detekt",
    docs: "https://detekt.dev/docs/next/rules/formatting",
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
    title: "Hint",
    description:
      "A ruleset to implement detection of violation of programming principles. detekt-hint offers also instructions on how to integrate with Danger and Github Actions",
    repo: "https://github.com/mkohm/detekt-hint",
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
    title: "Gitlab Report",
    description:
      "A reporter to export Detekt findings to GitLab Code Quality (and other code climate compatible tools).\nThis is designed for use with GitLab, but should also work fine with everything else that accepts the code climate format.",
    repo: "https://gitlab.com/cromefire/detekt-gitlab-report",
    tags: ["reporter"],
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
