---
title: pathMatcher -
---
//[detekt-api](../index.md)/[io.gitlab.arturbosch.detekt.api.internal](index.md)/[pathMatcher](path-matcher.md)



# pathMatcher  
[jvm]  
Brief description  


Converts given pattern into a [PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) specified by [FileSystem.getPathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-kotlin.String-). We only support the "glob:" syntax to stay os independently. Internally a globbing pattern is transformed to a regex respecting the Windows file system.

  
Content  
fun [pathMatcher](path-matcher.md)(pattern: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)  



