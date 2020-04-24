---
title: "Run detekt using Command Line Interface"
keywords: cli
sidebar: 
permalink: cli.html
folder: gettingstarted
summary:
---

# Install the cli

There are different ways to install the Command Line Interface (CLI):

### MacOS, with [Homebrew](https://brew.sh/):
```sh
brew install detekt
detekt [options]
```

### Unix, with the stand-alone executable:
```sh
curl -sSLO https://github.com/detekt/detekt/releases/download/v{{ site.detekt_version }}/detekt && chmod a+x detekt
./detekt [options]
```
You can add this file to your `PATH` so you can use it like `detekt [options]`.
For example, like this: `mv detekt /var/local/bin`

### Any OS:
```sh
curl -sSLO https://github.com/detekt/detekt/releases/download/v{{ site.detekt_version }}/detekt-cli-{{ site.detekt_version }}.zip && unzip detekt-cli-{{ site.detekt_version }}.zip
./bin/detekt [options]
```

# Use the cli

detekt will exit with one of the following exit codes:

| Exit code | Description                                                                    |
|-----------|--------------------------------------------------------------------------------|
| 0         | detekt ran normally and maxIssues count was not reached in BuildFailureReport. |
| 1         | An unexpected error occurred                                                   |
| 2         | MaxIssues count was reached in BuildFailureReport.                             |
| 3         | Invalid detekt configuration file detected.                                    |

The following parameters are shown when `--help` is entered.

```
Usage: detekt [options]
  Options:
    --auto-correct, -ac
      Allow rules to auto correct code if they support it. The default rule
      sets do NOT support auto correcting and won't change any line in the
      users code base. However custom rules can be written to support auto
      correcting. The additional 'formatting' rule set, added with
      '--plugins', does support it and needs this flag.
      Default: false
    --baseline, -b
      If a baseline xml file is passed in, only new code smells not in the
      baseline are printed in the console.
    --build-upon-default-config
      Preconfigures detekt with a bunch of rules and some opinionated defaults
      for you. Allows additional provided configurations to override the
      defaults.
      Default: false
    --classpath, -cp
      EXPERIMENTAL: Paths where to find user class files and depending jar
      files. Used for type resolution.
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
    --excludes, -ex
      Globing patterns describing paths to exclude from the analysis.
    --fail-fast
      Same as 'build-upon-default-config' but explicitly running all available
      rules. With this setting only exit code 0 is returned when the analysis
      does not find a single code smell. Additional configuration files can
      override rule properties which includes turning off specific rules.
      Default: false
    --generate-config, -gc
      Export default config. Path can be specified with --config option
      (default path: default-detekt-config.yml)
      Default: false
    --help, -h
      Shows the usage.
    --includes, -in
      Globing patterns describing paths to include in the analysis. Useful in
      combination with 'excludes' patterns.
    --input, -i
      Input paths to analyze. Multiple paths are separated by comma. If not
      specified the current working directory is used.
    --jvm-target
      EXPERIMENTAL: Target version of the generated JVM bytecode that was
      generated during compilation and is now being used for type resolution
      (1.6, 1.8, 9, 10, 11 or 12)
      Default: JVM_1_6
      Possible Values: [JVM_1_6, JVM_1_8, JVM_9, JVM_10, JVM_11, JVM_12, JVM_13]
    --language-version
      EXPERIMENTAL: Compatibility mode for Kotlin language version X.Y,
      reports errors for all language features that came out later (1.0, 1.1,
      1.2, 1.3, 1.4)
      Possible Values: [1.0, 1.1, 1.2, 1.3, 1.4]
    --parallel
      Enables parallel compilation and analysis of source files. Do some
      benchmarks first before enabling this flag. Heuristics show performance
      benefits starting from 2000 lines of Kotlin code.
      Default: false
    --plugins, -p
      Extra paths to plugin jars separated by ',' or ';'.
    --report, -r
      Generates a report for given 'report-id' and stores it on given 'path'.
      Entry should consist of: [report-id:path]. Available 'report-id' values:
      'txt', 'xml', 'html'. These can also be used in combination with each
      other e.g. '-r txt:reports/detekt.txt -r xml:reports/detekt.xml'
    --version
      Prints the detekt CLI version.
      Default: false
```
