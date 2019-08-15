---
title:  "Howto: make detekt silent"
published: true
permalink: howto-silent-reports.html
summary: "This guide shows how to silence detekt and write a custom report format."
tags: [guides]
---

detekt's reporting mechanism relies on implementations of [ConsoleReport](https://arturbosch.github.io/detekt/pages/kdoc/detekt-api/io.gitlab.arturbosch.detekt.api/-console-report/index.html)'s.
The cli module and therefore the Gradle plugin implement a bunch of this reports.

A typical detekt report will look like following:

![report](images/howto-silent-run/typical_console_report.png)

There are many different parts which might or might not interest you.
If one part is not important to you, it can be excluded in the yaml configuration file.
A __silent__ configuration would exclude all possible processors and reports:
```yaml
processors:
  active: true
  exclude:
    - 'DetektProgressListener'
    - 'FunctionCountProcessor'
    - 'PropertyCountProcessor'
    - 'ClassCountProcessor'
    - 'PackageCountProcessor'
    - 'KtFileCountProcessor'

console-reports:
  active: true
  exclude:
    - 'ProjectStatisticsReport'
    - 'ComplexityReport'
    - 'NotificationReport'
    - 'FindingsReport'
    - 'BuildFailureReport'
```  

Running with this config won't produce any console messages: 

![report](images/howto-silent-run/silent_run.png)

Just verify that the `./report.txt` is not empty ;).

We might find detekt's `FindingsReport` too verbose and just want to print one message line per finding.
This can be achieved by implementing a custom `ConsoleReport`.

```kotlin
class SingleLineFindingsReport : ConsoleReport() {

    override fun render(detektion: Detektion): String? =
        detektion.findings.values
            .flatten()
            .joinToString("\n") { "${it.id} - ${it.message} - ${it.entity.location.file}" }
}
```

Combined with our silent configuration only messages are printed when findings are actually found:

![report](images/howto-silent-run/compact_report.png)

See the [extension](https://arturbosch.github.io/detekt/extensions.html) documention on how to let detekt know about your custom report.
