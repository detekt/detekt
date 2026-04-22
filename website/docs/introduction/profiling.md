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

When the `detekt-profiler` plugin is on the classpath, detekt measures the execution time for each rule as it processes each file in your project. The profiling system:

1. **Tracks individual rule executions**: Records the time spent by each rule on each file
2. **Aggregates metrics**: Combines execution data to show total time, call counts, and averages
3. **Generates reports**: Outputs detailed CSV files via the `profiling` report type

Profiling is enabled automatically when the `detekt-profiler` module is present on the plugin classpath. No additional flags or separate tasks are needed — just add the dependency and run your normal detekt tasks.

## Using Profiling with Gradle

### Quick Start

Add the `detekt-profiler` module to your `detektPlugins` configuration:

```kotlin
dependencies {
    detektPlugins("dev.detekt:detekt-profiler:[detekt_version]")
}
```

Then run any detekt task as usual:

```bash
# Profile all source sets
./gradlew detekt

# Profile main source set with type resolution
./gradlew detektMain

# Profile test source set with type resolution
./gradlew detektTest
```

The profiling output report will be generated alongside your other detekt reports.

### Output

#### CSV Report

A detailed CSV file for further analysis:

```
RuleSet,Rule,File,Duration(ms),Findings
complexity,CyclomaticComplexMethod,src/main/kotlin/Foo.kt,12,1
complexity,LongMethod,src/main/kotlin/Foo.kt,8,0
naming,FunctionNaming,src/main/kotlin/Foo.kt,3,2
...
```

## Using Profiling with CLI

Enable profiling from the command line by adding the `detekt-profiler` module as a plugin:

```bash
java -jar detekt-cli.jar \
  --input src/main/kotlin \
  --config config/detekt.yml \
  --plugins path/to/detekt-profiler.jar \
  --report profiling:profiling-output.csv
```

### CLI Options

- **`--plugins path/to/detekt-profiler.jar`** - Adds the profiler module to the classpath, which enables rule execution profiling
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
- **Avg**: Average time per file (Total / Calls)
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

**Parallel Execution**: Profiling works with both sequential and parallel execution modes. When running in parallel, be aware that per-rule timing measurements may be influenced by concurrency overhead and resource contention.

**Overhead**: Profiling adds minimal overhead to analysis time (typically less than 1%). The measurements are collected efficiently using high-precision timers.

**Type Resolution**: Profiling works with both light analysis mode and full analysis mode (with type resolution). Type-resolution-based rules will typically show higher execution times.

## Programmatic Access

For advanced use cases, you can access profiling data programmatically through the detekt API:

```kotlin
// After running detekt with detekt-profiler on the classpath
val detektion: Detektion = // ... your Detektion result

// Access aggregated per-rule metrics
val ruleMetrics = detektion.userData["ruleExecutionMetrics"]
    as? List<RuleExecutionMetric>

// Access detailed per-rule-per-file executions
val fileExecutions = detektion.userData["ruleFileExecutions"]
    as? List<RuleFileExecution>
```

## Use Cases

### Optimizing Analysis Time

Profile your project to identify which rules take the most time:

```bash
./gradlew detekt
```

Review the output to find rules with high total execution time. Consider:
- Disabling rules that provide low value for the time cost
- Adding path exclusions to skip irrelevant files
- Optimizing custom rule implementations

### Benchmarking Custom Rules

If you've implemented custom rules, use profiling to ensure they perform well:

```bash
./gradlew detektMain
```

Compare your custom rules' execution times against built-in rules to identify performance issues.
