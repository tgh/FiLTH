<@layout.admin "Manage Oscars">
    <h1>Manage Oscars</h1>
    
    <table id="oscarsTable">
        <tr>
            <th>Id</th>
            <th>Category</th>
            <th></th>
        </tr>
        <#foreach oscar in oscars>
            <tr>
                <td>${oscar.id}</td>
                <td>${oscar.category}</td>
                <td>
                    <a href="${rc.contextPath}/admin/oscars/edit?id=${oscar.id}" class="button">Edit</a>
                </td>
                <td>
                    <form action="${rc.contextPath}/admin/oscars/delete?id=${oscar.id}" method="POST">
                        <input class="button" type="submit" value="Delete">
                    </form>
                </td>
            </tr>
        </#foreach>
        <tr>
            <td cellspacing="3">
                <a href="${rc.contextPath}/admin/oscars/create" class="button buttonPrimary">New</a>
            </td>
        </tr>
    </table>
</@layout.admin>