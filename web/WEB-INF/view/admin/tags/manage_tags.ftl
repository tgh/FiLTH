<@layout.admin "Manage Tags">
    <h1>Manage Tags</h1>
    
    <table id="tagsTable">
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Parent Id</th>
            <th></th>
        </tr>
        <#foreach tag in tags>
            <#assign rowCssClass = "odd" />
            <#if tag_index % 2 == 0>
                <#assign rowCssClass = "even" />
            </#if>
        
            <tr class="${rowCssClass}" data-tag-id="${tag.id}">
                <td>${tag.id}</td>
                <td>${tag.name}</td>
                <#if tag.parent??>
                    <td>${tag.parent.id}</td>
                <#else>
                    <td></td>
                </#if>
                <td>
                    <a href="${links.getLinkToEditTag(tag.id)}" class="button">Edit</a>
                </td>
                <td>
                    <a href="javascript: deleteTag('${links.getLinkToDeleteTag(tag.id)}', ${tag.id});" class="button">Delete</a>
                </td>
            </tr>
        </#foreach>
    </table>
        
    <div id="saveResult" class="hidden"></div>
    
    <a href="${links.getLinkToCreateTag()}" class="button buttonPrimary">New</a>

    <@util.js "admin/manage_tags" />
</@layout.admin>

<#macro renderTagHierarchy tag indents>
    <#local rowCssClass = "odd" />
    <#if ROW_COUNT % 2 == 0>
        <#local rowCssClass = "even" />
    </#if>
    
    <tr data-tag-id="${tag.id}" class="${rowCssClass}">
        <#list 0..indents as i>
            <td></td>
        </#list>
        
        <td>${tag.id}</td>
        <td>${tag.name}</td>
        <td>
            <a href="${links.getLinkToEditTag(tag.id)}" class="button">Edit</a>
        </td>
        <td>
            <a href="javascript: deleteTag('${links.getLinkToDeleteTag(tag.id)}', ${tag.id});" class="button">Delete</a>
        </td>
    </tr>
    
    <#assign ROW_COUNT = ROW_COUNT + 1 />
    
    <#if tag.children??>
        <#foreach child in tag.children>
            <@renderTagHierarchy child indents+1 />
        </#foreach>
    </#if>
</#macro>