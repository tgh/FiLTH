<@layout.standard "FiLTH Admin: Manage Lists">
    <@util.css "admin" />
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Lists</h1>
    
    <#-- Lists table -->
    <table id="listTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Author</th>
                <th class="editColumn"></th>
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
                        <a data-remodal-target="editListModal" data-list-id="${list.id}" class="button editButton">Edit</a>
                    </td>
                    <td>
                        <a href="javascript: listManager.deleteEntity('${links.getLinkToDeleteList(list.id)}', ${list.id});" class="button deleteButton">Delete</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add list button-->
    <a data-remodal-target="addListModal" class="addButton button">Add List</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add list modal -->
    <div id="addListModal" class="remodal modal inputModal" data-remodal-id="addListModal" data-remodal-options="hashTracking: false">
        <h2>New List</h2>
        
        <div class="modalInputContainer">
            <form id="addListForm" action="${links.getLinkToSaveList()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Title: </td>
                        <td><input id="addListTitleInput" type="text" name="title" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Author (if applicable): </td>
                        <td><input id="addListAuthorInput" type="text" name="author"></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: listManager.addList();" class="button modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit list modal -->
    <div id="editListModal" class="remodal modal inputModal" data-remodal-id="editListModal" data-remodal-options="hashTracking: false">
        <h2>Edit List</h2>
        
        <div class="modalInputContainer">
            <form id="editListForm" action="${links.getLinkToSaveList()}" method="POST">
                <input id="editListIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Title: </td>
                        <td><input id="editListTitleInput" type="text" name="title" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Author (if applicable): </td>
                        <td><input id="editListAuthorInput" type="text" name="author"></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: listManager.editList();" class="button modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- pass along delete url to javascript (in order to setup appropriate Delete buttons when adding/editing lists) -->
    <script type="text/javascript">
        var deleteUrl = '${links.getLinkToDeleteList()}';
    </script>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/ListManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.standard>