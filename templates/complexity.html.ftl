<#macro content complexityMetrics>
    <h2>Complexity Report</h2>

    <ul>
        <#list complexityMetrics as metric>
            <li>${metric}</li>
        </#list>
    </ul>

</#macro>
