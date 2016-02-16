<@layout.admin "Edit Oscar">
    <h1>Edit Oscar</h1>
    
    <form action="${rc.contextPath}/admin/oscars/save?${oscar.id}" method="POST">
        <input type="hidden" name="id" value="${oscar.id}">
        <input type="text" name="category" value="${oscar.category}">
    </form>
    
    <a href="javascript: document.forms[0].submit();" class="button buttonPrimary">Save</a>
    <a href="${rc.contextPath}/admin/oscars" class="button">Cancel</a>
</@layout.admin>