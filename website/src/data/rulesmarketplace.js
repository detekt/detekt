/*
 * ADD YOUR RULES TO THE DETEKT RULES MARKETPLACE
 *
 * Instructions for adding your ruleset:
 * - Add your third-party rule in the json array below
 * - `title` is the repository name name
 * - `description` is a short (≤120 characters) description of the ruleset
 * - `repo` is the repository URL
 * - `mavenCoordinates` are the maven coordinates of the ruleset so users can easily copy them
 * - `mavenRepo` is the maven repository where they're hosted.
 * - `ruleset` is the ID of the ruleset
 * - `rules` is an array of rules your ruleset is offering
 * - `usesTypeResolution` a boolean weather or not your ruleset uses type resolution.
 * - Open a PR and check for reported CI errors
 */

// Add sites to this list
// prettier-ignore
export const rulesets = [
  {
    title: 'Compiler',
    description: 'A ruleset that wraps the warnings and info messages of the Kotlin compiler as detekt findings..',
    repo: 'https://github.com/BraisGabin/detekt-compiler-rules',
    mavenCoordinates: 'com.github.BraisGabin:detekt-compiler-rules:+',
    mavenRepo: 'Jitpack',
    ruleset: 'compiler',
    rules: ['CompilerInfo', 'CompilerWarning'],  
    usesTypeResolution: true,
  },
    {
    title: 'Compose',
    description: 'A set of Detekt rules to help prevent common errors in projects using Jetpack Compose.',
    repo: 'https://github.com/appKODE/detekt-rules-compose',
    mavenCoordinates: 'ru.kode:detekt-rules-compose:+',
    mavenRepo: 'MavenCentral',
    ruleset: 'compose',
    rules: ['ReusedModifierInstance', 'UnnecessaryEventHandlerParameter', 'ComposableEventParameterNaming', 'ModifierHeightWithText', 'ModifierParameterPosition', 'ModifierDefaultValue', 'MissingModifierDefaultValue', 'PublicComposablePreview'],  
    usesTypeResolution: false,
  },
  {
    title: 'Doist',
    description: 'This repository contains custom detekt rules based on Doist internal coding conventions.',
    repo: 'https://github.com/Doist/detekt-rules',
    mavenCoordinates: 'com.doist.detekt:detekt-rules:+',
    mavenRepo: 'GithubPackages',
    ruleset: 'DoistRuleSet',
    rules: ['NoBlankNewLineAfterClassHeader', 'ConsistentWhenEntries', 'SingleLineWhenEntryExpressionsAreWrapped', 'MutableObservablePropertyIsPrivate', 'NoNotNullOperator', 'TodoPattern'],  
    usesTypeResolution: false,
  },
  {
    title: 'Operator',
    description: 'Rules to prefer expressions over named functions for kotlin operators.',
    repo: 'https://github.com/colematthew4/detekt-operator',
    mavenCoordinates: 'io.cole.matthew.detekt.operator:detekt-operator:+',
    mavenRepo: 'GithubPackages',
    ruleset: 'detekt-operator',
    rules: ['PreferInOverContainsSyntax', 'PreferUnaryPrefixOverFunctionSyntax', 'PreferUnaryPostfixOverFunctionSyntax', 'PreferArithmeticSymbolSyntax', 'PreferBracketAccessorOverFunctionSyntax'], 
    usesTypeResolution: false,
  },
  {
    title: 'Verify Implementation',
    description: 'A ruleset which enables verifying whether concrete classes are implemented as specified according to annotations applied to base types.',
    repo: 'https://github.com/cph-cachet/detekt-verify-implementation',
    mavenCoordinates: 'dk.cachet.detekt.extensions:detekt-verify-implementation:+',
    mavenRepo: 'GithubPackages',
    ruleset: 'verify-implementation',
    rules: ['Immutable', 'DataClass'],  
    usesTypeResolution: true,
  },
  /*
  Pro Tip: add your ruleset in alphabetical order.
  Appending your ruleset here (at the end) is more likely to produce Git conflicts.
   */
];
