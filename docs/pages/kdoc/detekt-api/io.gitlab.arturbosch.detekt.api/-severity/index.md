---
title: Severity - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Severity](./index.html)

# Severity

`enum class Severity`

Rules can classified into different severity grades. Maintainer can choose
a grade which is most harmful to their projects.

### Enum Values

| [CodeSmell](-code-smell.html) | Represents clean coding violations which may lead to maintainability issues. |
| [Style](-style.html) | Inspections in this category detect violations of code syntax styles. |
| [Warning](-warning.html) | Corresponds to issues that do not prevent the code from working, but may nevertheless represent coding inefficiencies. |
| [Defect](-defect.html) | Corresponds to coding mistakes which could lead to unwanted behavior. |
| [Minor](-minor.html) | Represents code quality issues which only slightly impact the code quality. |
| [Maintainability](-maintainability.html) | Issues in this category make the source code confusing and difficult to maintain. |
| [Security](-security.html) | Places in the source code that can be exploited and possibly result in significant damage. |
| [Performance](-performance.html) | Places in the source code which degrade the performance of the application. |

