rootProject.name = "detekt"
include("detekt-api",
		"detekt-core",
		"detekt-rules",
		"detekt-cli",
		"detekt-test",
		"detekt-sample-extensions",
		"detekt-generator",
		"detekt-watcher",
		"detekt-formatting")

includeBuild("detekt-gradle-plugin")
