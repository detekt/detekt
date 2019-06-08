---
title: "Code Smell Baseline"
keywords: baseline suppressing smells
tags: 
sidebar: 
permalink: baseline.html
summary:
---

With the cli option `--baseline` or the detekt-gradle-plugin closure-property `baseline` you can specify a file which is used to generate a `baseline.xml`.
It is a file where code smells are white- or blacklisted.

The intention of a whitelist is that only new code smells are printed on further analysis. 
The blacklist can be used to write down false positive detections (instead of suppressing them and polute your code base). 

The `ID` node has the following structure: `<RuleID>:<Codesmell_Signature>`.  
When adding a custom issue to the xml file, make sure the `RuleID` should be self-explaining.
The `Codesmell_Signature` is not printed to the console but can be retrieved from the **txt** output file when using
the `--report txt:path/to/report` cli flag.

```xml
<SmellBaseline>
  <Blacklist>
    <ID>CatchRuntimeException:Junk.kt$e: RuntimeException</ID>
  </Blacklist>
  <Whitelist>
    <ID>NestedBlockDepth:Indentation.kt$Indentation$override fun procedure(node: ASTNode)</ID>
    <ID>TooManyFunctions:LargeClass.kt$io.gitlab.arturbosch.detekt.rules.complexity.LargeClass.kt</ID>
    <ID>ComplexMethod:DetektExtension.kt$DetektExtension$fun convertToArguments(): MutableList&lt;String&gt;</ID>
  </Whitelist>
</SmellBaseline>
```

If you are using the gradle-plugin run the `detektBaseline` task to generate yourself a `baseline.xml`.

Be aware that auto formatting cannot be combined with the `baseline`.
The signatures for a `;` for example would be to ambiguous.
