## detekt-watcher - continuous static analysis

Provides three commands:
- `project [path]` - sets the current project to analyze
- `analyze [subPath]?` - plain detekt run, super fast after first use
- `watch [start?|stop]` - watches path set by project command and runs detekt on each changed file

To view builtin ksh commands write `help`.
- `help [[commandId] [subCommandId]?]?` - get synopsis and option infos for specified commands

#### Home folder - ~/.detekt

##### config.properties

Configures the detekt watcher

- set `detekt.watcher.config.paths` to one or many detekt config files separated by a comma or semicolon. 

Example configuration file:
```
detekt.watcher.config.paths=/home/artur/.detekt/default.yml
detekt.watcher.change.notification=true
detekt.watcher.change.timeout=5
```
