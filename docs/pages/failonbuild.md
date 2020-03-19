---
title: "Configure Build Failure Thresholds"
keywords: build fail thresholds
sidebar: 
permalink: failonbuild.html
summary:
---

_detekt_ supports the option to fail your build if a threshold of code smell issues is met.

For this the following code must be inside the detekt config:

```yaml
build:
  maxIssues: 10 # break the build if ten weighted issues are found
  weights:
    complexity: 2 # every rule of the complexity rule set should count as if two issues were found...
    LongParameterList: 1 # ...with the exception of the LongParameterList rule.
    comments: 0 # comment rules are just a nice to know?!
```

Every rule and rule set can be attached with an integer value which is the weight of the finding.
For example: If you have 5 findings of the category _complexity_, then your failThreshold of 10 is reached as
5 x 2 = 10. 

The formula for weights is: RuleID > RuleSetID > 1.

