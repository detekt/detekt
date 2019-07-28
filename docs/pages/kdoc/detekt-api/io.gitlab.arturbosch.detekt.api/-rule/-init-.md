---
title: Rule.<init> - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](index.html) / [&lt;init&gt;](./-init-.html)

# &lt;init&gt;

`Rule(ruleSetConfig: `[`Config`](../-config/index.html)` = Config.empty, ruleContext: `[`Context`](../-context/index.html)` = DefaultContext())`

A rule defines how one specific code structure should look like. If code is found
which does not meet this structure, it is considered as harmful regarding maintainability
or readability.

A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
function. If calculations must be done before or after the visiting process, here are
two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.

