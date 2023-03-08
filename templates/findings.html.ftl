<#macro content findings>
    <h2>Findings</h2>

    <span>Total ${findings.total}</span>

    <#list findings.groups as group>
        <h3>${group.name}: ${group?size}</h3>

        <#list group.rules as rule>
            <@_rule rule group/>
        </#list>
    </#list>

</#macro>

<#macro _rule rule group>
    <details id="${rule.name}" open="open">
        <summary class="rule-container">
            <span class="rule">${rule.name} ${rule.findings?size}</span>
            <span class="description">${rule.description}</span>
        </summary>

        <a href="${rule.documentationUrl}">Documentation</a>

        <ul>
            <#list rule.findings as finding>
                <@_finding finding/>
            </#list>
        </ul>
    </details>
</#macro>

<#macro _finding finding>
    <span class="location">${finding.path}:${finding.line}:${finding.column}</span>
    <span class="message">${finding.message}</span>

    ${finding}
</#macro>
