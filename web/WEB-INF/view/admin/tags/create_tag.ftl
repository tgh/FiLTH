<@layout.admin "Create Tag">
    <h1>Create Tag</h1>
    
    <form id="saveTagForm" action="${links.getLinkToSaveTag()}" method="POST">
        <input type="text" name="name">
    </form>
    
    <div id="saveResult" class="hidden"></div>
    
    <a href="javascript: saveTag();" class="button buttonPrimary">Save</a>
    <a href="${links.getLinkToManageTags()}" class="button">Cancel</a>
    
    <@util.js "admin/manage_tags" />
</@layout.admin>