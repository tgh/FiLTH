<@layout.admin "Edit Tag">
    <h1>Edit Tag</h1>
    
    <form id="saveTagForm" action="${links.getLinkToSaveTag()}" method="POST">
        <input type="hidden" name="id" value="${tag.id}">
        <input type="text" name="name" value="${tag.name}">
    </form>
    
    <div id="saveResult" class="hidden"></div>
    
    <a href="javascript: saveTag();" class="button buttonPrimary">Save</a>
    <a href="${links.getLinkToManageTags()}" class="button">Cancel</a>
    
    <@util.js "admin/manage_tags" />
</@layout.admin>