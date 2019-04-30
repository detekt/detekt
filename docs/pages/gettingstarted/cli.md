---
title: "Run detekt using Command Line Interface"
keywords: cli
tags: [getting_started, cli]
sidebar: 
permalink: cli.html
folder: gettingstarted
summary:
---

1. `cd detekt`
2. `gradle build`
3. `java -jar detekt-cli/build/libs/detekt-cli-[version]-all.jar [parameters]*`

detekt will exit with one of the following exit codes:

| Exit code | Description                                                                                     |
|-----------|-------------------------------------------------------------------------------------------------|
| 0         | detekt ran normally and maxIssues or failThreshold count was not reached in BuildFailureReport. |
| 1         | An unexpected error occurred                                                                    |
| 2         | maxIssues or failThreshold count was reached in BuildFailureReport.                             |

The following parameters are shown when `--help` is entered.

```
Usage: detekt [options]
  Options:
    --baseline, -b
      If a baseline xml file is passed in, only new code smells not in the
      baseline are printed in the console.
    --config, -c
      Path to the config file (path/to/config.yml). Multiple configuration
      files can be specified with ',' or ';' as separator.
    --config-resource, -cr
      Path to the config resource on detekt's classpath (path/to/config.yml).
    --create-baseline, -cb
      Treats current analysis findings as a smell baseline for future detekt
      runs.
      Default: false
    --debug
      Prints extra information about configurations and extensions.
      Default: false
    --disable-default-rulesets, -dd
      Disables default rule sets.
      Default: false
    --build-upon-default-config
      Apply values from config files as changes to the default configuration
      (instead of starting from an empty configuration).
      Default: false
    --fail-fast
      Shortcut for 'build-upon-default-config' together with all available rules active 
      and exit code 0 only when no code smells are found.
      Additional configuration files can override properties but not the 'active' one.
    --filters, -f
      Path filters defined through regex with separator ';' or ','
      (".*test.*"). These filters apply on relative paths from the project
      root.
    --generate-config, -gc
      Export default config to default-detekt-config.yml.
      Default: false
    --help, -h
      Shows the usage.
    --input, -i
      Input paths to analyze. Multiple paths are separated by comma. If not
      specified the current working directory is used.
    --parallel
      Enables parallel compilation of source files. Should only be used if the
      analyzing project has more than ~200 kotlin files.
      Default: false
    --plugins, -p
      Extra paths to plugin jars separated by ',' or ';'.
    --report, -r
      Generates a report for given 'report-id' and stores it on given 'path'.
      Entry should consist of: [report-id:path]. Available 'report-id' values:
      'txt', 'xml', 'html'. These can also be used in combination with each
      other e.g. '-r txt:reports/detekt.txt -r xml:reports/detekt.xml'

```
