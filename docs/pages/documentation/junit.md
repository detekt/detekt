---
title: Junit Rule Set
sidebar: home_sidebar
keywords: rules, junit
permalink: junit.html
toc: true
folder: documentation
---
Rules in this ruleset report issues related to JUnit tests

### TestWithoutAssertion

This rule report JUnit tests that do not have any assertions.
Assertions help to understand the purpose of the test.

**Severity**: CodeSmell

**Debt**: 10min

#### Configuration options:

* ``assertionPattern`` (default: ``'assert'``)

   assertion pattern
