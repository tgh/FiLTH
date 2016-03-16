<#assign cssRoot="/css">
<#assign jsRoot="/js">

<#macro css cssPath>
    <link rel="stylesheet" type="text/css" href="<@spring.url '${cssRoot}/${cssPath}'/>.css" />
</#macro>

<#macro external_css url>
    <link rel="stylesheet" type="text/css" href="${url}">
</#macro>

<#macro js jsPath>
    <script type="text/javascript" src="<@spring.url '${jsRoot}/${jsPath}'/>.js"></script>
</#macro>

<#macro external_js url charset="utf8">
    <script type="text/javascript" charset="${charset}" src="${url}"></script>
</#macro>

<#macro include_datatables_css>
    <@external_css "https://cdn.datatables.net/t/dt/dt-1.10.11/datatables.min.css"/>
</#macro>

<#macro include_datatables_js>
    <@external_js "https://cdn.datatables.net/t/dt/dt-1.10.11/datatables.min.js"/>
</#macro>