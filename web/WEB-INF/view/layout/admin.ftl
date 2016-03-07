<#macro admin pageTitle="FiLTH Admin">
    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title>${pageTitle}</title>
            
            <link rel="shortcut icon" href="${rc.contextPath}/favicon.ico" type="image/x-icon" />
            
            <@util.css "standard" />
            <@util.css "admin" />
            <@util.css "lightbox" />
        </head>
        
        <body style="background: url(../images/backgrounds/${backgroundImageFilename}) no-repeat center center fixed; background-size: cover; -webkit-background-size: cover; -moz-background-size: cover; -o-background-size: cover;">
            <div id="rootContentContainer">
                <#nested>
            </div>
            
            <div id="footer">
                <p>Copyright 2009-2016 Film Library of Tyler Hayes | <span id="bgImageMovieTitle"></span> (<span id="bgImageMovieYear"></span>) | <a data-lightbox="background" href="../images/backgrounds/${backgroundImageFilename}">View background image</a></p>
            </div>
            
            <@util.js "third-party/jquery-2.2.0.min" />
            <@util.js "third-party/jquery.form.min" />
            <@util.js "global_constants" />
            <@util.js "global_functions" />
            <@util.js "third-party/lightbox.min" />
        </body>
    </html>
</#macro>