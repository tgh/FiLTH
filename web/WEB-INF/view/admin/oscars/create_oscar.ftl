<@layout.admin "Create Oscar">
    <h1>Create Oscar</h1>
    
    <form id="saveOscarForm" action="${rc.contextPath}/admin/oscars/save" method="POST">
        <input type="text" name="category">
    </form>
    
    <div id="saveResult" class="hidden"></div>
    
    <a href="javascript: saveOscar();" class="button buttonPrimary">Save</a>
    <a href="${rc.contextPath}/admin/oscars" class="button">Cancel</a>
    
    <@util.js "admin/manage_oscars" />
</@layout.admin>