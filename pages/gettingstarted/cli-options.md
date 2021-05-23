```
Usage: detekt [options]
  Options:
    --all-rules
      Activates all available (even unstable) rules.
      Default: false
    --auto-correct, -ac
      Allow rules to auto correct code if they support it. The default rule 
      sets do NOT support auto correcting and won't change any line in the 
      users code base. However custom rules can be written to support auto 
      correcting. The additional 'formatting' rule set, added with 
      '--plugins', does support it and needs this flag.
      Default: false
    --base-path, -bp
      Specifies a directory as the base path.Currently it impacts all file 
      paths in the formatted reports. File paths in console output and txt 
      report are not affected and remain as absolute paths.
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
      DEPRECATED: please use '--build-upon-default-config' together with 
      '--all-rules'. Same as 'build-upon-default-config' but explicitly 
      running all available rules. With this setting only exit code 0 is 
      returned when the analysis does not find a single code smell. Additional 
      configuration files can override rule properties which includes turning 
      off specific rules.
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
      (1.6, 1.8, 9, 10, 11, 12, 13, 14, 15 or 16)
      Default: JVM_1_8
      Possible Values: [JVM_1_6, JVM_1_8, JVM_9, JVM_10, JVM_11, JVM_12, JVM_13, JVM_14, JVM_15, JVM_16]
    --language-version
      EXPERIMENTAL: Compatibility mode for Kotlin language version X.Y, 
      reports errors for all language features that came out later (1.0, 1.1, 
      1.2, 1.3, 1.4 or 1.5)
      Possible Values: [1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6]
    --max-issues
      Return exit code 0 only when found issues count does not exceed 
      specified issues count.
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
      'txt', 'xml', 'html', 'sarif'. These can also be used in combination 
      with each other e.g. '-r txt:reports/detekt.txt -r 
      xml:reports/detekt.xml' 
    --version
      Prints the detekt CLI version.
      Default: false
```
