# Style guide for authoring rule descriptions

A rule incorporated into the Detekt framework is generally accompanied by three
types of descriptions:
1. **Documentation string**: Using conventional
   Javadoc/KDoc syntax, the documentation string is associated with the `Rule`
   subclass implementing the considered rule. Documentation strings associated
   with built-in rules are automatically pulled from the Detekt codebase and
   used to generate the rule set overview available on the
   [Detekt website](https://detekt.github.io/detekt).
2. **Issue description**: The issue description gives a summary of the code
   smells that the respective rule is supposed to detect. From an implementation
   point of view, it is the string that `Rule` subclasses pass to the
   `description` parameter of the [`Issue` class][1]. In generated reports, the
   issue description is often used to introduce a list of code smells that the
   respective rule has identified.
3. **Code smell messages**: A code smell message is issued for every code
   smell (or *violation*) identified within the codebase. Implementation-wise,
   such a message is dynamically created during the construction of a
   [`CodeSmell` instance][2]. In generated reports, these messages are listed
   underneath the issue description. Built-in console reports such as
   `LiteFindingsReport` use these messages to display identified code smells to
   the user.

When authoring these descriptions, rule developers should keep two different
target audiences in mind:
1. The **documentation string** is generally read by individuals who want to
   learn about the *rule itself*. They might need to compose a Detekt
   configuration for their codebase, need to understand what the rule is
   currently checking for in order to extend it, or are just interested in the
   available Detekt rule sets.
2. The **issue description** as well as **code smell messages** are presented to
   developers whose codebase violates one or more rules that Detekt checked for.
   This audience is generally less interested in the rule itself. Instead,
   individuals reading these texts will usually expect specific references to
   *their codebase* and what can be done in order to improve it.

## Guidelines for documentation strings

When authoring the documentation string for a rule, adhere to the following
guidelines:
1. The documentation string shall be formulated in such a manner that it refers
   to the rule itself:
   - :heavy_check_mark:: `Detects trailing spaces and recommends to remove them.`
   - :x:: ``Trailing spaces detected. Consider removing them.``
2. Stick to the [KDoc convention][3] that the first paragraph of documentation
   text is the summary of the documented element (the rule), while the following
   text is a detailed description. More specifically, populate these two regions
   as follows:
   - In the **summary**, give a concise description of the violation(s) that the
   rule identifies. If applicable, a very brief summary of why the violation is
   considered bad practice or what is usually done to eliminate it can be
   given (but does not have to be given).
   - In the **detailed description**, which does not have to be added for
   simple/obvious rules, explain the violation(s) that the rule identifies in
   greater detail. Add a rationale (ideally with a reference) outlining why such
   a violation should be avoided. If applicable, add further remarks that make
   it easier to understand or configure the rule.
3. Formulate all descriptions using (potentially incomplete) sentences with
   proper capitalization and punctuation:
   - :heavy_check_mark:: `Ensures that files with a single non-private class are named accordingly.`
   - :x:: `ensures that files with a single non-private class are named accordingly`
4. Surround inline code (or code symbols) with `` ` `` characters (as in
   [Markdown][4]):
   - :heavy_check_mark:: ``Reports referential equality checks for types such as `String` and `List`.``
   - :x:: `Reports referential equality checks for types such as String and List.`

## Guidelines for issue descriptions and code smell messages

Adhere to the following guidelines when authoring an issue description or code
smell messages:
1. Use a wording that focuses on the violation rather than on the rule itself.
   Therefore, assume that the analyzed codebase contains at least one violation
   of the respective rule.
   - :heavy_check_mark:: ``Duplicated case statements in `when` expression.``
   - :x:: `Detects trailing spaces.`
2. Formulate the text as a sequence of (potentially incomplete) sentences with
   proper capitalization and punctuation:
   - :heavy_check_mark:: ``Duplicated case statements in `when` expression.``
   - :x:: ``duplicated case statements in `when` expression``
3. Separate sentences from the sequence with one space. Most importantly, do not
   use line breaks:
   - :heavy_check_mark:: `Non-boolean property with prefix suggesting boolean type. This might mislead clients of the API.`
   - :x:: `Non-boolean property with prefix suggesting boolean type.\nThis might mislead clients of the API.`
4. Surround inline code (or code symbols) with `` ` `` characters (as in
   [Markdown][4]):
   - :heavy_check_mark:: `` `map.get()` used with non-null assertion operator `!!`.``
   - :x:: `map.get() used with non-null assertion operator (!!).`

*Note*: `non-null` is a borderline case. While `null` itself can be seen as
code, the prefix turns it into a conventional word.

The above-mentioned guidelines tell you *how* to formulate issue descriptions
and code smell messages. To learn *what to include* in each of the texts,
refer to the following subsections.

### Components of issue descriptions
An issue description should consist of the following components (in this order):
1. **Violation** (*required*): What type of violation is it that Detekt
   identified in the codebase and, under consideration of the given Detekt
   configuration, recognizes as a violation of the considered rule?
2. **Rationale** (*optional*): Why is the identified violation considered an
   issue? Especially for less experienced Kotlin developers, this might not be
   clear from just the violation itself.
3. **Recommendation** (*optional*): What are developers usually expected to do
   in order to eliminate the identified violation? In some cases, this might be
   entirely obvious. If it is not, however, it makes sense to add a suitable
   recommendation (or multiple alternative recommendations).

:warning: In any case, try to keep the description as brief and concise as
possible!

The following list contains some positive and negative examples:
- :heavy_check_mark: (*just a violation*): `Public library class.`
- :x: (*just a recommendation*): `Library class should not be public.`
- :heavy_check_mark: (*violation and recommendation*): ``Public library class. Reduce its visibility to the module or the file.``
- :x: (*just a rationale*): `A function that only returns a constant is misleading.`
- :heavy_check_mark: (*violation and rationale*): `Function returns a constant. This is misleading.`
- :heavy_check_mark: (*all categories*): `Function returns a constant, which is misleading. Use a constant property instead.`

### Components of code smell messages
A code smell message should be dynamically created to describe a
*specific violation*. It should be regarded as an extension of the more generic
violation part of the issue description. More specifically, it should explicitly
reference identifiers or similar from the codebase. If it makes
sense in the specific context, it might be worthwhile to add a more detailed
version of the recommendation as well. If this is the case, add this extension
after the more specific description of the violation.

:warning: Try to keep code smell messages even shorter than issue descriptions!

The following list contains compliant and non-compliant examples of code smell
messages:
- :x: (*probably not more specific than the issue message*): ``Non-boolean property suggests a boolean type.``
- :heavy_check_mark: (*specific violation*): ``Non-boolean property `hasXyz` suggests a boolean type.``
- :x: (*pointless recommendation*): ``Non-boolean property `hasXyz` suggests a boolean type. Remove the prefix.``
- :heavy_check_mark: (*with specific recommendation*): ``Non-boolean property `hasXyz` suggests a boolean type. Remove the `has` prefix.``
- :heavy_check_mark: (*with usage-aware recommendation*): ``Magic number `4` passed to `abc`. Pass it as a named argument.``

*Note*: The recommendation given in the last example is usage-aware since
it is limited to cases in which a Kotlin function is called. Named
arguments [cannot be used when calling Java functions][5]. Since this
specific recommendation cannot be given for all encountered magic numbers,
it is not possible to incorporate it into the generic issue description.

[1]: https://github.com/detekt/detekt/blob/v1.19.0/detekt-api/src/main/kotlin/io/gitlab/arturbosch/detekt/api/Issue.kt
[2]: https://github.com/detekt/detekt/blob/v1.19.0/detekt-api/src/main/kotlin/io/gitlab/arturbosch/detekt/api/CodeSmell.kt
[3]: https://kotlinlang.org/docs/kotlin-doc.html
[4]: https://daringfireball.net/projects/markdown/syntax
[5]: https://kotlinlang.org/docs/functions.html#named-arguments
