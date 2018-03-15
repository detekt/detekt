## Roadmap to 1.0.0

### todo

- rework gradle plugin to behave like other static analysis tools
- `no new complex rules` (maybe only some easy or contributed ones)

### done

- Overall improve documentation of detekt.
- We now have a website: arturbosch.github.io/detekt
- Refine threshold definition
- an output format (like checkstyle etc - thx to @winterDroid)
- Resolve `formatting` issues, integrate ktlint fixes OR `integrate ktlint` into detekt for formatting
- Resolve `java9` java.xml.bind issue
- `gradle-plugin` should support `profiles` for yaml configurations -> different rules for different source sets eg. test
- `jcenter` publishing
- Windows support!
- `sonar-plugin` (has it's own space [here](https://github.com/arturbosch/sonar-kotlin))
- Allow to exclude rules or rule sets for test sources

## Beyond 1.0.0

- figure out how kotlinc/intellij does type and symbol resolution
- finish `FeatureEnvy` rule -> needs type resolution

## Ideas for new major versions

- `idea-plugin`
- `jenkins-plugin`

## Won't do

- implement formatting rules, instead please use:
	- KtLint
	- IntelliJ's format.sh
	- or `detektIdeaFormat` and `detektIdeaInspect` wrapper over a local intellij
