## detekt-watcher - continuous static analysis

Provides three commands:
- `project [path]` - sets the current project to analyze
- `analyze [subPath]?` - plain detekt run, super fast after first use
- `watch [start?|stop]` - watches path set by project command and runs detekt on each changed file
