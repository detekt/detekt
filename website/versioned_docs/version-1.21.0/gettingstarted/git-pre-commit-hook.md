---
title: "Run detekt using a Git pre-commit hook"
keywords: [detekt, static, analysis, code, kotlin]
sidebar: 
permalink: git-pre-commit-hook.html
folder: gettingstarted
summary:
sidebar_position: 6
---

Detekt can be integrated into your development workflow by using a Git pre-commit hook.
For that reason Git supports to run custom scripts automatically, when a specific action occurs.
The mentioned pre-commit hook can be setup locally on your dev-machine.
The following client-side detekt hook is triggered by a commit operation, and checks all files via the gradle task.

```bash
#!/usr/bin/env bash
echo "Running detekt check..."
OUTPUT="/tmp/detekt-$(date +%s)"
./gradlew detekt > $OUTPUT
EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
  cat $OUTPUT
  rm $OUTPUT
  echo "***********************************************"
  echo "                 Detekt failed                 "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $EXIT_CODE
fi
rm $OUTPUT
```

The shell script can be installed by copying the content over to `<<your-repo>>/.git/hooks/pre-commit`.
This pre-commit hook needs to be executable, so you may need to change the permission (`chmod +x pre-commit`).
More information about Git hooks and how to install them can be found in 
[Atlassian's tutorial](https://www.atlassian.com/git/tutorials/git-hooks).

A special thanks goes to Mohit Sarveiya for providing this shell script.
You can watch his excellent talk about **Static Code Analysis For Kotlin** on 
[YouTube](https://www.youtube.com/watch?v=LT6m5_LO2DQ).

## Only run on staged files

It is also possible to use [the CLI](cli.mdx) to create a hook that only runs on staged files. This has the advantage of speedier execution, by running on fewer files and avoiding the warm-up time of the gradle daemon.

Please note, however, that a handful of checks requiring [type resolution](type-resolution.md) will not work correctly with this approach. If you do adopt a partial hook, it is recommended that you still implement a full `detekt` check as part of your CI pipeline.

This example has been put together using [pre-commit](https://pre-commit.com/), but the same principle can be applied to any kind of hook. 

Hook definition in pre-commit:

```yml
- id: detekt
  name: detekt check
  description: Runs `detekt` on modified .kt files.
  language: script
  entry: detekt.sh
  files: \.kt
```

Script `detekt.sh`:

```bash
#!/bin/bash

echo "Running detekt check..."
fileArray=($@)
detektInput=$(IFS=,;printf  "%s" "${fileArray[*]}")
echo "Input files: $detektInput"

OUTPUT=$(detekt --input "$detektInput" 2>&1)
EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
  echo $OUTPUT
  echo "***********************************************"
  echo "                 Detekt failed                 "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $EXIT_CODE
fi
```
