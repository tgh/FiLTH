<@layout.admin "Manage Tags">
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    
    <h1>Manage Tags</h1>
    
    <#-- Tag table -->
    <table id="tagsTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Parent Id</th>
                <th></th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <#foreach tag in tags>
                <#assign rowCssClass = "odd" />
                <#if tag_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
            
                <tr class="${rowCssClass}" data-tag-id="${tag.id}">
                    <td class="tagId">${tag.id}</td>
                    <td class="tagName">${tag.name}</td>
                    <#if tag.parent??>
                        <td class="parentId">${tag.parent.id}</td>
                    <#else>
                        <td class="parentId"></td>
                    </#if>
                    <td>
                        <a href="javascript: editTag(${tag.id})" data-remodal-target="editTagModal" data-tag-id="${tag.id}" class="button editButton">Edit</a>
                    </td>
                    <td>
                        <a href="javascript: deleteTag('${links.getLinkToDeleteTag(tag.id)}', ${tag.id});" class="button deleteButton">Delete</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add tag button-->
    <a data-remodal-target="addTagModal" class="button buttonPrimary" title="Add tag">+</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add tag modal -->
    <div id="addTagModal" class="remodal modal inputModal" data-remodal-id="addTagModal" data-remodal-options="hashTracking: false">
        <h2>New Tag</h2>
        
        <div class="modalInputContainer">
            <form id="addTagForm" action="${links.getLinkToSaveTag()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Name: </td>
                        <td><input type="text" name="name"></td>
                    </tr>
                    <tr>
                        <td class="label">Parent ID (if applicable): </td>
                        <td><input type="text" name="parent"></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: addTag();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit tag modal -->
    <div id="editTagModal" class="remodal modal inputModal" data-remodal-id="editTagModal" data-remodal-options="hashTracking: false">
        <h2>Edit Tag</h2>
        
        <div class="modalInputContainer">
            <form id="editTagForm" action="${links.getLinkToSaveTag()}" method="POST">
                <input type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Name: </td>
                        <td><input type="text" name="name"></td>
                    </tr>
                    <tr>
                        <td class="label">Parent ID (if applicable): </td>
                        <td><input type="text" name="parent"></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: editTag();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>

    <@util.js "admin/manage_tags" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
</@layout.admin>