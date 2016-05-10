<#assign cssRoot="/css">
<#assign jsRoot="/js">
<#assign imagesRoot="/images">

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

<#macro image imagePath cssClass title>
    <img class="${cssClass}" src="<@spring.url '${imagesRoot}/${imagePath}' />" title="${title}" />
</#macro>

<#macro noImage cssClass title>
    <img class="${cssClass}" src="<@spring.url '${imagesRoot}/no-image-old.jpg' />" title="${title}" />
</#macro>

<#macro include_datatables_css>
    <@external_css "https://cdn.datatables.net/t/dt/dt-1.10.11/datatables.min.css"/>
</#macro>

<#macro include_datatables_js>
    <@external_js "https://cdn.datatables.net/t/dt/dt-1.10.11/datatables.min.js"/>
</#macro>

<#--
  -- Given a number, return that number with the appropriate suffix.
  -- For example, if the number 3 is passed in, "3rd" is returned.
  -- Another example: if 77 is passed in, "77th" is returned. 
  -->
<#function number_suffix num>
    <#local numString = num?string />
    <#if (numString?ends_with("1") && !numString?ends_with("11"))>
        <#return "${numString}st" />
    <#elseif numString?ends_with("2")>
        <#return "${numString}nd" />
    <#elseif numString?ends_with("3")>
        <#return "${numString}rd" />
    <#else>
        <#return "${numString}th" />
    </#if>
</#function>