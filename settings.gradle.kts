rootProject.name = "detekt"
include("detekt-api",
		"detekt-core",
		"detekt-rules",
		"detekt-cli",
		"detekt-test",
		"detekt-sample-extensions",
		"detekt-generator",
		"detekt-watch-service",
		"detekt-formatting")

includeBuild("detekt-gradle-plugin")

pluginManagement {
	resolutionStrategy {
		eachPlugin {
			if(requested.id.id == "io.gitlab.arturbosch.detekt") {
				useModule("io.gitlab.arturbosch:detekt-gradle-plugin:1") // version ignored for composite build
			}
		}
	}
}
