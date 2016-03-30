<@layout.admin "Manage Oscars">
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Oscars</h1>
    
    <#-- Oscars table -->
    <table id="oscarsTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Category</th>
                <th class="editColumn"></th>
                <th class="deleteColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach oscar in oscars>
                <#assign rowCssClass = "odd" />
                <#if oscar_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr class="${rowCssClass}" data-oscar-id="${oscar.id}">
                    <td class="oscarId">${oscar.id}</td>
                    <td class="oscarCategory">${oscar.category}</td>
                    <td>
                        <a data-remodal-target="editOscarModal" data-oscar-id="${oscar.id}" class="button editButton">Edit</a>
                    </td>
                    <td>
                        <a href="javascript: deleteOscar('${links.getLinkToDeleteOscar(oscar.id)}', ${oscar.id});" class="button deleteButton">Delete</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add oscar button-->
    <a id="addOscarButton" data-remodal-target="addOscarModal" class="button buttonPrimary">Add Oscar</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add oscar modal -->
    <div id="addOscarModal" class="remodal modal inputModal" data-remodal-id="addOscarModal" data-remodal-options="hashTracking: false">
        <h2>New Oscar</h2>
        
        <div class="modalInputContainer">
            <form id="addOscarForm" action="${links.getLinkToSaveOscar()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Category: </td>
                        <td><input id="addOscarCategoryInput" type="text" name="category" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: addOscar();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit oscar modal -->
    <div id="editOscarModal" class="remodal modal inputModal" data-remodal-id="editOscarModal" data-remodal-options="hashTracking: false">
        <h2>Edit Oscar</h2>
        
        <div class="modalInputContainer">
            <form id="editOscarForm" action="${links.getLinkToSaveOscar()}" method="POST">
                <input type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Category: </td>
                        <td><input id="editOscarCategoryInput" type="text" name="category" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: editOscar();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- pass along delete url to javascript (in order to setup appropriate Delete buttons when adding/editing oscars) -->
    <script type="text/javascript">
        var deleteUrl = '${links.getLinkToDeleteOscar()}';
    </script>
    

    <@util.js "admin/manage_oscars" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.admin>