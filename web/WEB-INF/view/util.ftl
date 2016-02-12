<#assign cssRoot="/css">

<#macro css cssPath>
    <link rel="stylesheet" type="text/css" href="<@spring.url '${cssRoot}/${cssPath}'/>.css" />
</#macro>