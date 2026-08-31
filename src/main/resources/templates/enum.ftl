public enum ${enum.name} {
<#list enum.values as value>
    ${value}<#sep>, </#sep>
<#/list>
}