<#macro standard pageTitle="FiLTH">

    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title>${pageTitle}</title>
            
            <@util.css "standard" />
        </head>
        
        <body>
            <div id="rootContainer">
                <#nested>
            </div>
        </body>
    </html>
</#macro>