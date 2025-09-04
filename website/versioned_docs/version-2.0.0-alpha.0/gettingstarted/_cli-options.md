```
Usage: detekt [options] Options to pass to the Kotlin compiler.
  Options:
    --all-rules
      Activates all available (even unstable) rules.
      Default: false
    --analysis-mode
      Analysis mode used by detekt. 'full' analysis mode is comprehensive but 
      requires the correct compiler options to be provided. 'light' analysis 
      cannot utilise compiler information and some rules cannot be run in this 
      mode. 
      Default: light
      Possible Values: [full, light]
    --api-version
      Kotlin API version used by the code under analysis. Some rules use this 
      information to provide more specific rule violation messages.
    --auto-correct, -ac
      Allow rules to auto correct code if they support it. The default rule 
      sets do NOT support auto correcting and won't change any line in the 
      users code base. However custom rules can be written to support auto 
      correcting. The additional 'formatting' rule set, added with 
      '--plugins', does support it and needs this flag.
      Default: false
    --base-path, -bp
      Specifies a directory as the base path.Currently it impacts all file 
      paths in the formatted reports. File paths in console output are not 
      affected and remain as absolute paths.
      Default: /Users/ncor/oss/detekt/detekt-generator
    --baseline, -b
      If a baseline xml file is passed in, only new findings not in the 
      baseline are printed in the console.
    --build-upon-default-config
      Preconfigures detekt with a bunch of rules and some opinionated defaults 
      for you. Allows additional provided configurations to override the 
      defaults. 
      Default: false
    --classpath, -cp
      Paths where to find user class files and depending jar files. Used for 
      type resolution.
    --config, -c
      Path to the config file (path/to/config.yml). Multiple configuration 
      files can be specified with ',' or ';' as separator.
      Default: []
    --config-resource, -cr
      Path to the config resource on detekt's classpath (path/to/config.yml).
      Default: []
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
      Globbing patterns describing paths to exclude from the analysis.
    --fail-on-severity
      Specifies the minimum severity that causes the build to fail. When the 
      value is set to 'Never' detekt will not fail regardless of the number of 
      issues and their severities.
      Default: Error
      Possible Values: [Error, Warning, Info, Never]
    --generate-config, -gc
      Export default config to the provided path.
    --help, -h
      Shows the usage.
    --includes, -in
      Globbing patterns describing paths to include in the analysis. Useful in 
      combination with 'excludes' patterns.
    --input, -i
      Input paths to analyze. Multiple paths are separated by comma. If not 
      specified the current working directory is used.
      Default: [/Users/ncor/oss/detekt/detekt-generator]
    --jdk-home
      Use a custom JDK home directory to include into the classpath
    --jvm-target
      Target version of the generated JVM bytecode that was generated during 
      compilation and is now being used for type resolution
      Default: 1.8
      Possible Values: [1.6, 1.8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
    --language-version
      Compatibility mode for Kotlin language version X.Y, reports errors for 
      all language features that came out later
      Possible Values: [1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3]
    --parallel
      Enables parallel compilation and analysis of source files. Do some 
      benchmarks first before enabling this flag. Heuristics show performance 
      benefits starting from 2000 lines of Kotlin code.
      Default: false
    --plugins, -p
      Extra paths to plugin jars separated by ',' or ';'.
      Default: []
    --report, -r
      Generates a report for given 'report-id' and stores it on given 'path'. 
      Entry should consist of: [report-id:path]. Available 'report-id' values: 
      'xml', 'html', 'md', 'sarif'. These can also be used in combination with 
      each other e.g. '-r html:reports/detekt.html -r xml:reports/detekt.xml'
      Default: []
    --version
      Prints the detekt CLI version.
      Default: false

```
