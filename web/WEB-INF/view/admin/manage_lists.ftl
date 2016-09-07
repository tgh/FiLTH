<@layout.standard "FiLTH Admin: Manage Lists">
    <@util.css "admin" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    
    <h1>Manage Lists</h1>
    
    <#-- Lists table -->
    <table id="listTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Author</th>
                <th class="deleteColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach list in lists>
                <#assign rowCssClass = "odd" />
                <#if list_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <#if list.author??>
                    <#assign author = list.author />
                <#else>
                    <#assign author = '' />
                </#if>
                
                <tr class="${rowCssClass}" data-list-id="${list.id}">
                    <td class="listId">${list.id}</td>
                    <td class="listTitle listLink"><a href="${links.getLinkToList(list.id)}">${list.title}</a></td>
                    <td class="listAuthor">${author}</td>
                    <td>
                        <a href="javascript: listManager.deleteEntity('${links.getLinkToDeleteList(list.id)}', ${list.id});" class="button deleteButton">Delete</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add list button-->
    <a href="${links.getLinkToNewList()}" class="addButton button">Create List</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- pass along delete url to javascript (in order to setup appropriate Delete buttons when adding/editing lists) -->
    <script type="text/javascript">
        var deleteUrl = '${links.getLinkToDeleteList()}';
    </script>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/ListManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
</@layout.standard>