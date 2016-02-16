<@layout.admin "Create Oscar">
    <h1>Create Oscar</h1>
    
    <form action="${rc.contextPath}/admin/oscars/save" method="POST">
        <input type="text" name="category">
    </form>
    
    <a href="javascript: document.forms[0].submit();" class="button buttonPrimary">Save</a>
    <a href="${rc.contextPath}/admin/oscars" class="button">Cancel</a>
</@layout.admin>