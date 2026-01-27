---
id: profiling
title: "Rule Execution Profiling"
keywords: [profiling, performance, optimization]
sidebar_position: 12
---

Rule execution profiling allows you to measure and analyze the time spent by each rule during code analysis. This is particularly useful for:

- **Identifying slow rules** that impact analysis performance
- **Optimizing custom rules** by comparing execution times
- **Understanding analysis bottlenecks** in large projects
- **Making informed decisions** about which rules to enable or disable

## How it Works

When profiling is enabled, detekt measures the execution time for each rule as it processes each file in your project. The profiling system:

1. **Tracks individual rule executions**: Records the time spent by each rule on each file
2. **Aggregates metrics**: Combines execution data to show total time, call counts, and averages
3. **Disables parallel execution**: Automatically runs analysis sequentially to ensure accurate timing measurements
4. **Generates reports**: Outputs detailed CSV files and formatted console summaries

## Using Profiling with Gradle

### Quick Start

The Gradle plugin automatically creates profiling tasks for your project:

```bash
# Profile all source sets
./gradlew detektProfile

# Profile main source set with type resolution
./gradlew detektProfileMain

# Profile test source set with type resolution
./gradlew detektProfileTest
```

In multi-project builds, running profiling tasks on the root project will automatically:
- Run profiling on all subprojects
- Aggregate results from all modules
- Display a combined profiling summary

:::note
Individual subproject profiling tasks have `printResults` set to `false` by default to avoid cluttering the console output. The aggregate task at the root level always displays results. If you need to see profiling results for a specific subproject, you can either run the task on that subproject directly or configure `printResults = true` on the task.
:::

### Available Tasks

The plugin creates the following profiling tasks:

- **`detektProfile`** - Runs detekt with profiling enabled on all source sets. Displays the top slowest rules and generates an aggregated profiling summary.
- **`detektProfileMain`** - Similar to `detektProfile`, but runs only on the `main` source set with type resolution enabled
- **`detektProfileTest`** - Similar to `detektProfile`, but runs only on the `test` source set with type resolution enabled
- **Android-only: `detektProfile<Variant>`** - Similar to `detektProfile`, but runs profiling with type resolution enabled only for the specific build variant

### Configuration

You can configure profiling behavior in your `build.gradle` or `build.gradle.kts`:

#### Kotlin DSL
```kotlin
detekt {
    // Number of slowest rules to display in profiling task console output.
    // Defaults to 10.
    topRulesToShow = 10
}

// Optionally configure individual profiling tasks
tasks.named<DetektProfilingTask>("detektProfile") {
    // Whether to print profiling results to the console.
    // Defaults to false for individual tasks to avoid cluttering output in multi-project builds.
    // Aggregate tasks always print results.
    printResults.set(true)
    
    // Override the number of top rules to show for this specific task
    topRulesToShow.set(15)
}
```

#### Groovy DSL
```groovy
detekt {
    // Number of slowest rules to display in profiling task console output.
    // Defaults to 10.
    topRulesToShow = 10
}

// Optionally configure individual profiling tasks
tasks.named('detektProfile') {
    // Whether to print profiling results to the console.
    // Defaults to false for individual tasks to avoid cluttering output in multi-project builds.
    // Aggregate tasks always print results.
    printResults = true
    
    // Override the number of top rules to show for this specific task
    topRulesToShow = 15
}
```

### Output

Profiling tasks generate two types of output:

#### 1. Console Report

A formatted table showing the top slowest rules:

```
Rule Execution Profile (Top 10, Aggregated from 3 sources):
=======================

Rule                                      Total    Calls        Avg Findings
--------------------------------------------------------------------------------
complexity:CyclomaticComplexMethod        2.35s      847     2.8ms      123
complexity:LongMethod                     1.82s      847     2.1ms       45
naming:FunctionNaming                     1.24s      847     1.5ms       78
style:MagicNumber                       987.3ms      847     1.2ms      234
...
--------------------------------------------------------------------------------
Total analysis time: 8.45s
Rules measured: 156
```

#### 2. CSV Report

A detailed CSV file for further analysis:

```
RuleSet,Rule,TotalDuration(ms),Calls,Findings
complexity,CyclomaticComplexMethod,2350,847,123
complexity,LongMethod,1820,847,45
naming,FunctionNaming,1240,847,78
...
```

The CSV file is saved to:
- Single project: `build/reports/detekt/<taskName>-profiling.csv`
- Multi-project (aggregated): `build/reports/detekt/<taskName>-profiling-aggregate.csv`

## Using Profiling with CLI

Enable profiling from the command line using the `--profiling` flag:

```bash
java -jar detekt-cli.jar \
  --input src/main/kotlin \
  --config config/detekt.yml \
  --profiling \
  --report profiling:profiling-output.csv
```

### CLI Options

- **`--profiling`** - Enables rule execution profiling. When profiling is enabled, parallel analysis is automatically disabled to ensure accurate measurements
- **`--report profiling:path/to/file.csv`** - Generates a detailed CSV report with per-rule-per-file execution data

The profiling CSV report contains the following columns:
- **RuleSet**: The rule set ID
- **Rule**: The rule ID  
- **File**: The file path that was analyzed
- **Duration(ms)**: Execution time in milliseconds
- **Findings**: Number of findings reported

## Interpreting Results

### Understanding Metrics

- **Total**: Total time spent executing a rule across all files
- **Calls**: Number of files the rule was executed against
- **Avg**: Average time per file (Total ÷ Calls)
- **Findings**: Total number of issues detected by the rule

### Performance Insights

Rules with high **total time** are the biggest contributors to analysis duration:
- Consider whether you need all such rules enabled
- Evaluate if rule configuration can be optimized (e.g., using excludes)
- Custom rules with high total time may benefit from optimization

Rules with high **average time** but low call counts might:
- Process complex files inefficiently
- Have algorithmic issues in their implementation
- Benefit from targeted optimization

### Important Notes

**Parallel Execution**: When profiling is enabled, parallel execution is automatically disabled (even if `--parallel` is specified). This ensures timing measurements are accurate and not influenced by concurrency overhead or resource contention.

**Overhead**: Profiling adds minimal overhead to analysis time (typically less than 1%). The measurements are collected efficiently using high-precision timers.

**Type Resolution**: Profiling works with both light analysis mode and full analysis mode (with type resolution). Type-resolution-based rules will typically show higher execution times.

## Programmatic Access

For advanced use cases, you can access profiling data programmatically through the detekt API:

```kotlin
// After running detekt with profiling enabled
val detektion: Detektion = // ... your Detektion result

// Access aggregated per-rule metrics
val ruleMetrics = detektion.userData[RuleProfilingKeys.RULE_METRICS] 
    as? List<RuleExecutionMetric>

// Access detailed per-rule-per-file executions
val fileExecutions = detektion.userData[RuleProfilingKeys.FILE_EXECUTIONS]
    as? List<RuleFileExecution>

// Check if parallel execution was disabled for profiling
val parallelDisabled = detektion.userData[RuleProfilingKeys.PARALLEL_DISABLED] 
    as? Boolean
```

## Use Cases

### Optimizing Analysis Time

Profile your project to identify which rules take the most time:

```bash
./gradlew detektProfile
```

Review the output to find rules with high total execution time. Consider:
- Disabling rules that provide low value for the time cost
- Adding path exclusions to skip irrelevant files
- Optimizing custom rule implementations

### Benchmarking Custom Rules

If you've implemented custom rules, use profiling to ensure they perform well:

```bash
./gradlew detektProfile
```

Compare your custom rules' execution times against built-in rules to identify performance issues.

### Multi-Module Analysis

In multi-module projects, run profiling from the root to get aggregated insights:

```bash
# Aggregate profiling across all modules
./gradlew detektProfileMain
```

This shows which rules are slowest across your entire codebase, helping you make informed decisions about rule configuration.
