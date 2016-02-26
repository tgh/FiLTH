<#macro standard pageTitle="FiLTH">
    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title>${pageTitle}</title>
            
            <link rel="shortcut icon" href="${rc.contextPath}/favicon.ico" type="image/x-icon" />
            
            <@util.css "standard" />
        </head>
        
        <body>
            <div id="rootContainer">
                <#nested>
            </div>
            
            <@util.js "third-party/jquery-2.2.0.min" />
            <@util.js "third-party/jquery.form.min" />
            <@util.js "global_constants" />
            <@util.js "global_functions" />
        </body>
    </html>
</#macro>