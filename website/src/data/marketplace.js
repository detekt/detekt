/*
 * ADD YOUR REPOSITORY TO THE DETEKT MARKETPLACE
 *
 * We're allowing third-party repositories that contains rulesets,
 * custom processors, custom reporters and external plugins that work with Detekt.
 *
 * You can read more about how to extend Detekt here:
 * https://detekt.dev/docs/introduction/extensions
 *
 * Instructions for adding your repository:
 *
 * - Add your third-party repository in the json array below
 * - `title` is the repository name
 * - `description` is a short (≤120 characters) description of the repository.
 * - `repo` is the repository URL
 * - `ruleset` (Optional) is the ID of the ruleset
 * - `rules` (Optional) is an array of rules your ruleset is offering
 * - `usesTypeResolution` (Optional) a boolean weather or not your ruleset uses type resolution.
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
    rules: ["CompilerInfo", "CompilerWarning"],
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
      "ReusedModifierInstance",
      "UnnecessaryEventHandlerParameter",
      "ComposableEventParameterNaming",
      "ModifierHeightWithText",
      "ModifierParameterPosition",
      "ModifierDefaultValue",
      "MissingModifierDefaultValue",
      "PublicComposablePreview",
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
    title: "Doist detekt-rules",
    description:
      "This repository contains custom detekt rules based on Doist internal coding conventions.",
    repo: "https://github.com/Doist/detekt-rules",
    ruleset: "DoistRuleSet",
    rules: [
      "NoBlankNewLineAfterClassHeader",
      "ConsistentWhenEntries",
      "SingleLineWhenEntryExpressionsAreWrapped",
      "MutableObservablePropertyIsPrivate",
      "NoNotNullOperator",
      "TodoPattern",
    ],
    usesTypeResolution: false,
    tags: ["ruleset"],
  },
  {
    title: "Kure",
    description:
      "The purpose of this ruleset for functional programming is to report the potential use of impure language elements in kotlin code.",
    repo: "https://github.com/neeffect/kure-potlin",
    tags: ["ruleset"],
    ruleset: "kure-potlin",
    rules: [
      "LoopDefinition",
      "ReturnStatement",
      "VariableDefinition",
      "ReturnUnit",
      "ClassDefinition",
      "AbstractClassDefinition",
      "ThrowExpression",
      "MutableCollections",
      "BranchStatement",
      "MissingElse"
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
      "PreferInOverContainsSyntax",
      "PreferUnaryPrefixOverFunctionSyntax",
      "PreferUnaryPostfixOverFunctionSyntax",
      "PreferArithmeticSymbolSyntax",
      "PreferBracketAccessorOverFunctionSyntax",
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
    rules: ["Immutable", "DataClass"],
    usesTypeResolution: true,
    tags: ["ruleset"],
  },
  /*
  Pro Tip: add your ruleset in alphabetical order.
  Appending your ruleset here (at the end) is more likely to produce Git conflicts.
   */
];
