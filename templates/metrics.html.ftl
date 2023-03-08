<#macro content metrics>
    <h2>Complexity Report</h2>

    <ul>
        <#list metrics as metric>
            <li>${metric.value} - ${metric.type}</li>
        </#list>
    </ul>

</#macro>
