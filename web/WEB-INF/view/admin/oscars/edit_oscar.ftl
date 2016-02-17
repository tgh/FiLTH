<@layout.admin "Edit Oscar">
    <h1>Edit Oscar</h1>
    
    <form id="saveOscarForm" action="${rc.contextPath}/admin/oscars/save?${oscar.id}" method="POST">
        <input type="hidden" name="id" value="${oscar.id}">
        <input type="text" name="category" value="${oscar.category}">
    </form>
    
    <div id="saveResult" class="hidden"></div>
    
    <a href="javascript: saveOscar();" class="button buttonPrimary">Save</a>
    <a href="${rc.contextPath}/admin/oscars" class="button">Cancel</a>
    
    <@util.js "admin/manage_oscars" />
</@layout.admin>