<@layout.admin "Edit Tag">
    <h1>Edit Tag</h1>
    
    <form id="saveTagForm" action="${links.getLinkToSaveTag()}" method="POST">
        <input type="hidden" name="id" value="${tag.id}">
        Name: <input type="text" name="name" value="${tag.name}">
        Parent ID (if applicable): <input type="test" name="parent" value="${tag.parent.id}">
    </form>
    
    <div id="saveResult" class="hidden"></div>
    
    <a href="javascript: saveTag();" class="button buttonPrimary">Save</a>
    <a href="${links.getLinkToManageTags()}" class="button">Cancel</a>
    
    <@util.js "admin/manage_tags" />
</@layout.admin>