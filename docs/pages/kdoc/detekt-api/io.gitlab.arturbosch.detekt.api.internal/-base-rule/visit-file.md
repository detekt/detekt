---
title: visitFile -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[BaseRule](index.md)/[visitFile](visit-file.md)



# visitFile  
[jvm]  
Brief description  


Before starting visiting kotlin elements, a check is performed if this rule should be triggered. Pre- and post-visit-hooks are executed before/after the visiting process. BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that rely on symbols and types being resolved can use the BindingContext for this analysis. Note that detekt must receive the correct compile classpath for the code being analyzed otherwise the default value BindingContext.EMPTY will be used and it will not be possible for detekt to resolve types or symbols.

  
Content  
fun [visitFile](visit-file.md)(root: KtFile, bindingContext: BindingContext, compilerResources: [CompilerResources](../-compiler-resources/index.md)?)  



