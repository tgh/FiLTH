<#assign cssRoot="/css">
<#assign jsRoot="/js">

<#macro css cssPath>
    <link rel="stylesheet" type="text/css" href="<@spring.url '${cssRoot}/${cssPath}'/>.css" />
</#macro>

<#macro js jsPath>
    <script type="text/javascript" src="<@spring.url '${jsRoot}/${jsPath}'/>.js"></script>
</#macro>