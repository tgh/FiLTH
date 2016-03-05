<@layout.admin "Manage Oscars">
    <h1>Manage Oscars</h1>
    
    <table id="oscarsTable">
        <tr>
            <th>Id</th>
            <th>Category</th>
            <th></th>
        </tr>
        <#foreach oscar in oscars>
            <#assign rowCssClass = "odd" />
            <#if oscar_index % 2 == 0>
                <#assign rowCssClass = "even" />
            </#if>
            
            <tr data-oscar-id="${oscar.id}" class="${rowCssClass}">
                <td>${oscar.id}</td>
                <td>${oscar.category}</td>
                <td>
                    <a href="${links.getLinkToEditOscar(oscar.id)}" class="button">Edit</a>
                </td>
                <td>
                    <a href="javascript: deleteOscar('${links.getLinkToDeleteOscar(oscar.id)}', ${oscar.id});" class="button">Delete</a>
                </td>
            </tr>
        </#foreach>
    </table>
        
    <div id="saveResult" class="hidden"></div>
    
    <a href="${links.getLinkToCreateOscar()}" class="button buttonPrimary">New</a>

    <@util.js "admin/manage_oscars" />
</@layout.admin>