<#-- @ftlvariable name="detektion" type="java.util.Map" -->
<#-- @ftlvariable name="metadata" type="java.util.Map" -->

<#-- @ftlroot "." -->

<#import "/css/base.css" as baseCss>
<#import "/css/fonts.css" as fontsCss>
<#import "metrics.html.ftl" as metricsFragment>
<#import "complexity.html.ftl" as complexityFragment>
<#import "findings.html.ftl" as findingsFragment>
<#import "footer.html.ftl" as footerFragment>

<!DOCTYPE html>
<html lang="en">
<head>
    <style><@fontsCss.googleFonts/></style>
    <style><@baseCss.style/></style>
    <meta charset="utf-8">
    <title>Detekt Report</title>
</head>
<body>

<#include 'svg/logo.svg'/>

<@metricsFragment.content detektion.metrics />

<@complexityFragment.content detektion.complexity />

<@findingsFragment.content detektion.findings />

<@footerFragment.content metadata/>

</body>
</html>
