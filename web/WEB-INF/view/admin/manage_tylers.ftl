<@layout.standard "FiLTH Admin: Manage Tylers">
    <@util.css "admin" />
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Tylers</h1>
    
    <#-- Tylers table -->
    <table id="tylerTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Category</th>
                <th class="editColumn"></th>
                <th class="deleteColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach tyler in tylers>
                <#assign rowCssClass = "odd" />
                <#if tyler_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr class="${rowCssClass}" data-tyler-id="${tyler.id}">
                    <td class="tylerId">${tyler.id}</td>
                    <td class="tylerCategory">${tyler.category}</td>
                    <td>
                        <a data-remodal-target="editTylerModal" data-tyler-id="${tyler.id}" class="button editButton">Edit</a>
                    </td>
                    <td>
                        <a href="javascript: tylerManager.deleteEntity('${links.getLinkToDeleteTyler(tyler.id)}', ${tyler.id});" class="button deleteButton">Delete</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add tyler button-->
    <a data-remodal-target="addTylerModal" class="addButton button">Add Tyler</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add tyler modal -->
    <div id="addTylerModal" class="remodal modal inputModal" data-remodal-id="addTylerModal" data-remodal-options="hashTracking: false">
        <h2>New Tyler</h2>
        
        <div class="modalInputContainer">
            <form id="addTylerForm" action="${links.getLinkToSaveTyler()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Category: </td>
                        <td><input id="addTylerCategoryInput" type="text" name="category" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: tylerManager.addTyler();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit tyler modal -->
    <div id="editTylerModal" class="remodal modal inputModal" data-remodal-id="editTylerModal" data-remodal-options="hashTracking: false">
        <h2>Edit Tyler</h2>
        
        <div class="modalInputContainer">
            <form id="editTylerForm" action="${links.getLinkToSaveTyler()}" method="POST">
                <input id="editTylerIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Category: </td>
                        <td><input id="editTylerCategoryInput" type="text" name="category" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: tylerManager.editTyler();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- pass along delete url to javascript (in order to setup appropriate Delete buttons when adding/editing tylers) -->
    <script type="text/javascript">
        var deleteUrl = '${links.getLinkToDeleteTyler()}';
    </script>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/TylerManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.standard>