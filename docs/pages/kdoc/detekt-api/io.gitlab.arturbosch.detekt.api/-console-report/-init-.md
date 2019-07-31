---
title: ConsoleReport.<init> - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConsoleReport](index.html) / [&lt;init&gt;](./-init-.html)

# &lt;init&gt;

`ConsoleReport()`

Extension point which describes how findings should be printed on the console.

Additional [ConsoleReport](index.html)'s can be made available through the [java.util.ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) pattern.
If the default reporting mechanism should be turned off, exclude the entry 'FindingsReport'
in the 'console-reports' property of a detekt yaml config.

