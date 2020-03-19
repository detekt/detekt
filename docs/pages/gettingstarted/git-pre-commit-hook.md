---
title: "Run detekt using a Git pre-commit hook"
keywords: detekt static analysis code kotlin
sidebar: 
permalink: git-pre-commit-hook.html
folder: gettingstarted
summary:
---

Detekt can be integrated into your development workflow by using a Git pre-commit hook.
For that reason Git supports to run custom scripts automatically, when a specific action occurs.
The mentioned pre-commit hook can be setup locally on your dev-machine.
The following client-side detekt hook is triggered by a commit operation.

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
