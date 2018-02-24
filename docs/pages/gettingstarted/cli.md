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

The following parameters are shown when `--help` is entered. The `--input`/`-i` option is required:

```
Usage: detekt [options]
  Options:
    --baseline, -b
      If a baseline xml file is passed in, only new code smells not in the 
      baseline are printed in the console.
    --config, -c
      Path to the config file (path/to/config.yml).
    --config-resource, -cr
      Path to the config resource on detekt's classpath (path/to/config.yml).
    --create-baseline, -cb
      Treats current analysis findings as a smell baseline for future detekt 
      runs. 
      Default: false
    --debug
      Debugs given ktFile by printing its elements.
      Default: false
    --disable-default-rulesets, -dd
      Disables default rule sets.
      Default: false
    --filters, -f
      Path filters defined through regex with separator ';' (".*test.*").
    --generate-config, -gc
      Export default config to default-detekt-config.yml.
      Default: false
    --help, -h
      Shows the usage.
  * --input, -i
      Input paths to analyze.
    --output, -o
      Directory where output reports are stored.
    --output-name, -on
      The base name for output reports is derived from this parameter.
    --parallel
      Enables parallel compilation of source files. Should only be used if the 
      analyzing project has more than ~200 kotlin files.
      Default: false
    --plugins, -p
      Extra paths to plugin jars separated by ',' or ';'.
```
