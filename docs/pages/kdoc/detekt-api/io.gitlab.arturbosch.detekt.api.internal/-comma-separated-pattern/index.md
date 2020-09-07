---
title: CommaSeparatedPattern -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[CommaSeparatedPattern](index.md)



# CommaSeparatedPattern  
 [jvm] class [CommaSeparatedPattern](index.md)(**text**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **delimiters**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [SplitPattern](../../io.gitlab.arturbosch.detekt.api/-split-pattern/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [any](../../io.gitlab.arturbosch.detekt.api/-split-pattern/any.md)| [jvm]  <br>Brief description  <br><br><br>Is there any element which matches the given value?<br><br>  <br>Content  <br>override fun [any](../../io.gitlab.arturbosch.detekt.api/-split-pattern/any.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [contains](../../io.gitlab.arturbosch.detekt.api/-split-pattern/contains.md)| [jvm]  <br>Brief description  <br><br><br>Does any part contain given value?<br><br>  <br>Content  <br>override fun [contains](../../io.gitlab.arturbosch.detekt.api/-split-pattern/contains.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [mapAll](../../io.gitlab.arturbosch.detekt.api/-split-pattern/map-all.md)| [jvm]  <br>Brief description  <br><br><br>Transforms all parts by given transform function.<br><br>  <br>Content  <br>override fun <T> [mapAll](../../io.gitlab.arturbosch.detekt.api/-split-pattern/map-all.md)(transform: ([String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) -> T): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<T>  <br><br><br>
| mapIf| [jvm]  <br>Content  <br>override fun <T> [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<T>.mapIf(condition: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), then: ([Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<T>) -> [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<T>): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<T>  <br><br><br>
| [mapToRegex](map-to-regex.md)| [jvm]  <br>Content  <br>fun [mapToRegex](map-to-regex.md)(): [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)>  <br><br><br>
| [matches](../../io.gitlab.arturbosch.detekt.api/-split-pattern/matches.md)| [jvm]  <br>Brief description  <br><br><br>Finds all parts which match the given value.<br><br>  <br>Content  <br>override fun [matches](../../io.gitlab.arturbosch.detekt.api/-split-pattern/matches.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  <br><br><br>
| [none](../../io.gitlab.arturbosch.detekt.api/-split-pattern/none.md)| [jvm]  <br>Brief description  <br><br><br>Tests if none of the parts contain the given value.<br><br>  <br>Content  <br>override fun [none](../../io.gitlab.arturbosch.detekt.api/-split-pattern/none.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [startWith](../../io.gitlab.arturbosch.detekt.api/-split-pattern/start-with.md)| [jvm]  <br>Brief description  <br><br><br>Tests if any part starts with the given value<br><br>  <br>Content  <br>override fun [startWith](../../io.gitlab.arturbosch.detekt.api/-split-pattern/start-with.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [excludes](index.md#io.gitlab.arturbosch.detekt.api.internal/CommaSeparatedPattern/excludes/#/PointingToDeclaration/)|  [jvm] override val [excludes](index.md#io.gitlab.arturbosch.detekt.api.internal/CommaSeparatedPattern/excludes/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>   <br>

