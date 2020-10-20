---
title: commaSeparatedPattern -
---
//[detekt-api](../index.md)/[io.gitlab.arturbosch.detekt.api](index.md)/[commaSeparatedPattern](comma-separated-pattern.md)



# commaSeparatedPattern  
[jvm]  
Content  
fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[commaSeparatedPattern](comma-separated-pattern.md)(vararg delimiters: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = arrayOf(",")): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  
More info  


Splits given String into a sequence of strings splited by the provided delimiters ("," by default).



It also trims the strings and removes the empty ones

  



