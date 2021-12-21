# Style guide for rule descriptions

Detekt aims to support developers by providing an easily readable and concise overview of identified code smells.
To contribute to this goal, follow this style guide when authoring rule descriptions.
By doing so, you ensure that whenever the respective rule is triggered, developers are presented
with a recognizable representation of all relevant pieces of information.

## a) Target audience

Think about the audience that you address with a rule description.

In the vast majority of cases, this audience is a developer who just applied Detekt to a codebase and
is now presented with a list of rules that this codebase violates. Therefore, focus on the specific issue
that Detekt identified in the code rather than what the rule itself did to come to come to its conclusion.

**Guideline 1**: Rule descriptions shall be focused on the identified issue rather than what the rule itself does.

- :heavy_check_mark:: ``Duplicated case statements in `when` expression.``
- :x:: `Detects trailing spaces.`

## b) Components to incorporate

There are three categories of information that might be incorporated into a rule description:

1. **Violation**: What exactly is it that Detekt identified in the codebase
   and, under consideration of the given Detekt configuration, recognizes as a violation of the considered rule?
   This is an essential piece of information that should be part of every rule description. Formulate it in a concise
   manner and in such a way that experienced Kotlin developers will need no further explanation to understand what
   needs to be done in order to eliminate the issue.
2. **Rationale**: Why is the identified violation considered an issue? Especially for less
   experienced Kotlin developers, this might not be clear from just the violation itself. If this is the case, it
   might make sense to add a rationale for the rule to its description.
3. **Recommendation**: What are devlopers usually expected to do in order to eliminate the identified violation? In some
   cases, this might be entirely obvious. If it is not, however, it might make sense to add a suitable recommendation (or
   multiple alternative recommendations) to the rule description. Even experienced developers might be thankful for
   a quick reminder.

**Guideline 2**: Every rule description shall begin with a concise description of the identified violation. If it makes sense
in the specific case, a rationale and/or a recommendation can be added (in this order).

- :heavy_check_mark: *(just a violation)*: `Public library class.`
- :x: *(just a recommendation)*: `Library class should not be public.`
- :heavy_check_mark: *(violation and recommendation)*: ``Public library class. Consider reducing its visibility to the module or the file.``
- :x: *(just a recommendation)*: ``Remove the `is` prefix from the name of non-boolean property.``
- :heavy_check_mark: *(violation and rationale)*: ``Non-boolean property has an `is` prefix. This might mislead clients of the library.``
- :x: *(just a rationale)*: `A function that only returns a constant is misleading.`
- :heavy_check_mark: *(violation and rationale)*: `Function returns a constant. This is misleading.`
- :heavy_check_mark: *(all categories)*: `Function returns a constant, which is misleading. Consider a constant property instead.`

**Guideline 3**: Rationales or recommendations should not be introduced if they do not create added value:

- :x: *(stating the obvious)*: `Unnecessary safe call operator used. This can be removed.`
- :heavy_check_mark: *(probably still sufficient)*: `Unnecessary safe call operator used.`

## c) Formatting and markup
**Guideline 4**: Descriptions shall be formulated as a sequence of (possibly incomplete) sentences.
Capitalize the beginning of each sentence and use proper punctuation, especially by terminating each
sentence with a period. If it makes sense, information from different categories (violation,
rationale, recommendation) can be merged into one sentence.

- :x: *(not capitalized)*: `public library class`
- :x: *(without period)*: `Public library class`
- :heavy_check_mark: *(capitalized and with period)*: `Public library class.`
- :x: *(final period missing)*: `Function returns a constant. This is misleading`
- :heavy_check_mark: *(with final period)*: `Function returns a constant. This is misleading.`

**Guideline 5**: Inline code (including syntax elements, entity names, ...) shall be surrounded using `` ` `` characters.
- :x: *(code markup missing)*: `map.get() used with non-null assertion operator (!!).`
- :heavy_check_mark: *(with code markup)*: `` `map.get()` used with non-null assertion operator `!!`.``
- :heavy_check_mark: *(with code markup)*: `` `equals()` has wrong parameter type. Override it using `Any?`.``

*Note that `non-null` is a borderline case. While `null` itself can be seen as code, the prefix turns it into a conventional word.*

**Guideline 6**: Sentences in rule descriptions shall be separated using a single whitespace character. Most importantly, rule descriptions
must not contain any newline characters.

- :x: *(newline)*: `Function returns a constant.\nThis is misleading.`
- :heavy_check_mark: *(one space)*: `Function returns a constant. This is misleading.`
