<#macro admin pageTitle="FiLTH Admin">
    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title>${pageTitle}</title>
            
            <@util.css "standard" />
            <@util.css "admin" />
        </head>
        
        <body>
            <div id="rootContainer">
                <#nested>
            </div>
        </body>
    </html>
</#macro>