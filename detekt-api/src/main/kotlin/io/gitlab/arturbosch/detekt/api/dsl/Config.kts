package io.gitlab.arturbosch.detekt.api.dsl

rules {
    ruleSet("comments") {
        active = false
        undocumentedPublicClass {
            active = true
            searchInInnerClass = false
        }
    }
}
