<@layout.admin "Manage Tags">
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    
    <h1>Manage Tags</h1>
    
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
        </tbody>
    </table>
    
    <a data-remodal-target="addTagModal" class="button buttonPrimary" title="Add tag">+</a>
    
    <div id="addTagContainer" class="remodal modal inputModal" data-remodal-id="addTagModal" data-remodal-options="hashTracking: false">
        <h2>New Tag</h2>
        
        <div class="modalInputContainer">
            <form id="saveTagForm" action="${links.getLinkToSaveTag()}" method="POST">
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
        
    <div id="stackTraceContainer" class="hidden error"></div>

    <@util.js "admin/manage_tags" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
</@layout.admin>