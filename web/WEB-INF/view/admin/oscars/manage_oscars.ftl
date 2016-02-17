<@layout.admin "Manage Oscars">
    <h1>Manage Oscars</h1>
    
    <table id="oscarsTable">
        <tr>
            <th>Id</th>
            <th>Category</th>
            <th></th>
        </tr>
        <#foreach oscar in oscars>
            <tr data-oscar-id="${oscar.id}">
                <td>${oscar.id}</td>
                <td>${oscar.category}</td>
                <td>
                    <a href="${rc.contextPath}/admin/oscars/edit?id=${oscar.id}" class="button">Edit</a>
                </td>
                <td>
                    <a href="javascript: deleteOscar('${rc.contextPath}/admin/oscars/delete?id=${oscar.id}', ${oscar.id});" class="button">Delete</a>
                </td>
            </tr>
        </#foreach>
    </table>
        
    <div id="saveResult" class="hidden"></div>
    
    <a href="${rc.contextPath}/admin/oscars/create" class="button buttonPrimary">New</a>

    <@util.js "admin/manage_oscars" />
</@layout.admin>