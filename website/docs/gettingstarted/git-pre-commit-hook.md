---
title: "Run detekt using a Git pre-commit hook"
keywords: [detekt, static, analysis, code, kotlin]
sidebar: 
permalink: git-pre-commit-hook.html
folder: gettingstarted
summary:
sidebar_position: 6
---

detekt can be integrated into your development workflow by using a Git pre-commit hook.
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
  echo "                 detekt failed                 "
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

## Only run on staged files - Gradle

It is possible to configure Gradle to only run on staged files in pre-commit hook. 
This has the advantage of speedier execution, by running on fewer files and 
of lowered false positives by not scanning files that are not yet ready to be commited. 

First, we need to declare `GitPreCommitFilesTask` task - a gradle task that will retrieve list of staged files
in a configuration-cache compatible way. Paste following into your project's `build.gradle.kts`:

```kotlin
abstract class GitPreCommitFilesTask : DefaultTask() {
    @get:OutputFile
    abstract val gitStagedListFile: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val gitProcess =
            ProcessBuilder("git", "--no-pager", "diff", "--name-only", "--cached").start()
        val error = gitProcess.errorStream.readBytes().decodeToString()
        if (error.isNotBlank()) {
            error("Git error : $error")
        }

        val gitVersion = gitProcess.inputStream.readBytes().decodeToString().trim()

        gitStagedListFile.get().asFile.writeText(gitVersion)
    }
}

fun GitPreCommitFilesTask.getStagedFiles(rootDir: File): Provider<List<File>> {
    return gitStagedListFile.map {
        try {
            it.asFile.readLines()
                .filter { it.isNotBlank() }
                .map { File(rootDir, it) }
        } catch (e: FileNotFoundException) {
            // First build on configuration cache might fail
            // see https://github.com/gradle/gradle/issues/19252
            throw IllegalStateException(
                "Failed to load git configuration. " +
                    "Please disable configuration cache for the first commit and " +
                    "try again",
                e
            )
        }
    }
}

val gitPreCommitFileList = tasks.register<GitPreCommitFilesTask>("gitPreCommitFileList") {
    val targetFile = File(
        project.layout.buildDirectory.asFile.get(),
        "intermediates/gitPreCommitFileList/output"
    )

    targetFile.also {
        it.parentFile.mkdirs()
        gitStagedListFile.set(it)
    }
    outputs.upToDateWhen { false }
}
```

Then we need to configure `Detekt` task and change its `source` from the entire `src` foler (by default) to only set of
files that have been staged by git. Paste following into your project's `build.gradle.kts`::

```kotlin
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    if (project.hasProperty("precommit")) {
        dependsOn(gitPreCommitFileList)

        val rootDir = project.rootDir
        val projectDir = projectDir

        val fileCollection = files()

        setSource(
            gitPreCommitFileList.flatMap { task ->
                task.getStagedFiles(rootDir)
                    .map { stagedFiles ->
                        val stagedFilesFromThisProject = stagedFiles
                            .filter { it.startsWith(projectDir) }

                        fileCollection.setFrom(*stagedFilesFromThisProject.toTypedArray())

                        fileCollection.asFileTree
                    }
            }
        )
    }
}
```

Finally, we need to add `-Pprecommit=true` to the pre-commit script to tell Gradle to run detekt in "pre-commit mode".
For example, from above `detekt.sh` 

```bash
...
./gradlew -Pprecommit=true detekt > $OUTPUT
...
```

## Only run on staged files - CLI

It is also possible to use [the CLI](/docs/gettingstarted/cli) to create a hook that only runs on staged files. This has the advantage of speedier execution by avoiding the warm-up time of the gradle daemon.

Please note, however, that a handful of checks requiring [type resolution](/docs/gettingstarted/type-resolution) will not work correctly with this approach. If you do adopt a partial CLI hook, it is recommended that you still implement a full `detekt` check as part of your CI pipeline.

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
  echo "                 detekt failed                 "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $EXIT_CODE
fi
```
