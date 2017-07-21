# Roadmap to 1.0.0

### todo
- `no new complex rules` (maybe only some easy or contributed ones)
- `Wiki` with documentation of the default rule set and/or CLI module provide a `documentation flag`

### done

- an output format (like checkstyle etc - thx to @winterDroid)
- Resolve `formatting` issues, integrate ktlint fixes OR `integrate ktlint` into detekt for formatting
- Resolve `java9` java.xml.bind issue
- `gradle-plugin` should support `profiles` for yaml configurations -> different rules for different source sets eg. test
- `jcenter` publishing
- Windows support!
- `sonar-plugin` (has it's own space [here](https://github.com/arturbosch/sonar-kotlin))

## Beyond 1.0.0

- finish `FeatureEnvy` rule -> needs type resolution
- figure out how kotlinc/intellij does type and symbol resolution

## For new major versions

- `idea-plugin`
- `jenkins-plugin`